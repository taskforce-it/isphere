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
import org.eclipse.core.runtime.SubMonitor;
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
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.exceptions.NoMoreNamesAvailableException;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;

public class CopyMemberValidator extends Thread {

    public enum MemberValidationError {
        ERROR_NONE,
        ERROR_FROM_FILE,
        ERROR_FROM_MEMBER,
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
            this.monitor = SubMonitor.convert(monitor);
        }

        @Override
        public void run() {

            errorId = MemberValidationError.ERROR_NONE;
            errorMessage = null;

            if (!isCanceled() && errorId == MemberValidationError.ERROR_NONE) {

                CopyMemberItem firstFromMember;
                String fromLibraryName;
                String fromFileName;

                if (fromMembers.length > 0) {
                    firstFromMember = fromMembers[0];
                    fromLibraryName = firstFromMember.getFromLibrary();
                    fromFileName = firstFromMember.getFromFile();
                    validateTargetFile(fromConnectionName, fromLibraryName, fromFileName, toConnectionName, toLibraryName, toFileName);
                }
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

        private boolean setFileError(MemberValidationError errorId, ErrorContext errorContext, String errorMessage) {

            if (!(errorId == MemberValidationError.ERROR_TO_CONNECTION || errorId == MemberValidationError.ERROR_TO_LIBRARY
                || errorId == MemberValidationError.ERROR_TO_FILE || errorId == MemberValidationError.ERROR_FROM_FILE)) {
                throw new IllegalArgumentException("Update Javadoc in IItemErrorListener, is you want to allow: " + errorId.name());
            }

            boolean isError = true;

            if (itemErrorListeners != null) {
                for (IItemErrorListener errorListener : itemErrorListeners) {
                    boolean isCancelRequested = errorListener.reportError(CopyMemberValidator.this, errorId, errorContext, errorMessage);
                    if (isCancelRequested) {
                        CopyMemberValidator.this.errorId = errorId;
                        CopyMemberValidator.this.errorMessage = errorMessage;
                        monitor.setCanceled(true);
                    } else {
                        isError = false;
                    }
                }
            }

            return isError;
        }

        private boolean setMemberError(MemberValidationError errorId, CopyMemberItem member, String errorMessage) {

            boolean isError = true;

            member.setErrorMessage(errorMessage);

            if (itemErrorListeners != null) {
                for (IItemErrorListener errorListener : itemErrorListeners) {
                    if (errorListener.reportError(CopyMemberValidator.this, errorId, member, errorMessage)) {
                        monitor.setCanceled(true);
                    } else {
                        isError = false;
                    }
                }
            }

            return isError;
        }

        public void cancel() {
            isCanceled = true;
            if (monitor != null) {
                monitor.setCanceled(true);
            }
        }

        private void validateTargetFile(String fromConnectionName, String fromLibrary, String fromFile, String toConnectionName, String toLibrary,
            String toFile) {

            Validator nameValidator = Validator.getNameInstance(toCcsid);

            boolean isError = !hasConnection(toConnectionName);
            if (isError) {
                isError = setFileError(MemberValidationError.ERROR_TO_CONNECTION, null, toConnectionName);
            }

            if (!isError) {
                isError = !nameValidator.validate(toLibrary);
                if (isError) {
                    isError = setFileError(MemberValidationError.ERROR_TO_LIBRARY, ErrorContext.newToLibrary(toConnectionName, toLibrary),
                        Messages.bind(Messages.Invalid_library_name, toLibrary));
                }
            }

            AS400 toSystem;
            if (!isError) {
                toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);
            } else {
                toSystem = null;
            }

            if (!isError) {
                isError = !ISphereHelper.checkLibrary(toSystem, toLibrary);
                if (isError) {
                    isError = setFileError(MemberValidationError.ERROR_TO_LIBRARY, ErrorContext.newToLibrary(toConnectionName, toLibrary),
                        Messages.bind(Messages.Library_A_not_found, toLibrary));
                }
            }

            if (!isError) {
                isError = !nameValidator.validate(toFile);
                if (isError) {
                    isError = setFileError(MemberValidationError.ERROR_TO_FILE, ErrorContext.newToFile(toConnectionName, toFile, toLibrary),
                        Messages.bind(Messages.Invalid_file_name, toFile));
                }
            }

            if (!isError) {
                isError = !ISphereHelper.checkFile(toSystem, toLibrary, toFile);
                if (isError) {
                    ErrorContext errorContext = new ErrorContext();
                    errorContext.setFromObject(RemoteObject.newFile(fromConnectionName, fromFile, fromLibrary));
                    errorContext.setToObject(RemoteObject.newFile(toConnectionName, toFile, toLibrary));
                    isError = setFileError(MemberValidationError.ERROR_TO_FILE, errorContext, Messages.bind(Messages.File_A_not_found, toFile));
                }
            }

            if (!isError) {
                if (!ignoreDataLostError) {

                    AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);

                    RecordFormatDescriptionsStore fromSourceFiles = new RecordFormatDescriptionsStore(fromSystem);
                    RecordFormatDescriptionsStore toSourceFiles = new RecordFormatDescriptionsStore(toSystem);

                    RecordFormatDescription fromRecordFormatDescription = fromSourceFiles.get(fromFile, fromLibrary);
                    RecordFormatDescription toRecordFormatDescription = toSourceFiles.get(toFile, toLibrary);

                    FieldDescription fromSrcDta = fromRecordFormatDescription.getFieldDescription("SRCDTA");
                    if (fromSrcDta == null) {
                        isError = setFileError(MemberValidationError.ERROR_FROM_FILE,
                            ErrorContext.newToFile(fromConnectionName, fromFile, fromLibrary),
                            Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                new String[] { fromFile, fromLibrary, "SRCDTA" }));
                    } else {

                        FieldDescription toSrcDta = toRecordFormatDescription.getFieldDescription("SRCDTA");
                        if (toSrcDta == null) {
                            isError = setFileError(MemberValidationError.ERROR_TO_FILE, ErrorContext.newToFile(toConnectionName, toFile, toLibrary),
                                Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                                    new String[] { toFile, toLibrary, "SRCDTA" }));
                        } else {

                            int fromLength = fromSrcDta.getLength();
                            int toLength = toSrcDta.getLength();
                            if (fromLength > toLength) {
                                ErrorContext errorContext = new ErrorContext();
                                errorContext.setFromObject(RemoteObject.newFile(fromConnectionName, fromFile, fromLibrary));
                                errorContext.setToObject(RemoteObject.newFile(toConnectionName, toFile, toLibrary));
                                isError = setFileError(MemberValidationError.ERROR_TO_FILE, errorContext, Messages
                                    .bind(Messages.Data_lost_error_From_source_line_A_is_longer_than_target_source_line_B, fromLength, toLength));
                            }
                        }
                    }
                }
            }
        }

        private boolean hasConnection(String connectionName) {

            Set<String> connectionNames = new HashSet<String>(Arrays.asList(IBMiHostContributionsHandler.getConnectionNames()));
            boolean hasConnection = connectionNames.contains(connectionName);

            return hasConnection;
        }

        private void validateMembers(String fromConnectionName, String toConnectionName, ExistingMemberAction existingMemberAction,
            boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck) {

            boolean isFinalError = false;

            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

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

                    boolean isCurrentError = false;

                    if (isCanceled()) {
                        this.cancel();
                        CopyMemberValidator.this.cancelOperation();
                        break;
                    }

                    if (member.isCopied()) {
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
                        setMemberError(MemberValidationError.ERROR_FROM_MEMBER, member, Messages.Member_is_open_in_editor_and_has_unsaved_changes);
                        isCurrentError = true;
                    } else if (!ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(fromConnectionName), member.getFromLibrary(),
                        member.getFromFile(), member.getFromMember())) {
                        setMemberError(MemberValidationError.ERROR_FROM_MEMBER, member, Messages.bind(Messages.From_member_A_not_found, from));
                        isCurrentError = true;
                    } else if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
                        setMemberError(MemberValidationError.ERROR_TO_MEMBER, member, Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                        isCurrentError = true;
                    } else if (targetMembers.contains(to)) {
                        setMemberError(MemberValidationError.ERROR_TO_MEMBER, member, Messages.Can_not_copy_member_twice_to_same_target_member);
                        isCurrentError = true;
                    }

                    if (!isCurrentError) {
                        if (fullErrorCheck) {
                            if (ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(toConnectionName), member.getToLibrary(),
                                member.getToFile(), member.getToMember())) {

                                if (existingMemberAction.equals(ExistingMemberAction.REPLACE)) {
                                    // that is fine, go ahead
                                } else if (existingMemberAction.equals(ExistingMemberAction.RENAME)) {

                                    try {
                                        actor.produceNewMemberName(member.getToLibrary(), member.getToFile(), member.getToMember()); // $NON-NLS-1$
                                    } catch (NoMoreNamesAvailableException e) {
                                        setMemberError(MemberValidationError.ERROR_TO_MEMBER, member,
                                            Messages.Error_No_more_names_available_Delete_old_backups);
                                        isCurrentError = true;
                                    } catch (Exception e) {
                                        setMemberError(MemberValidationError.ERROR_TO_MEMBER, member, e.getLocalizedMessage());
                                        isCurrentError = true;
                                    }

                                } else {
                                    setMemberError(MemberValidationError.ERROR_TO_MEMBER, member,
                                        Messages.bind(Messages.Target_member_A_already_exists, to));
                                    isCurrentError = true;
                                }
                            }
                        }
                    }

                    if (!isCurrentError) {
                        setMemberError(MemberValidationError.ERROR_NONE, member, Messages.EMPTY);
                    } else {
                        if (!isFinalError) {
                            isFinalError = isCurrentError;
                        }
                    }

                    targetMembers.add(to);
                }

            } catch (Exception e) {
                errorId = MemberValidationError.ERROR_EXCEPTION;
                errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
                MessageDialogAsync.displayBlockingError(Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            }

            if (isFinalError) {
                errorId = MemberValidationError.ERROR_TO_MEMBER;
                errorMessage = Messages.Validation_ended_with_errors_Request_canceled;
            }
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
