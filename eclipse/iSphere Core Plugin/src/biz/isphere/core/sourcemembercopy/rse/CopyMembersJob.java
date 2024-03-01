/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy.rse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.StringHelper;
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
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcemembercopy.AbstractResult;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.ErrorContext;
import biz.isphere.core.sourcemembercopy.ICopyItemMessageListener;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.MemberCopyError;
import biz.isphere.core.sourcemembercopy.SynchronizeMembersAction;

public class CopyMembersJob extends Job {

    private Set<ICopyItemMessageListener> itemMessageListeners;

    private String fromConnectionName;
    private String toConnectionName;
    private CopyMemberItem[] members;
    private ExistingMemberAction existingMemberAction;
    private MissingFileAction missingFileAction;
    boolean isIgnoreDataLostError;
    boolean isIgnoreUnsavedChangesError;
    boolean isFullErrorCheck;
    boolean isRenameMemberCheck;
    boolean isValidationMode;
    private ICopyMembersPostRun postRun;

    private AS400 fromSystem;
    private AS400 toSystem;

    private Validator fromSystemNameValidator;
    private Validator toSystemNameValidator;

    private Map<String, FileError> fileValidationResult;
    private IProgressMonitor monitor;

    private CopyResult copyResult;

    public CopyMembersJob(String fromConnectionName, String toConnectionName, CopyMemberItem[] members, ICopyMembersPostRun postRun) {
        super(Messages.Copying_dots);

        this.itemMessageListeners = new HashSet<ICopyItemMessageListener>();

        this.fromConnectionName = fromConnectionName;
        this.toConnectionName = toConnectionName;
        this.members = members;
        this.existingMemberAction = ExistingMemberAction.ERROR;
        this.missingFileAction = MissingFileAction.ASK_USER;
        this.isIgnoreDataLostError = false;
        this.isIgnoreUnsavedChangesError = false;
        this.isFullErrorCheck = false;
        this.isRenameMemberCheck = true;
        this.isValidationMode = false;
        this.postRun = postRun;

        this.fileValidationResult = new HashMap<String, FileError>();
    }

    public void setMissingFileAction(MissingFileAction missingFileAction) {
        this.missingFileAction = missingFileAction;
    }

    public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
        this.existingMemberAction = existingMemberAction;
    }

    public void setIgnoreDataLostError(boolean enabled) {
        this.isIgnoreDataLostError = enabled;
    }

    public void setIgnoreUnsavedChanges(boolean enabled) {
        this.isIgnoreUnsavedChangesError = enabled;
    }

    public void setFullErrorCheck(boolean enabled) {
        this.isFullErrorCheck = enabled;
    }

    public void setRenameMemberCheck(boolean enabled) {
        this.isRenameMemberCheck = enabled;
    }

    public void setValidationMode(boolean enabled) {
        this.isValidationMode = enabled;
    }

    public void addItemErrorListener(ICopyItemMessageListener listener) {
        itemMessageListeners.add(listener);
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {

        SubMonitor subMonitor = SubMonitor.convert(monitor, members.length);

        try {
            runInSameThread(subMonitor);
        } finally {
            subMonitor.done();
        }

        return Status.OK_STATUS;
    }

    public void runInSameThread(SubMonitor subMonitor) {

        monitor = subMonitor;

        try {

            copyResult = new CopyResult();

            fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            fromSystemNameValidator = Validator.getNameInstance(getFromSystemCcsid());
            toSystemNameValidator = Validator.getNameInstance(getToSystemCcsid());

            if (isAbortProcessError(fromConnectionName, toConnectionName)) {
                return;
            }

            // Validate member copied to same target
            Set<String> targetMembers = new HashSet<String>();

            // Validate files open in editor
            Set<String> dirtyFiles;
            if (!(isIgnoreUnsavedChangesError() || isFullErrorCheck())) {
                dirtyFiles = getDirtyFiles();
            } else {
                dirtyFiles = new HashSet<String>();
            }

            for (CopyMemberItem member : members) {

                if (isCanceled()) {
                    break;
                }

                copyResult.addTotal();

                subMonitor.split(1);

                if (member.isCopied()) {
                    copyResult.addSkipped();
                    continue;
                }

                if (member.isError()) {
                    copyResult.addSkipped();
                    continue;
                }

                String fromLibraryName = member.getFromLibrary();
                String fromFileName = member.getFromFile();

                String toLibraryName = member.getToLibrary();
                String toFileName = member.getToFile();

                RemoteObject fromObject = RemoteObject.newFile(fromConnectionName, fromFileName, fromLibraryName);
                RemoteObject toObject = RemoteObject.newFile(toConnectionName, toFileName, toLibraryName);

                ErrorContext errorContext = new ErrorContext();
                errorContext.setFromObject(fromObject);
                errorContext.setToObject(toObject);
                errorContext.setCopyMemberItem(member);

                /*
                 * -------------------------------------------------------------
                 * Ensure that the member is not copied to the same name twice.
                 * -------------------------------------------------------------
                 */

                String from = member.getFromQSYSName();
                String to = member.getToQSYSName();

                boolean isTwiceError;
                if (isSameSystem() && from.equals(to)) {
                    // Local copy...
                    setMemberError(MemberCopyError.ERROR_TO_MEMBER_COPY_TO_SAME_NAME, errorContext,
                        Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                    isTwiceError = true;
                } else {
                    if (targetMembers.contains(to)) {
                        setMemberError(MemberCopyError.ERROR_TO_MEMBER_COPY_TO_SAME_NAME, errorContext,
                            Messages.Can_not_copy_member_twice_to_same_target_member);
                        isTwiceError = true;
                    } else {
                        isTwiceError = false;
                    }
                }

                // Always add the member to the set.
                // No matter whether there is an error or not.
                targetMembers.add(to);

                if (isTwiceError) {
                    continue;
                }

                /*
                 * -------------------------------------------------------------
                 * Check if member is open in an editor and has unsaved changes
                 * -------------------------------------------------------------
                 */

                String memberName = member.getFromMember();
                String srcType = member.getFromSrcType();
                IFile localResource = new IBMiHostContributionsHandler().getLocalResource(fromConnectionName, fromLibraryName, fromFileName,
                    memberName, srcType);
                String localResourcePath = localResource.getLocation().makeAbsolute().toOSString();

                boolean isDirty;
                if (dirtyFiles.contains(localResourcePath)) {
                    setMemberError(MemberCopyError.ERROR_FROM_MEMBER_IS_DIRTY, errorContext,
                        Messages.Member_is_open_in_editor_and_has_unsaved_changes);
                    isDirty = true;
                } else {
                    isDirty = false;
                }

                if (isDirty) {
                    continue;
                }

                /*
                 * -------------------------------------------------------------
                 * Do pre-checks.
                 * -------------------------------------------------------------
                 */

                boolean isPreCheckValid;
                if (isFromLibraryAndFileValid(fromConnectionName, member, errorContext)) {
                    isPreCheckValid = isToLibraryAndFileValid(toConnectionName, member, errorContext);
                } else {
                    isPreCheckValid = false;
                }

                if (!isPreCheckValid) {
                    continue;
                }

                /*
                 * -------------------------------------------------------------
                 * Check target member.
                 * -------------------------------------------------------------
                 */

                boolean canCopy;
                if (isMember(getToSystem(), member.getToLibrary(), member.getToFile(), member.getToMember())) {
                    if (ExistingMemberAction.RENAME.equals(existingMemberAction)) {
                        canCopy = performRenameMember(getToSystem(), member, errorContext);
                    } else if (ExistingMemberAction.REPLACE.equals(existingMemberAction)) {
                        canCopy = true;
                    } else {
                        canCopy = false;
                        setMemberError(MemberCopyError.ERROR_TO_MEMBER_EXISTS, errorContext,
                            Messages.bind(Messages.Target_member_A_already_exists, member.getToQSYSName()));
                    }
                } else {
                    canCopy = true;
                }

                if (!canCopy) {
                    continue;
                }

                /*
                 * -------------------------------------------------------------
                 * Copy the member.
                 * -------------------------------------------------------------
                 */

                boolean isCopied;
                if (!isValidationMode()) {
                    isCopied = member.performCopyOperation(fromConnectionName, toConnectionName);
                    if (!isCopied) {
                        setMemberError(MemberCopyError.ERROR_COPY_FILE_COMMAND, errorContext, member.getErrorMessage());
                    }
                } else {
                    isCopied = true;
                }

                if (!isCopied) {
                    continue;
                }

                reportMemberCopied(member);
            }

        } catch (Throwable e) {
            String message = ExceptionHelper.getLocalizedMessage(e);
            ISpherePlugin.logError("Unexpected error: " + message, e);
            setAbortErrorAndCancel(MemberCopyError.ERROR_EXCEPTION, message);
        } finally {
            copyResult.finished();
            subMonitor.done();
            if (postRun != null) {
                postRun.returnCopyMembersResult(monitor.isCanceled(), copyResult.getTotal(), copyResult.getSkipped(), copyResult.getProcessed(),
                    copyResult.getErrors(), copyResult.getAverageTime(), copyResult.getCancelErrorId(), copyResult.getCancelMessage());
            }
        }
    }

    private boolean isAbortProcessError(String fromConnectionName, String toConnectionName) {

        if (getFromSystem() == null) {
            String errorMessage = Messages.bind(Messages.Connection_A_not_found, fromConnectionName);
            setAbortErrorAndCancel(MemberCopyError.ERROR_FROM_CONNECTION_NOT_FOUND, errorMessage);
            return true;
        }

        if (getToSystem() == null) {
            String errorMessage = Messages.bind(Messages.Connection_A_not_found, toConnectionName);
            setAbortErrorAndCancel(MemberCopyError.ERROR_TO_CONNECTION_NOT_FOUND, errorMessage);
            return true;
        }

        return false;
    }

    private boolean isFromLibraryAndFileValid(String fromConnectionName, CopyMemberItem copyMemberItem, ErrorContext errorContext) {

        String fromLibraryName = copyMemberItem.getFromLibrary();
        String fromFileName = copyMemberItem.getFromFile();
        String fromQualifiedFileName = getQualifiedName(fromLibraryName, fromFileName);

        FileError fileError = null;

        String targetFileKey = getFileValidKey(fromConnectionName, fromLibraryName, fromFileName);
        if (!fileValidationResult.containsKey(targetFileKey)) {

            if (isFullErrorCheck()) {
                if (fileError == null) {
                    if (!fromSystemNameValidator.validate(fromLibraryName)) {
                        String errorMessage = Messages.bind(Messages.Invalid_library_name, fromLibraryName);
                        fileError = new FileError(MemberCopyError.ERROR_FROM_LIBRARY_NAME_NOT_VALID, errorMessage);
                    }
                }

                if (fileError == null) {
                    if (!isLibrary(getFromSystem(), fromLibraryName)) {
                        String errorMessage = Messages.bind(Messages.Library_A_not_found, fromLibraryName);
                        fileError = new FileError(MemberCopyError.ERROR_FROM_LIBRARY_NOT_FOUND, errorMessage);
                    }
                }

                if (fileError == null) {
                    if (!fromSystemNameValidator.validate(fromFileName)) {
                        String errorMessage = Messages.bind(Messages.Invalid_file_name, fromFileName);
                        fileError = new FileError(MemberCopyError.ERROR_FROM_FILE_NAME_NOT_VALID, errorMessage);
                    }
                }

                if (fileError == null) {
                    if (!isFile(getFromSystem(), fromLibraryName, fromFileName)) {
                        String errorMessage = Messages.bind(Messages.File_A_not_found, fromQualifiedFileName);
                        fileError = new FileError(MemberCopyError.ERROR_FROM_FILE_NOT_FOUND, errorMessage);
                    }
                }
            }

            // Store validation result
            fileValidationResult.put(targetFileKey, fileError);

        } else {

            // Get previous result from isTargetFileFoundAndValid()
            fileError = fileValidationResult.get(targetFileKey);
        }

        if (fileError == null) {
            return true;
        }

        setMemberError(fileError.errorId, errorContext, fileError.errorMessage);

        return false;
    }

    private boolean isToLibraryAndFileValid(String toConnectionName, CopyMemberItem copyMemberItem, ErrorContext errorContext) {

        String fromLibraryName = copyMemberItem.getFromLibrary();
        String fromFileName = copyMemberItem.getFromFile();

        String toLibraryName = copyMemberItem.getToLibrary();
        String toFileName = copyMemberItem.getToFile();
        String toQualifiedFileName = getQualifiedName(toLibraryName, toFileName);

        FileError fileError = null;

        String targetFileKey = getFileValidKey(toConnectionName, toLibraryName, toFileName);
        if (!fileValidationResult.containsKey(targetFileKey)) {

            if (isFullErrorCheck()) {
                if (fileError == null) {
                    if (!toSystemNameValidator.validate(toLibraryName)) {
                        String errorMessage = Messages.bind(Messages.Invalid_library_name, toLibraryName);
                        fileError = new FileError(MemberCopyError.ERROR_TO_LIBRARY_NAME_NOT_VALID, errorMessage);
                    }
                }

                if (fileError == null) {
                    if (!isLibrary(getFromSystem(), toLibraryName)) {
                        String errorMessage = Messages.bind(Messages.Library_A_not_found, toLibraryName);
                        fileError = new FileError(MemberCopyError.ERROR_TO_LIBRARY_NOT_FOUND, errorMessage);
                    }
                }

                if (fileError == null) {
                    if (!toSystemNameValidator.validate(toFileName)) {
                        String errorMessage = Messages.bind(Messages.Invalid_file_name, toQualifiedFileName);
                        fileError = new FileError(MemberCopyError.ERROR_TO_FILE_NAME_NOT_VALID, errorMessage);
                    }
                }
            }

            if (fileError == null) {
                if (!isFile(getToSystem(), toLibraryName, toFileName)) {
                    if (!askUserAndCreateFile(fromLibraryName, fromFileName, toLibraryName, toFileName)) {
                        String errorMessage = Messages.bind(Messages.File_A_not_found, toQualifiedFileName);
                        fileError = new FileError(MemberCopyError.ERROR_TO_FILE_NOT_FOUND, errorMessage);
                    }
                }
            }

            if (fileError == null) {
                if (!isIgnoreDataLostError()) {
                    int fromRecordLength = getRecordLength(getFromSystem(), fromLibraryName, fromFileName);
                    int toRecordLength = getRecordLength(getToSystem(), toLibraryName, toFileName);
                    if (toRecordLength < fromRecordLength) {
                        Object[] values = new Object[] { fromRecordLength, fromLibraryName, fromFileName, toRecordLength, toLibraryName, toFileName };
                        String errorMessage = Messages.bind(
                            Messages.Data_lost_error_From_source_line_length_A_of_file_B_C_is_longer_than_target_source_line_length_D_of_file_E_F,
                            values);
                        fileError = new FileError(MemberCopyError.ERROR_TO_FILE_DATA_LOST, errorMessage);
                    }
                }
            }

            // Store validation result
            fileValidationResult.put(targetFileKey, fileError);

        } else {

            // Get previous result from isTargetFileFoundAndValid()
            fileError = fileValidationResult.get(targetFileKey);
        }

        if (fileError == null) {
            return true;
        }

        setMemberError(fileError.errorId, errorContext, fileError.errorMessage);

        return false;
    }

    private boolean askUserAndCreateFile(String fromLibraryName, String fromFileName, String toLibraryName, String toFileName) {

        String toQualifiedFileName = getQualifiedName(toLibraryName, toFileName);

        boolean doCreateMissingFile;
        if (MissingFileAction.ERROR.equals(missingFileAction)) {
            doCreateMissingFile = false;
        } else if (MissingFileAction.CREATE.equals(missingFileAction)) {
            doCreateMissingFile = true;
        } else {
            String[] messages = new String[] { toQualifiedFileName, "Create missing file?" };
            String[] buttonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
            int result = MessageDialogAsync.displayBlockingDialog(MessageDialog.CONFIRM, buttonLabels, Messages.Confirmation, messages);
            if (result == 0) {
                doCreateMissingFile = true;
            } else if (result == 1) {
                doCreateMissingFile = false;
            } else {
                doCreateMissingFile = false;
                String errorMessage = Messages.bind(Messages.File_A_not_found, toQualifiedFileName);
                setAbortErrorAndCancel(MemberCopyError.ERROR_TO_FILE_NOT_FOUND, errorMessage);
            }
        }

        RemoteObject newTargetFile = null;
        if (doCreateMissingFile) {
            newTargetFile = createMissingSourceFileFromTemplate(getFromSystem(), fromLibraryName, fromFileName, getToSystem(), toLibraryName,
                toFileName);
        }

        if (newTargetFile != null) {
            return true;
        }

        return false;
    }

    private String getFileValidKey(String connectionName, String libraryName, String fileName) {
        return String.format("%s:%s/%s", connectionName, libraryName, fileName);
    }

    private boolean isLibrary(AS400 system, String libraryName) {

        boolean isLibrary = ISphereHelper.checkLibrary(system, libraryName);

        return isLibrary;
    }

    private boolean isFile(AS400 system, String libraryName, String fileName) {

        boolean isFile = ISphereHelper.checkFile(system, libraryName, fileName);

        return isFile;
    }

    private boolean isMember(AS400 system, String libraryName, String fileName, String memberName) {

        boolean isMember = ISphereHelper.checkMember(system, libraryName, fileName, memberName);

        return isMember;
    }

    private RemoteObject createMissingSourceFileFromTemplate(AS400 tmplSystem, String tmplLibraryName, String tmplFileName, AS400 toSystem,
        String toLibraryName, String toFileName) {

        int recordLength = getRecordLength(tmplSystem, tmplLibraryName, tmplFileName);

        String description;
        try {
            RemoteObject template = ISphereHelper.resolveFile(tmplSystem, tmplLibraryName, tmplFileName);
            description = template.getDescription();
        } catch (Exception e) {
            String message = "*** Could not find template file " + tmplFileName + " in library " + tmplLibraryName + " ***";
            ISpherePlugin.logError(message, e);
            return null;
        }

        try {

            String command;
            String message;
            List<AS400Message> rtnMessages = new LinkedList<AS400Message>();

            if (!isValidationMode()) {
                command = String.format("CRTSRCPF FILE(%s/%s) RCDLEN(%s) TEXT('%s')", toLibraryName, toFileName, recordLength, description);
                message = ISphereHelper.executeCommand(toSystem, command, rtnMessages);
            } else {
                command = null;
                message = null;
            }

            if (!StringHelper.isNullOrEmpty(message)) {
                ISphereHelper.displayCommandExecutionError(command, rtnMessages);
                return null;
            }

            RemoteObject newFile = ISphereHelper.resolveFile(toSystem, toLibraryName, toFileName);
            return newFile;

        } catch (Exception e) {
            String message = "*** Could not create source file " + toFileName + " in library " + toLibraryName + " from template ***";
            ISpherePlugin.logError(message, e);
            return null;
        }
    }

    private int getRecordLength(AS400 system, String libraryName, String fileName) {

        RecordFormatDescriptionsStore templateRecordFormat = new RecordFormatDescriptionsStore(system);
        RecordFormatDescription toRecordFormatDescription = templateRecordFormat.get(fileName, libraryName);

        int recordLength = 0;
        for (FieldDescription fieldDescription : toRecordFormatDescription.getFieldDescriptions()) {
            recordLength += fieldDescription.getLength();
        }

        return recordLength;
    }

    private boolean performRenameMember(AS400 system, CopyMemberItem copyMemberItem, ErrorContext errorContext) {

        if (isValidationMode()) {
            if (!(isRenameMemberCheck() || isFullErrorCheck())) {
                return true;
            }
        }

        IMemberRenamingRule newNameRule = Preferences.getInstance().getMemberRenamingRule();
        RenameMemberActor actor = new RenameMemberActor(system, newNameRule);

        String library = copyMemberItem.getToLibrary();
        String file = copyMemberItem.getToFile();
        String member = copyMemberItem.getToMember();

        try {

            List<AS400Message> rtnMessages = new LinkedList<AS400Message>();
            QSYSObjectPathName newMember = actor.produceNewMemberName(library, file, member);

            String message;
            if (!isValidationMode()) {
                String command = String.format("RNMM FILE(%s/%s) MBR(%s) NEWMBR(%s)", library, file, member, newMember.getMemberName()); //$NON-NLS-1$
                message = ISphereHelper.executeCommand(system, command, rtnMessages);
            } else {
                message = null;
            }

            if (message != null) {
                StringBuilder errorMessage = new StringBuilder();
                for (AS400Message as400Message : rtnMessages) {
                    if (errorMessage.length() > 0) {
                        errorMessage.append(" :: "); //$NON-NLS-1$
                    }
                    errorMessage.append(as400Message.getText());
                }
                setMemberError(MemberCopyError.ERROR_TO_MEMBER_RENAME_EXCEPTION, errorContext, errorMessage.toString());
                return false;
            }

            return true;

        } catch (Exception e) {
            // e.g. no more names are available
            setMemberError(MemberCopyError.ERROR_TO_MEMBER_RENAME_EXCEPTION, errorContext, ExceptionHelper.getLocalizedMessage(e));
            return false;
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

    protected boolean isSameSystem() {

        if (getFromSystem().getSystemName().equals(getToSystem().getSystemName())) {
            return true;
        }

        return false;
    }

    private boolean isIgnoreDataLostError() {
        return isIgnoreDataLostError;
    }

    private boolean isIgnoreUnsavedChangesError() {
        return isIgnoreUnsavedChangesError;
    }

    private boolean isFullErrorCheck() {
        return isFullErrorCheck;
    }

    private boolean isRenameMemberCheck() {
        return isRenameMemberCheck;
    }

    private boolean isValidationMode() {
        return isValidationMode;
    }

    private AS400 getFromSystem() {
        return fromSystem;
    }

    private int getFromSystemCcsid() {
        return getCcsid(getFromSystem());
    }

    private AS400 getToSystem() {
        return toSystem;
    }

    private int getToSystemCcsid() {
        return getCcsid(getToSystem());
    }

    private int getCcsid(AS400 system) {

        if (system == null) {
            return -1;
        }

        return system.getCcsid();
    }

    private String getQualifiedName(String libraryName, String fileName) {
        return String.format("%s/%s", libraryName, fileName);
    }

    private void setAbortErrorAndCancel(MemberCopyError errorId, String errorMessage) {

        if (itemMessageListeners != null) {
            for (ICopyItemMessageListener errorListener : itemMessageListeners) {
                errorListener.reportCopyMemberMessage(errorId, null, errorMessage);
                copyResult.setCancel(errorId, errorMessage);
                cancelOperation();
            }
        }
    }

    private void setMemberError(MemberCopyError errorId, ErrorContext errorContext, String errorMessage) {

        if (errorId == MemberCopyError.ERROR_NONE) {
            throw new IllegalArgumentException("Update Javadoc in ICopyItemMessageListener, if you want to allow: " + errorId.name());
        }

        CopyMemberItem member = errorContext.getCopyMemberItem();

        member.setErrorMessage(errorMessage);

        if (itemMessageListeners != null) {
            for (ICopyItemMessageListener errorListener : itemMessageListeners) {
                SynchronizeMembersAction response = errorListener.reportCopyMemberMessage(errorId, member, errorMessage);
                if (response == SynchronizeMembersAction.CANCEL) {
                    member.setErrorMessage(errorMessage);
                    copyResult.addError();
                    copyResult.setCancel(errorId, errorMessage);
                    cancelOperation();
                } else if (response == SynchronizeMembersAction.CONTINUE_WITH_ERROR) {
                    member.setErrorMessage(errorMessage);
                    copyResult.addError();
                } else {
                    // Continue
                }
            }
        }
    }

    private void reportMemberCopied(CopyMemberItem member) {

        MemberCopyError errorId = MemberCopyError.ERROR_NONE;

        copyResult.addProcessed();

        if (itemMessageListeners != null) {
            for (ICopyItemMessageListener errorListener : itemMessageListeners) {
                SynchronizeMembersAction response = errorListener.reportCopyMemberMessage(MemberCopyError.ERROR_NONE, member, null);
                if (response == SynchronizeMembersAction.CANCEL) {
                    String errorMessage = Messages.Operation_has_been_canceled_by_the_user;
                    copyResult.setCancel(errorId, errorMessage);
                    cancelOperation();
                }
            }
        }
    }

    public boolean isError() {
        return copyResult.isError();
    }

    public int getCountTotal() {
        return copyResult.getTotal();
    }

    public int getCountSkipped() {
        return copyResult.getSkipped();
    }

    public int getMembersCopiedCount() {
        return copyResult.getProcessed();
    }

    public int getMembersErrorCount() {
        return copyResult.getErrors();
    }

    public long getAverageTime() {
        return copyResult.getAverageTime();
    }

    public void cancelOperation() {
        monitor.setCanceled(true);
    }

    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    private class FileError {

        MemberCopyError errorId;
        String errorMessage;

        public FileError(MemberCopyError errorId, String errorMessage) {
            this.errorId = errorId;
            this.errorMessage = errorMessage;
        }
    }

    private class CopyResult extends AbstractResult<MemberCopyError> {
    };
}
