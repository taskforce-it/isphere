/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.FieldDescription;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.file.description.RecordFormatDescription;
import biz.isphere.core.file.description.RecordFormatDescriptionsStore;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;

public class CopyMemberValidator extends Thread {

    public enum MemberValidationError {
        ERROR_NONE,
        ERROR_TO_CONNECTION,
        ERROR_TO_FILE,
        ERROR_TO_LIBRARY,
        ERROR_TO_MEMBER,
        ERROR_CANCELED,
        ERROR_EXCEPTION;
    }

    private DoValidateMembers doValidateMembers;
    private IValidateMembersPostRun postRun;

    private MemberValidationError errorId;
    private String errorMessage;

    private boolean isActive;

    public CopyMemberValidator(String fromConnectionName, CopyMemberItem[] fromMembers, ExistingMemberAction existingMemberAction,
        boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck, IValidateMembersPostRun postRun) {
        doValidateMembers = new DoValidateMembers(fromConnectionName, fromMembers, existingMemberAction, ignoreDataLostError,
            ignoreUnsavedChangesError, fullErrorCheck);
        this.postRun = postRun;
    }

    public void setToConnectionName(String connectionName) {
        doValidateMembers.setToConnectionName(connectionName);
    }

    public void setToLibraryName(String libraryName) {
        doValidateMembers.setToLibraryName(libraryName);
    }

    public void setToFileName(String fileName) {
        doValidateMembers.setToFileName(fileName);
    }

    public void setToCcsid(int toCcsid) {
        doValidateMembers.setToCcsid(toCcsid);
    }

    public void runInSameThread(IProgressMonitor monitor) {
        doValidateMembers.setMonitor(monitor);
        startProcess();
        doValidateMembers.run();
        postRun.returnResult(errorId, errorMessage);
        endProcess();
    }

    public void addItemErrorListener(IItemErrorListener listener) {
        doValidateMembers.addItemErrorListener(listener);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void run() {

        startProcess();

        try {

            doValidateMembers.start();

            while (doValidateMembers.isAlive()) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

            }

        } finally {
            postRun.returnResult(errorId, errorMessage);
            endProcess();
        }

    }

    public void cancelOperation() {
        if (doValidateMembers != null) {
            doValidateMembers.cancel();
        }
    }

    private void startProcess() {
        isActive = true;
    }

    private void endProcess() {
        isActive = false;
    }

    public boolean isCanceled() {
        return doValidateMembers.isCanceled();
    }

    private class DoValidateMembers extends Thread {

        private Set<IItemErrorListener> itemErrorListeners;

        private String fromConnectionName;
        private CopyMemberItem[] fromMembers;
        private ExistingMemberAction existingMemberAction;
        private boolean ignoreDataLostError;
        private boolean ignoreUnsavedChangesError;
        private boolean fullErrorCheck;

        private IProgressMonitor monitor;

        private boolean isCanceled;

        private String toConnectionName;
        private String toLibraryName;
        private String toFileName;
        private int toCcsid;

        public DoValidateMembers(String fromConnectionName, CopyMemberItem[] fromMembers, ExistingMemberAction existingMemberAction,
            boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck) {

            this.itemErrorListeners = new HashSet<IItemErrorListener>();

            this.fromConnectionName = fromConnectionName;
            this.fromMembers = fromMembers;
            this.existingMemberAction = existingMemberAction;
            this.ignoreDataLostError = ignoreDataLostError;
            this.ignoreUnsavedChangesError = ignoreUnsavedChangesError;
            this.fullErrorCheck = fullErrorCheck;
            this.isCanceled = false;
        }

        public void addItemErrorListener(IItemErrorListener listener) {
            itemErrorListeners.add(listener);
        }

        public void setToConnectionName(String connectionName) {
            this.toConnectionName = connectionName;
        }

        public void setToLibraryName(String libraryName) {
            this.toLibraryName = libraryName;
        }

        public void setToFileName(String fileName) {
            this.toFileName = fileName;
        }

        public void setToCcsid(int toCcsid) {
            this.toCcsid = toCcsid;
        }

        public void setMonitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {

            errorId = MemberValidationError.ERROR_NONE;
            errorMessage = null;

            if (!isCanceled() && errorId == MemberValidationError.ERROR_NONE) {
                validateTargetFile(toConnectionName, toLibraryName, toFileName);
            }

            if (!isCanceled() && errorId == MemberValidationError.ERROR_NONE) {
                validateMembers(fromConnectionName, toConnectionName, existingMemberAction, ignoreDataLostError, ignoreUnsavedChangesError,
                    fullErrorCheck);
            }

            if (isCanceled()) {
                errorId = MemberValidationError.ERROR_CANCELED;
                errorMessage = Messages.Operation_has_been_canceled_by_the_user;
            }
        }

        private void setMemberError(CopyMemberItem member, String errorMessage) {

            member.setErrorMessage(errorMessage);

            if (itemErrorListeners != null) {
                for (IItemErrorListener errorListener : itemErrorListeners) {
                    errorListener.reportError(member, errorMessage);
                }
            }
        }

        public void cancel() {
            isCanceled = true;
        }

        private boolean validateTargetFile(String toConnectionName, String toLibrary, String toFile) {

            Validator nameValidator = Validator.getNameInstance(toCcsid);

            boolean isError = false;

            if (!hasConnection(toConnectionName)) {
                errorId = MemberValidationError.ERROR_TO_CONNECTION;
                errorMessage = Messages.bind(Messages.Connection_A_not_found, toConnectionName);
                isError = true;
            } else if (!nameValidator.validate(toLibrary)) {
                errorId = MemberValidationError.ERROR_TO_LIBRARY;
                errorMessage = Messages.bind(Messages.Invalid_library_name, toLibrary);
                isError = true;
            } else {
                AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);
                if (!ISphereHelper.checkLibrary(toSystem, toLibrary)) {
                    errorId = MemberValidationError.ERROR_TO_LIBRARY;
                    errorMessage = Messages.bind(Messages.Library_A_not_found, toLibrary);
                    isError = true;
                } else if (!nameValidator.validate(toFile)) {
                    errorId = MemberValidationError.ERROR_TO_FILE;
                    errorMessage = Messages.bind(Messages.Invalid_file_name, toFile);
                    isError = true;
                } else if (!ISphereHelper.checkFile(toSystem, toLibrary, toFile)) {
                    errorId = MemberValidationError.ERROR_TO_FILE;
                    errorMessage = Messages.bind(Messages.File_A_not_found, toFile);
                    isError = true;
                }
            }

            return !isError;
        }

        private boolean hasConnection(String connectionName) {

            Set<String> connectionNames = new HashSet<String>(Arrays.asList(IBMiHostContributionsHandler.getConnectionNames()));
            boolean hasConnection = connectionNames.contains(connectionName);

            return hasConnection;
        }

        private boolean validateMembers(String fromConnectionName, String toConnectionName, ExistingMemberAction existingMemberAction,
            boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck) {

            boolean isError = false;
            boolean isSeriousError = false;

            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            RecordFormatDescriptionsStore fromSourceFiles = new RecordFormatDescriptionsStore(fromSystem);
            RecordFormatDescriptionsStore toSourceFiles = new RecordFormatDescriptionsStore(toSystem);

            IMemberRenamingRule newNameRule = Preferences.getInstance().getMemberRenamingRule();
            RenameMemberActor actor = new RenameMemberActor(toSystem, newNameRule);

            Set<String> targetMembers = new HashSet<String>();

            try {

                Set<String> dirtyFiles;
                if (!ignoreUnsavedChangesError) {
                    dirtyFiles = getDirtyFiles();
                } else {
                    dirtyFiles = new HashSet<String>();
                }

                for (CopyMemberItem member : fromMembers) {

                    if (isCanceled()) {
                        break;
                    }

                    if (member.isCopied()) {
                        continue;
                    }

                    if (isSeriousError) {
                        setMemberError(member, Messages.Canceled_due_to_previous_error);
                        continue;
                    }

                    String libraryName = member.getFromLibrary();
                    String fileName = member.getFromFile();
                    String memberName = member.getFromMember();
                    String srcType = member.getFromSrcType();

                    IFile localResource = new IBMiHostContributionsHandler().getLocalResource(fromConnectionName, libraryName, fileName, memberName,
                        srcType);
                    String localResourcePath = localResource.getLocation().makeAbsolute().toOSString();
                    String from = member.getFromQSYSName();
                    String to = member.getToQSYSName();

                    if (dirtyFiles.contains(localResourcePath)) {
                        setMemberError(member, Messages.Member_is_open_in_editor_and_has_unsaved_changes);
                        isError = true;
                    } else if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
                        setMemberError(member, Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                        isError = true;
                    } else if (targetMembers.contains(to)) {
                        setMemberError(member, Messages.Can_not_copy_member_twice_to_same_target_member);
                        isError = true;
                    } else if (!ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(fromConnectionName), member.getFromLibrary(),
                        member.getFromFile(), member.getFromMember())) {
                        setMemberError(member, Messages.bind(Messages.From_member_A_not_found, from));
                        isError = true;
                    }

                    if (!isError) {
                        if (fullErrorCheck) {
                            if (ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(toConnectionName), member.getToLibrary(),
                                member.getToFile(), member.getToMember())) {

                                if (existingMemberAction.equals(ExistingMemberAction.REPLACE)) {
                                    // that is fine, go ahead
                                } else if (existingMemberAction.equals(ExistingMemberAction.RENAME)) {

                                    try {
                                        actor.produceNewMemberName(member.getToLibrary(), member.getToFile(), member.getToMember()); // $NON-NLS-1$
                                    } catch (NoMoreNamesAvailableException e) {
                                        setMemberError(member, Messages.Error_No_more_names_available_Delete_old_backups);
                                        isError = true;
                                    } catch (Exception e) {
                                        setMemberError(member, e.getLocalizedMessage());
                                        isError = true;
                                    }

                                } else {
                                    setMemberError(member, Messages.bind(Messages.Target_member_A_already_exists, to));
                                    isError = true;
                                }
                            }
                        }
                    }

                    if (!isError) {
                        if (!ignoreDataLostError) {

                            RecordFormatDescription fromRecordFormatDescription = fromSourceFiles.get(member.getFromFile(), member.getFromLibrary());
                            RecordFormatDescription toRecordFormatDescription = toSourceFiles.get(member.getToFile(), member.getToLibrary());

                            FieldDescription fromSrcDta = fromRecordFormatDescription.getFieldDescription("SRCDTA");
                            if (fromSrcDta == null) {
                                setMemberError(member, Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                    new String[] { member.getFromFile(), member.getFromLibrary(), "SRCDTA" }));
                                isError = true;
                                isSeriousError = true;
                            } else {

                                FieldDescription toSrcDta = toRecordFormatDescription.getFieldDescription("SRCDTA");
                                if (toSrcDta == null) {
                                    setMemberError(member, Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                        new String[] { member.getToFile(), member.getToLibrary(), "SRCDTA" }));
                                    isError = true;
                                    isSeriousError = true;
                                } else {

                                    if (fromSrcDta.getLength() > toSrcDta.getLength()) {
                                        setMemberError(member, Messages.Data_lost_error_From_source_line_is_longer_than_target_source_line);
                                        isError = true;
                                    }
                                }
                            }
                        }
                    }

                    if (!isError) {
                        setMemberError(member, Messages.EMPTY);
                    } else {
                        errorId = MemberValidationError.ERROR_TO_MEMBER;
                        errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                    }

                    targetMembers.add(to);
                }

            } catch (Exception e) {
                errorId = MemberValidationError.ERROR_EXCEPTION;
                errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                MessageDialogAsync.displayError(Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

            return !isError;
        }

        private Set<String> getDirtyFiles() throws Exception {

            Set<String> openFiles = new HashSet<String>();

            try {

                IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
                for (IWorkbenchWindow window : windows) {
                    IWorkbenchPage[] pages = window.getPages();
                    for (IWorkbenchPage page : pages) {
                        IEditorReference[] editors = page.getEditorReferences();
                        for (IEditorReference editorReference : editors) {
                            if (editorReference.isDirty()) {
                                IEditorInput input = editorReference.getEditorInput();
                                if (input instanceof FileEditorInput) {
                                    FileEditorInput fileInput = (FileEditorInput)input;
                                    openFiles.add(fileInput.getFile().getLocation().makeAbsolute().toOSString());
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                String message = "*** Failed retrieving list of open editors ***";
                ISpherePlugin.logError(message, e); // $NON-NLS-1$
                throw new Exception(message);
            }

            return openFiles;
        }

        private boolean isCanceled() {

            if (monitor != null) {
                if (monitor.isCanceled()) {
                    isCanceled = true;
                }
            }

            return isCanceled;
        }
    }
};
