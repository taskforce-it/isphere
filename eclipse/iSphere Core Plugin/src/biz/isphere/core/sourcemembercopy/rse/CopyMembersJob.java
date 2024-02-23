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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;

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
    private ICopyMembersPostRun postRun;

    private Map<String, Boolean> haveTargetFileCheckResult;
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
        this.postRun = postRun;

        this.haveTargetFileCheckResult = new HashMap<String, Boolean>();
    }

    public void setMissingFileAction(MissingFileAction missingFileAction) {
        this.missingFileAction = missingFileAction;
    }

    public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
        this.existingMemberAction = existingMemberAction;
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

            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

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

                boolean canCopy;
                if (isMember(toSystem, member.getToLibrary(), member.getToFile(), member.getToMember())) {
                    if (ExistingMemberAction.RENAME.equals(existingMemberAction)) {
                        canCopy = performRenameMember(toSystem, member, errorContext);
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

                boolean isCopied;
                if (canCopy) {
                    if (haveTargetFile(fromConnectionName, toConnectionName, member, errorContext)) {
                        isCopied = member.performCopyOperation(fromConnectionName, toConnectionName);
                    } else {
                        isCopied = false;
                    }
                } else {
                    isCopied = false;
                }

                if (isCopied) {
                    reportMemberCopied(member);
                    copyResult.addProcessed();
                }
            }

        } finally {
            copyResult.finished();
            subMonitor.done();
            if (postRun != null) {
                postRun.returnCopyMembersResult(monitor.isCanceled(), copyResult.getTotal(), copyResult.getSkipped(), copyResult.getProcessed(),
                    copyResult.getErrors(), copyResult.getAverageTime(), copyResult.getCancelErrorId(), copyResult.getCancelMessage());
            }
        }
    }

    private boolean haveTargetFile(String fromConnectionName, String toConnectionName, CopyMemberItem copyMemberItem, ErrorContext errorContext) {

        String fromLibraryName = copyMemberItem.getFromLibrary();
        String fromFileName = copyMemberItem.getFromFile();
        String toLibraryName = copyMemberItem.getToLibrary();
        String toFileName = copyMemberItem.getToFile();

        String toQualifiedToFileName = String.format("%s/%s", toLibraryName, toFileName);

        boolean haveTargetFile = false;

        String targetFileKey = String.format("%s:%s/%s", toConnectionName, toLibraryName, toFileName);
        if (!haveTargetFileCheckResult.containsKey(targetFileKey)) {

            AS400 system = IBMiHostContributionsHandler.getSystem(toConnectionName);
            if (isFile(system, toLibraryName, toFileName)) {
                haveTargetFile = true;
            } else {

                boolean doCreateMissingFile;
                if (MissingFileAction.ERROR.equals(missingFileAction)) {
                    doCreateMissingFile = false;
                } else if (MissingFileAction.CREATE.equals(missingFileAction)) {
                    doCreateMissingFile = true;
                } else {
                    String[] messages = new String[] { toQualifiedToFileName, "Create missing file?" };
                    if (MessageDialogAsync.displayBlockingConfirmation(messages) == IDialogConstants.OK_ID) {
                        doCreateMissingFile = true;
                    } else {
                        doCreateMissingFile = false;
                    }
                }

                if (doCreateMissingFile) {
                    RemoteObject templateFile = RemoteObject.newFile(fromConnectionName, fromFileName, fromLibraryName);
                    RemoteObject newFile = createMissingSourceFileFromTemplate(toConnectionName, toLibraryName, toFileName, templateFile);
                    if (newFile != null) {
                        // File successfully created...
                        haveTargetFile = true;
                    }
                }
            }

        } else {
            haveTargetFile = haveTargetFileCheckResult.get(targetFileKey);
        }

        // Store result
        haveTargetFileCheckResult.put(targetFileKey, haveTargetFile);

        // Target file does not exist
        if (!haveTargetFile) {
            setMemberError(MemberCopyError.ERROR_TO_FILE_NOT_FOUND, errorContext, Messages.bind(Messages.File_A_not_found, toQualifiedToFileName));
        }

        return haveTargetFile;
    }

    private boolean isFile(AS400 system, String libraryName, String fileName) {

        boolean isFile = ISphereHelper.checkFile(system, libraryName, fileName);

        return isFile;
    }

    private boolean isMember(AS400 system, String libraryName, String fileName, String memberName) {

        boolean isMember = ISphereHelper.checkMember(system, libraryName, fileName, memberName);

        return isMember;
    }

    private RemoteObject createMissingSourceFileFromTemplate(String connectionName, String libraryName, String fileName, RemoteObject template) {

        AS400 templateSystem = IBMiHostContributionsHandler.getSystem(template.getConnectionName());
        int recordLength = getRecordLength(templateSystem, template.getLibrary(), template.getName());

        String description = template.getDescription();
        if (description == null) {
            AS400 system = IBMiHostContributionsHandler.getSystem(template.getConnectionName());
            String templateLibraryName = template.getLibrary();
            String templateFileName = template.getName();
            try {
                template = ISphereHelper.resolveFile(system, templateLibraryName, templateFileName);
                description = template.getDescription();
            } catch (Exception e) {
                ISpherePlugin.logError("*** Could not find template file " + templateFileName + " in library " + templateLibraryName + " ***", e);
                MessageDialogAsync.displayNonBlockingError(null, "Unexpected exception. See Eclipse error log.");
            }
        }

        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            String command = String.format("CRTSRCPF FILE(%s/%s) RCDLEN(%s) TEXT('%s')", libraryName, fileName, recordLength, description);
            List<AS400Message> rtnMessages = new LinkedList<AS400Message>();
            String message = ISphereHelper.executeCommand(system, command, rtnMessages);
            if (!StringHelper.isNullOrEmpty(message)) {
                ISphereHelper.displayCommandExecutionError(command, rtnMessages);
                return null;
            }

            RemoteObject newFile = ISphereHelper.resolveFile(system, libraryName, fileName);
            newFile.setConnectionName(connectionName);
            return newFile;

        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not create source file " + fileName + " in library " + libraryName + " from template ***", e);
            MessageDialogAsync.displayNonBlockingError(null, "Unexpected exception. See Eclipse error log.");
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

        IMemberRenamingRule newNameRule = Preferences.getInstance().getMemberRenamingRule();
        RenameMemberActor actor = new RenameMemberActor(system, newNameRule);

        String library = copyMemberItem.getToLibrary();
        String file = copyMemberItem.getToFile();
        String member = copyMemberItem.getToMember();

        try {

            List<AS400Message> rtnMessages = new LinkedList<AS400Message>();
            QSYSObjectPathName newMember = actor.produceNewMemberName(library, file, member);

            String command = String.format("RNMM FILE(%s/%s) MBR(%s) NEWMBR(%s)", library, file, member, newMember.getMemberName()); //$NON-NLS-1$
            String message = ISphereHelper.executeCommand(system, command, rtnMessages);

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
            setMemberError(MemberCopyError.ERROR_TO_MEMBER_RENAME_EXCEPTION, errorContext, ExceptionHelper.getLocalizedMessage(e));
            return false;
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

    private class CopyResult extends AbstractResult<MemberCopyError> {
    };
}
