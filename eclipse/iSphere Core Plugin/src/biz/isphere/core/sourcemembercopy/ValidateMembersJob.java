/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.Messages;
import biz.isphere.core.sourcemembercopy.rse.CopyMembersJob;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;
import biz.isphere.core.sourcemembercopy.rse.MissingFileAction;

public class ValidateMembersJob extends Job implements ICopyItemMessageListener {

    private Set<IValidateItemMessageListener> itemMessageListeners;

    private String fromConnectionName;
    private CopyMemberItem[] members;
    private ExistingMemberAction existingMemberAction;
    private MissingFileAction missingFileAction;
    private boolean isIgnoreDataLostError;
    private boolean isIgnoreUnsavedChangesError;
    private boolean isFullErrorCheck;
    private boolean isRenameMemberCheck;
    private IValidateMembersPostRun postRun;

    private CopyMembersJob copyMembersJob;

    private IProgressMonitor monitor;

    private String toConnectionName;

    // private int countTotal;
    // private int countSkipped;
    // private int countProcessed;
    // private int countErrors;
    // private long averageTime;
    // private MemberCopyError cancelErrorId;
    // private String cancelMessage;

    private CopyResult copyResult;

    public ValidateMembersJob(String fromConnectionName, CopyMemberItem[] members, IValidateMembersPostRun postRun) {
        super(Messages.Validating_dots);

        this.itemMessageListeners = null;

        this.fromConnectionName = fromConnectionName;
        this.members = members;
        this.existingMemberAction = ExistingMemberAction.ERROR;
        this.missingFileAction = MissingFileAction.ERROR;
        this.isIgnoreDataLostError = false;
        this.isIgnoreUnsavedChangesError = false;
        this.isFullErrorCheck = false;
        this.isRenameMemberCheck = true;
        this.postRun = postRun;
    }

    public void addItemErrorListener(IValidateItemMessageListener listener) {
        getItemMessageListeners().add(listener);
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

    public void setToConnectionName(String connectionName) {
        this.toConnectionName = connectionName;
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

            copyMembersJob = new CopyMembersJob(this.fromConnectionName, this.toConnectionName, members, null);
            this.copyMembersJob.setValidationMode(true);
            this.copyMembersJob.setMissingFileAction(this.missingFileAction);
            this.copyMembersJob.setExistingMemberAction(this.existingMemberAction);
            this.copyMembersJob.setIgnoreDataLostError(this.isIgnoreDataLostError);
            this.copyMembersJob.setIgnoreUnsavedChanges(this.isIgnoreUnsavedChangesError);
            this.copyMembersJob.setFullErrorCheck(this.isFullErrorCheck);
            this.copyMembersJob.addItemErrorListener(this);
            copyMembersJob.runInSameThread(subMonitor);

        } finally {
            if (postRun != null) {
                postRun.returnValidateMembersResult(monitor.isCanceled(), copyResult.getTotal(), copyResult.getSkipped(), copyResult.getProcessed(),
                    copyResult.getErrors(), copyResult.getAverageTime(), copyResult.getCancelErrorId(), copyResult.getCancelMessage());
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

    public SynchronizeMembersAction reportCopyMemberMessage(MemberCopyError errorId, CopyMemberItem item, String errorMessage) {

        if (itemMessageListeners != null) {
            for (IValidateItemMessageListener listener : itemMessageListeners) {
                SynchronizeMembersAction response = listener.reportValidateMemberMessage(errorId, item, errorMessage);
                if (response == SynchronizeMembersAction.CANCEL) {
                    item.setErrorMessage(errorMessage);
                    copyResult.addError();
                    copyResult.setCancel(errorId, errorMessage);
                    cancelOperation();
                } else if (response == SynchronizeMembersAction.CONTINUE_WITH_ERROR) {
                    item.setErrorMessage(errorMessage);
                    copyResult.addError();
                } else {
                    // Continue
                }
            }
        }

        return errorId.getDefaultAction();
    }

    private class CopyResult extends AbstractResult<MemberCopyError> {
    };
}
