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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
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

public class ValidateMembersJob extends Job {

    private Set<IValidateItemMessageListener> itemMessageListeners;

    private String fromConnectionName;
    private CopyMemberItem[] fromMembers;
    private ExistingMemberAction existingMemberAction;
    private boolean ignoreDataLostError;
    private boolean ignoreUnsavedChangesError;
    private boolean fullErrorCheck;
    private IValidateMembersPostRun postRun;

    private IProgressMonitor monitor;

    private String toConnectionName;
    private int toCcsid;

    private ValidationResult validationResult;

    public ValidateMembersJob(String fromConnectionName, CopyMemberItem[] fromMembers, ExistingMemberAction existingMemberAction,
        boolean ignoreDataLostError, boolean ignoreUnsavedChangesError, boolean fullErrorCheck, IValidateMembersPostRun postRun) {
        super(Messages.Validating_dots);

        this.itemMessageListeners = null;

        this.fromConnectionName = fromConnectionName;
        this.fromMembers = fromMembers;
        this.existingMemberAction = existingMemberAction;
        this.ignoreDataLostError = ignoreDataLostError;
        this.ignoreUnsavedChangesError = ignoreUnsavedChangesError;
        this.fullErrorCheck = fullErrorCheck;
        this.postRun = postRun;
    }

    public void addItemErrorListener(IValidateItemMessageListener listener) {
        getItemMessageListeners().add(listener);
    }

    public void setToConnectionName(String connectionName) {
        this.toConnectionName = connectionName;
    }

    public void setToCcsid(int toCcsid) {
        this.toCcsid = toCcsid;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {

        SubMonitor subMonitor = SubMonitor.convert(monitor, fromMembers.length);

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

            validationResult = new ValidationResult();

            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);
            IMemberRenamingRule newNameRule = Preferences.getInstance().getMemberRenamingRule();
            RenameMemberActor actor = new RenameMemberActor(toSystem, newNameRule);

            // Validate member copied to same target
            Set<String> targetMembers = new HashSet<String>();

            // Validate files open in editor
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

                validationResult.addTotal();

                subMonitor.split(1);

                if (member.isCopied()) {
                    validationResult.addSkipped();
                    continue;
                }

                // Prepare validation ...
                String fromLibraryName = member.getFromLibrary();
                String fromFileName = member.getFromFile();
                String srcType = member.getFromSrcType();

                String toLibraryName = member.getToLibrary();
                String toFileName = member.getToFile();

                String from = member.getFromQSYSName();
                String to = member.getToQSYSName();

                RemoteObject fromObject = RemoteObject.newFile(fromConnectionName, fromFileName, fromLibraryName);
                RemoteObject toObject = RemoteObject.newFile(toConnectionName, toFileName, toLibraryName);

                ErrorContext errorContext = new ErrorContext();
                errorContext.setFromObject(fromObject);
                errorContext.setToObject(toObject);
                errorContext.setCopyMemberItem(member);

                // Clear member error message
                member.setErrorMessage(Messages.EMPTY);

                try {

                    if (!isFromLibraryValid(fromConnectionName, member, errorContext)) {
                        // nothing to do here, everything has been done in
                        // isTargetLibraryValid()
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    // Is 'to' file valid?
                    if (!isTargetLibraryValid(toConnectionName, member, errorContext)) {
                        // nothing to do here, everything has been done in
                        // isTargetLibraryValid()
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    // Is 'to' file valid?
                    if (!isTargetFileValid(fromConnectionName, toConnectionName, member, errorContext)) {
                        // nothing to do here, everything has been done in
                        // isTargetFileValid()
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    String memberName = member.getFromMember();
                    IFile localResource = new IBMiHostContributionsHandler().getLocalResource(fromConnectionName, fromLibraryName, fromFileName,
                        memberName, srcType);
                    String localResourcePath = localResource.getLocation().makeAbsolute().toOSString();

                    // Is member not 'dirty' (open in editor)
                    if (dirtyFiles.contains(localResourcePath)) {
                        setMemberError(MemberValidationError.ERROR_FROM_MEMBER_IS_DIRTY, errorContext,
                            Messages.Member_is_open_in_editor_and_has_unsaved_changes);
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);

                    // Have 'from' member?
                    if (!isMember(fromSystem, fromLibraryName, fromFileName, member.getFromMember())) {
                        setMemberError(MemberValidationError.ERROR_FROM_MEMBER_NOT_FOUND, errorContext,
                            Messages.bind(Messages.From_member_A_not_found, from));
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    // Do not copy member to same name?
                    if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
                        setMemberError(MemberValidationError.ERROR_TO_MEMBER_COPY_TO_SAME_NAME, errorContext,
                            Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                        continue;
                    } else if (targetMembers.contains(to)) {
                        setMemberError(MemberValidationError.ERROR_TO_MEMBER_COPY_TO_SAME_NAME, errorContext,
                            Messages.Can_not_copy_member_twice_to_same_target_member);
                        continue;
                    }
                    if (skipNextToMember(member)) continue;

                    // Is full error check enabled?
                    if (fullErrorCheck) {

                        errorContext.setCopyMemberItem(member);

                        // Does the 'to' member exist?
                        if (ISphereHelper.checkMember(IBMiHostContributionsHandler.getSystem(toConnectionName), toLibraryName, toFileName,
                            member.getToMember())) {

                            if (existingMemberAction.equals(ExistingMemberAction.REPLACE)) {
                                // that is fine, go ahead
                            } else if (existingMemberAction.equals(ExistingMemberAction.RENAME)) {

                                try {
                                    actor.produceNewMemberName(member.getToLibrary(), member.getToFile(), member.getToMember()); // $NON-NLS-1$
                                } catch (NoMoreNamesAvailableException e) {
                                    setMemberError(MemberValidationError.ERROR_TO_MEMBER_OUT_OF_NAMES, errorContext,
                                        Messages.Error_No_more_names_available_Delete_old_backups);
                                    continue;
                                } catch (Exception e) {
                                    setMemberError(MemberValidationError.ERROR_TO_MEMBER_RENAME_EXCEPTION, errorContext, e.getLocalizedMessage());
                                    continue;
                                }

                            } else {
                                setMemberError(MemberValidationError.ERROR_TO_MEMBER_EXISTS, errorContext,
                                    Messages.bind(Messages.Target_member_A_already_exists, to));
                                continue;
                            }
                        } else {
                            // Fine. Member does not exist.
                        }
                    }

                } finally {

                    // Always do the following things ...
                    // No matter whether there is an error or not.
                    targetMembers.add(to);

                    if (!member.isError()) {
                        reportMemberValidated(member);
                    }
                }
            }

        } catch (Exception e) {
            ISpherePlugin.logError("*** Error when Copying Members ***", e);
            monitor.setCanceled(true);
            MessageDialogAsync.displayBlockingError(Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        } finally {
            validationResult.finished();
            subMonitor.done();
            if (postRun != null) {
                postRun.returnValidateMembersResult(monitor.isCanceled(), validationResult.getTotal(), validationResult.getSkipped(),
                    validationResult.getProcessed(), validationResult.getErrors(), validationResult.getAverageTime(),
                    validationResult.getCancelErrorId(), validationResult.getCancelMessage());
            }
        }
    }

    private boolean skipNextToMember(CopyMemberItem member) {
        // Skip next step if the member has an error but the user voted for
        // going further down the list.
        if (member.isError()) {
            return true;
        }
        return false;
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

    private boolean isFromLibraryValid(String fromConnectionName, CopyMemberItem member, ErrorContext errorContext) {

        String fromLibraryName = member.getFromLibrary();

        MemberValidationError errorId;
        String errorMessage;

        // Validate library...
        Validator nameValidator = Validator.getNameInstance(toCcsid);

        // Have 'to' connection ?
        if (!hasConnection(fromConnectionName)) {

            errorId = MemberValidationError.ERROR_FROM_CONNECTION_NOT_FOUND;
            errorMessage = Messages.bind(Messages.Connection_A_not_found, fromConnectionName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        }

        // 'to' library name valid?
        if (!nameValidator.validate(fromLibraryName)) {

            errorId = MemberValidationError.ERROR_FROM_LIBRARY_NAME_NOT_VALID;
            errorMessage = Messages.bind(Messages.Invalid_library_name, fromLibraryName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        }

        AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

        // Have 'to' library?
        if (!ISphereHelper.checkLibrary(toSystem, fromLibraryName)) {

            errorId = MemberValidationError.ERROR_FROM_LIBRARY_NOT_FOUND;
            errorMessage = Messages.bind(Messages.Library_A_not_found, fromLibraryName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
            return false;
        }

        // All fine. Continue with validation.
        return true;
    }

    private boolean isTargetLibraryValid(String toConnectionName, CopyMemberItem member, ErrorContext errorContext) {

        String toLibraryName = member.getToLibrary();

        MemberValidationError errorId;
        String errorMessage;

        // Validate library...
        Validator nameValidator = Validator.getNameInstance(toCcsid);

        // Have 'to' connection ?
        if (!hasConnection(toConnectionName)) {

            errorId = MemberValidationError.ERROR_TO_CONNECTION_NOT_FOUND;
            errorMessage = Messages.bind(Messages.Connection_A_not_found, toConnectionName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        }

        // 'to' library name valid?
        if (!nameValidator.validate(toLibraryName)) {

            errorId = MemberValidationError.ERROR_TO_LIBRARY_NAME_NOT_VALID;
            errorMessage = Messages.bind(Messages.Invalid_library_name, toLibraryName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        }

        AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

        // Have 'to' library?
        if (!ISphereHelper.checkLibrary(toSystem, toLibraryName)) {

            errorId = MemberValidationError.ERROR_TO_LIBRARY_NOT_FOUND;
            errorMessage = Messages.bind(Messages.Library_A_not_found, toLibraryName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting. Default is: CANCEL
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
            return false;
        }

        // All fine. Continue with validation.
        return true;
    }

    private boolean isTargetFileValid(String fromConnectionName, String toConnectionName, CopyMemberItem member, ErrorContext errorContext) {

        String fromLibraryName = member.getFromLibrary();
        String fromFileName = member.getFromFile();

        String toLibraryName = member.getToLibrary();
        String toFileName = member.getToFile();

        RemoteObject toObject = RemoteObject.newFile(toConnectionName, toFileName, toLibraryName);

        MemberValidationError errorId;
        String errorMessage;

        // Validate file...
        Validator nameValidator = Validator.getNameInstance(toCcsid);

        // 'to' file name valid?
        if (!nameValidator.validate(toFileName)) {

            errorId = MemberValidationError.ERROR_TO_FILE_NAME_NOT_VALID;
            errorMessage = Messages.bind(Messages.Invalid_file_name, toFileName);
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting.
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        }

        // Have 'to'file?
        boolean haveToFile = false;

        AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);
        if (!ISphereHelper.checkFile(toSystem, toLibraryName, toFileName)) {

            errorId = MemberValidationError.ERROR_TO_FILE_NOT_FOUND;
            errorMessage = Messages.bind(Messages.File_A_not_found, toObject.getQSYSName());
            setMemberError(errorId, errorContext, errorMessage);

            // Check user voting.
            if (isCanceled()) {
                return false;
            } else {
                // Continue, error has or will bee fixed.
            }
        } else {
            haveToFile = true;
        }

        // Do extended validation checks, if file exists.
        if (haveToFile && !ignoreDataLostError) {

            int fromLength = -1;
            int toLength = -1;

            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            if (isFile(fromSystem, fromLibraryName, fromFileName)) {
                fromLength = getLineLength(fromSystem, fromLibraryName, fromFileName);
                if (fromLength < 0) {

                    errorId = MemberValidationError.ERROR_FROM_FILE_NOT_FOUND;
                    errorMessage = Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                        new String[] { fromFileName, fromLibraryName, "SRCDTA" });
                    setMemberError(errorId, errorContext, errorMessage);

                    // Check user voting.
                    if (isCanceled()) {
                        return false;
                    } else {
                        // Continue, but keep the error for this file.
                        // Other members might get copied.
                        return true;
                    }
                }
            }

            if (isFile(toSystem, toLibraryName, toFileName)) {
                toLength = getLineLength(toSystem, toLibraryName, toFileName);
                if (toLength < 0) {

                    errorId = MemberValidationError.ERROR_TO_FILE_NOT_FOUND;
                    errorMessage = Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A,
                        new String[] { toFileName, toLibraryName, "SRCDTA" });
                    setMemberError(errorId, errorContext, errorMessage);

                    // Check user voting.
                    if (isCanceled()) {
                        return false;
                    } else {
                        // Continue, but keep the error for this file.
                        // Other members might get copied.
                        return true;
                    }
                }
            }

            // Is line length of target file OK?
            if (!(fromLength <= toLength)) {
                Object[] values = new Object[] { fromLength, fromLibraryName, fromFileName, toLength, toLibraryName, toFileName };

                errorId = MemberValidationError.ERROR_TO_FILE_DATA_LOST;
                errorMessage = Messages.bind(
                    Messages.Data_lost_error_From_source_line_length_A_of_file_B_C_is_longer_than_target_source_line_length_D_of_file_E_F, values);
                setMemberError(errorId, errorContext, errorMessage);

                // Check user voting.
                if (isCanceled()) {
                    return false;
                } else {
                    // Continue, but keep the error for this file.
                    // Other members might get copied.
                    return true;
                }
            }
        }

        // All fine. Continue with validation.
        return true;
    }

    private boolean hasConnection(String connectionName) {

        Set<String> connectionNames = new HashSet<String>(Arrays.asList(IBMiHostContributionsHandler.getConnectionNames()));
        boolean hasConnection = connectionNames.contains(connectionName);

        return hasConnection;
    }

    private int getLineLength(AS400 system, String libraryName, String fileName) {

        RecordFormatDescriptionsStore store = new RecordFormatDescriptionsStore(system);
        RecordFormatDescription recordFormatDescription = store.get(fileName, libraryName);
        FieldDescription fieldDescription = recordFormatDescription.getFieldDescription("SRCDTA");
        if (fieldDescription == null) {
            return -1;
        }

        int fieldLength = fieldDescription.getLength();

        return fieldLength;
    }

    private boolean isFile(AS400 system, String libraryName, String fileName) {

        boolean isFile = ISphereHelper.checkFile(system, libraryName, fileName);

        return isFile;
    }

    private boolean isMember(AS400 system, String libraryName, String fileName, String memberName) {

        boolean isMember = ISphereHelper.checkMember(system, libraryName, fileName, memberName);

        return isMember;
    }

    private void setMemberError(MemberValidationError errorId, ErrorContext errorContext, String errorMessage) {

        if (errorId == MemberValidationError.ERROR_NONE) {
            throw new IllegalArgumentException("Update Javadoc in IValidateItemMessageListener, if you want to allow: " + errorId.name());
        }

        CopyMemberItem member = errorContext.getCopyMemberItem();

        member.setErrorMessage(errorMessage);

        if (itemMessageListeners != null) {
            for (IValidateItemMessageListener errorListener : itemMessageListeners) {
                SynchronizeMembersAction response = errorListener.reportValidateMemberMessage(errorId, member, errorMessage);
                if (response == SynchronizeMembersAction.CANCEL) {
                    member.setErrorMessage(errorMessage);
                    validationResult.addError();
                    validationResult.setCancel(errorId, errorMessage);
                    cancelOperation();
                } else if (response == SynchronizeMembersAction.CONTINUE_WITH_ERROR) {
                    member.setErrorMessage(errorMessage);
                    validationResult.addError();
                } else {
                    // Continue
                }
            }
        }
    }

    private void reportMemberValidated(CopyMemberItem member) {

        MemberValidationError errorId = MemberValidationError.ERROR_NONE;

        validationResult.addProcessed();

        if (itemMessageListeners != null) {
            for (IValidateItemMessageListener errorListener : itemMessageListeners) {
                SynchronizeMembersAction response = errorListener.reportValidateMemberMessage(errorId, member, null);
                if (response == SynchronizeMembersAction.CANCEL) {
                    String errorMessage = Messages.Operation_has_been_canceled_by_the_user;
                    validationResult.setCancel(errorId, errorMessage);
                    cancelOperation();
                }
            }
        }
    }

    private Set<IValidateItemMessageListener> getItemMessageListeners() {

        if (itemMessageListeners == null) {
            itemMessageListeners = new HashSet<IValidateItemMessageListener>();
        }

        return itemMessageListeners;
    }

    public boolean isError() {
        return validationResult.isError();
    }

    public int getCountTotal() {
        return validationResult.getTotal();
    }

    public int getCountSkipped() {
        return validationResult.getSkipped();
    }

    public int getMembersCopiedCount() {
        return validationResult.getProcessed();
    }

    public int getMembersErrorCount() {
        return validationResult.getErrors();
    }

    public long getAverageTime() {
        return validationResult.getAverageTime();
    }

    public void cancelOperation() {
        monitor.setCanceled(true);
    }

    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    private class ValidationResult extends AbstractResult<MemberValidationError> {
    }
}
