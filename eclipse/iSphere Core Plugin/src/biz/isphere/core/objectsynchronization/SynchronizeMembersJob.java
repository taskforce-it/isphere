/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.as400.access.AS400;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.jobs.ICancelableJob;
import biz.isphere.core.objectsynchronization.jobs.ISynchronizeMembersPostRun;
import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.ICopyItemMessageListener;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.IValidateItemMessageListener;
import biz.isphere.core.sourcemembercopy.IValidateMembersPostRun;
import biz.isphere.core.sourcemembercopy.MemberCopyError;
import biz.isphere.core.sourcemembercopy.MemberValidationError;
import biz.isphere.core.sourcemembercopy.ValidateMembersJob;
import biz.isphere.core.sourcemembercopy.rse.CopyMembersJob;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;
import biz.isphere.core.sourcemembercopy.rse.MissingFileAction;

public class SynchronizeMembersJob extends Job implements ICancelableJob, IValidateMembersPostRun, ICopyMembersPostRun {

    private RemoteObject leftFileOrLibrary;
    private RemoteObject rightFileOrLibrary;
    private ISynchronizeMembersPostRun postRun;

    private SortedSet<MemberCompareItem> copyToLeftItems;
    private SortedSet<MemberCompareItem> copyToRightItems;

    private IProgressMonitor monitor;

    private IValidateItemMessageListener validateItemErrorListener;
    private ICopyItemMessageListener copyItemErrorListener;

    private SyncResult syncResult;

    private MissingFileAction missingFileAction;
    private ExistingMemberAction existingMemberAction;

    public SynchronizeMembersJob(RemoteObject leftFileOrLibrary, RemoteObject rightFileOrLibrary, ISynchronizeMembersPostRun postRun) {
        super(Messages.Copying_source_members);

        if (!leftFileOrLibrary.getObjectType().equals(rightFileOrLibrary.getObjectType())) {
            throw new IllegalArgumentException(
                String.format("Object types do not match: %s vs. %s", leftFileOrLibrary.getObjectType(), rightFileOrLibrary.getObjectType()));
        }

        this.leftFileOrLibrary = leftFileOrLibrary;
        this.rightFileOrLibrary = rightFileOrLibrary;
        this.postRun = postRun;

        this.copyToLeftItems = new TreeSet<MemberCompareItem>();
        this.copyToRightItems = new TreeSet<MemberCompareItem>();

        this.missingFileAction = missingFileAction.ASK_USER;
        this.existingMemberAction = ExistingMemberAction.ERROR;

        this.syncResult = null;
    }

    public void setValidateItemErrorListener(IValidateItemMessageListener itemErrorListener) {
        this.validateItemErrorListener = itemErrorListener;
    }

    public void setCopyItemErrorListener(ICopyItemMessageListener itemErrorListener) {
        this.copyItemErrorListener = itemErrorListener;
    }

    public void setMissingFileAction(MissingFileAction missingFileAction) {
        this.missingFileAction = missingFileAction;
    }

    public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
        this.existingMemberAction = existingMemberAction;
    }

    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public void addCopyRightToLeftMember(MemberCompareItem compareIems) {
        this.copyToLeftItems.add(compareIems);
    }

    public void addCopyLeftToRightMember(MemberCompareItem compareIems) {
        this.copyToRightItems.add(compareIems);
    }

    public int getNumCopyRightToLeft() {
        return copyToLeftItems.size();
    }

    public int getNumCopyLeftToRight() {
        return copyToRightItems.size();
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {

        this.monitor = monitor;

        try {

            syncResult = new SyncResult();

            CopyMemberItem[] toLeftMembers = createMemberArray(leftFileOrLibrary, copyToLeftItems, MemberCompareItem.LEFT_MISSING);
            CopyMemberItem[] toRightMembers = createMemberArray(rightFileOrLibrary, copyToRightItems, MemberCompareItem.RIGHT_MISSING);

            int totalMembers = toLeftMembers.length + toRightMembers.length;
            SubMonitor progress = SubMonitor.convert(monitor).setWorkRemaining(totalMembers * 2);

            SubMonitor subMonitorValidate = progress.split(totalMembers).setWorkRemaining(totalMembers);

            if (leftFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                validateFileSync(subMonitorValidate, toLeftMembers, toRightMembers);
            } else if (leftFileOrLibrary.getObjectType().equals(ISeries.LIB)) {
                validateLibrarySync(subMonitorValidate, toLeftMembers, toRightMembers);
            } else {
                throw new IllegalArgumentException("Invalid taget object type: " + leftFileOrLibrary.getObjectType());
            }

            if (isCanceled()) {
                return Status.OK_STATUS;
            }

            SubMonitor subMonitorCopy = progress.split(totalMembers).setWorkRemaining(totalMembers);

            if (leftFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                copyFileSync(subMonitorCopy, toLeftMembers, toRightMembers);
            } else if (leftFileOrLibrary.getObjectType().equals(ISeries.LIB)) {
                copyLibrarySync(subMonitorCopy, toLeftMembers, toRightMembers);
            } else {
                throw new IllegalArgumentException("Invalid taget object type: " + leftFileOrLibrary.getObjectType());
            }

            if (isCanceled()) {
                return Status.OK_STATUS;
            }

        } finally {
            monitor.done();

            if (isError()) {
                postRun.synchronizeMembersPostRun(SynchronizationResult.ERROR, getCountCopied(), getCountErrors(), getMemberErrorMessage());
            } else {
                if (isCanceled()) {
                    postRun.synchronizeMembersPostRun(SynchronizationResult.CANCELED, getCountCopied(), getCountErrors(),
                        Messages.Operation_has_been_canceled_by_the_user);
                } else {
                    postRun.synchronizeMembersPostRun(SynchronizationResult.OK, getCountCopied(), getCountErrors(), getJobSuccessfulMessage());
                }
            }
        }

        return Status.OK_STATUS;
    }

    private SubMonitor getSubMonitor(SubMonitor subMonitor, int partialLength) {
        SubMonitor partialSubMonitor = subMonitor.split(partialLength).setWorkRemaining(partialLength);
        return partialSubMonitor;
    }

    private void copyLibrarySync(SubMonitor subMonitor, CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

        copyFilesOfLibrarySync(subMonitor, leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
        if (isCanceled()) {
            return;
        }

        copyFilesOfLibrarySync(subMonitor, rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
        if (isCanceled()) {
            return;
        }
    }

    private void copyFilesOfLibrarySync(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        CopyMemberItem[] toMembers) {

        List<CopyMemberItem> fileMembers = new LinkedList<CopyMemberItem>();
        String lastFileName = null;
        for (CopyMemberItem copyMemberItem : toMembers) {

            String fileName = copyMemberItem.getFromFile();
            if (lastFileName == null) {
                lastFileName = fileName;
                fileMembers.clear();
            }

            if (fileName.equals(lastFileName)) {
                fileMembers.add(copyMemberItem);
            } else {

                SubMonitor chunkSubMonitor = getSubMonitor(subMonitor, fileMembers.size());
                copyChunkOfLibrarySync(chunkSubMonitor, fromFileOrLibrary, toFileOrLibrary, fileMembers, lastFileName);

                lastFileName = null;
                fileMembers.clear();

                fileMembers.add(copyMemberItem);
                lastFileName = fileName;
            }
        }

        if (!isCanceled()) {
            if (lastFileName != null && fileMembers.size() > 0) {
                SubMonitor chunkSubMonitor = getSubMonitor(subMonitor, fileMembers.size());
                copyChunkOfLibrarySync(chunkSubMonitor, fromFileOrLibrary, toFileOrLibrary, fileMembers, lastFileName);
            }
        }

    }

    private void copyChunkOfLibrarySync(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        List<CopyMemberItem> tempMembers, String lastFileName) {

        RemoteObject fromFile = resolveFile(fromFileOrLibrary.getConnectionName(), fromFileOrLibrary.getName(), lastFileName);

        String toConnectionName = toFileOrLibrary.getConnectionName();
        String toLibraryName = toFileOrLibrary.getName();
        String toFileName = lastFileName;

        RemoteObject toFile = new RemoteObject(toConnectionName, toFileName, toLibraryName, ISeries.FILE, "");

        copyToLeftOrRight(subMonitor, fromFile, toFile, tempMembers.toArray(new CopyMemberItem[tempMembers.size()]));
    }

    private boolean copyFileSync(SubMonitor subMonitor, CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

        copyToLeftOrRight(subMonitor, leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
        if (isCanceled()) {
            return true;
        }

        copyToLeftOrRight(subMonitor, rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
        if (isCanceled()) {
            return true;
        }

        return false; // no error
    }

    private void validateLibrarySync(SubMonitor subMonitor, CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

        validateFilesOfLibrarySync(subMonitor, leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
        if (isCanceled()) {
            return;
        }

        validateFilesOfLibrarySync(subMonitor, rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
        if (isCanceled()) {
            return;
        }
    }

    private void validateFilesOfLibrarySync(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        CopyMemberItem[] toMembers) {

        List<CopyMemberItem> fileMembers = new LinkedList<CopyMemberItem>();
        String lastFileName = null;
        for (CopyMemberItem copyMemberItem : toMembers) {

            if (isCanceled()) {
                break;
            }

            String fileName = copyMemberItem.getFromFile();
            if (lastFileName == null) {
                lastFileName = fileName;
                fileMembers.clear();
            }

            if (fileName.equals(lastFileName)) {
                fileMembers.add(copyMemberItem);
            } else {

                SubMonitor chunkSubMonitor = getSubMonitor(subMonitor, fileMembers.size());
                validateChunkOfLibrarySync(chunkSubMonitor, fromFileOrLibrary, toFileOrLibrary, fileMembers, lastFileName);

                lastFileName = null;
                fileMembers.clear();

                fileMembers.add(copyMemberItem);
                lastFileName = fileName;
            }
        }

        if (!isCanceled()) {
            if (lastFileName != null && fileMembers.size() > 0) {

                SubMonitor chunkSubMonitor = getSubMonitor(subMonitor, fileMembers.size());
                validateChunkOfLibrarySync(chunkSubMonitor, fromFileOrLibrary, toFileOrLibrary, fileMembers, lastFileName);

            }
        }
    }

    private void validateChunkOfLibrarySync(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        List<CopyMemberItem> chunkOfMembers, String lastFileName) {

        RemoteObject fromFile = resolveFile(fromFileOrLibrary.getConnectionName(), fromFileOrLibrary.getName(), lastFileName);

        String toConnectionName = toFileOrLibrary.getConnectionName();
        String toLibraryName = toFileOrLibrary.getName();
        String toFileName = lastFileName;

        RemoteObject toFile = new RemoteObject(toConnectionName, toFileName, toLibraryName, ISeries.FILE, "");

        validateLeftOrRightMembers(subMonitor, fromFile, toFile, chunkOfMembers.toArray(new CopyMemberItem[chunkOfMembers.size()]));
    }

    private boolean validateFileSync(SubMonitor subMonitor, CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

        validateLeftOrRightMembers(subMonitor, rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
        if (isCanceled()) {
            return true;
        }

        validateLeftOrRightMembers(subMonitor, leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
        if (isCanceled()) {
            return true;
        }

        return false; // no error
    }

    private void validateLeftOrRightMembers(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        CopyMemberItem[] copyMemberItems) {

        String fromConnectionName = fromFileOrLibrary.getConnectionName();

        boolean ignoreDataLostError = false;
        boolean ignoreUnsavedChangesError = false;
        boolean fullErrorCheck = true;

        ValidateMembersJob validatorJob = new ValidateMembersJob(fromConnectionName, copyMemberItems, existingMemberAction, ignoreDataLostError,
            ignoreUnsavedChangesError, fullErrorCheck, this);
        validatorJob.addItemErrorListener(validateItemErrorListener);

        if (!toFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
            throw new IllegalArgumentException("Invalid taget object type: " + toFileOrLibrary.getObjectType());
        }

        validatorJob.setToConnectionName(toFileOrLibrary.getConnectionName());
        validatorJob.setToCcsid(getSystemCcsid(toFileOrLibrary.getConnectionName()));

        validatorJob.runInSameThread(subMonitor);
    }

    private RemoteObject resolveFile(String connectionName, String libraryName, String fileName) {

        try {

            AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
            RemoteObject remoteObject = ISphereHelper.resolveFile(system, libraryName, fileName);
            remoteObject.setConnectionName(connectionName);

            return remoteObject;

        } catch (Exception e) {
            ISpherePlugin.logError("*** File " + fileName + " not found in library " + libraryName + " ***", e);
            return null;
        }
    }

    private int getSystemCcsid(String connectionName) {
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system != null) {
            return system.getCcsid();
        }

        return -1;
    }

    private void copyToLeftOrRight(SubMonitor subMonitor, RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
        CopyMemberItem[] copyMemberItems) {

        String fromConnectionName = fromFileOrLibrary.getConnectionName();
        String toConnectionName = toFileOrLibrary.getConnectionName();

        CopyMembersJob copyMembersJob = new CopyMembersJob(fromConnectionName, toConnectionName, copyMemberItems, this);
        copyMembersJob.setExistingMemberAction(existingMemberAction);
        copyMembersJob.setMissingFileAction(missingFileAction);
        copyMembersJob.addItemErrorListener(copyItemErrorListener);

        copyMembersJob.runInSameThread(subMonitor);
    }

    private CopyMemberItem[] createMemberArray(RemoteObject toFileOrLibrary, SortedSet<MemberCompareItem> memberCompareItems, int compareStatus) {

        SortedSet<CopyMemberItem> membersToCopy = new TreeSet<CopyMemberItem>();

        for (MemberCompareItem compareItem : memberCompareItems) {

            String memberName = compareItem.getMemberName();

            String fromLibraryName;
            String fromFileName;
            String fromSourceType;

            MemberDescription fromMemberDescription;
            if (compareStatus == MemberCompareItem.LEFT_MISSING) {
                fromMemberDescription = compareItem.getRightMemberDescription();
            } else if (compareStatus == MemberCompareItem.RIGHT_MISSING) {
                fromMemberDescription = compareItem.getLeftMemberDescription();
            } else {
                continue;
            }

            fromLibraryName = fromMemberDescription.getLibraryName();
            fromFileName = fromMemberDescription.getFileName();
            fromSourceType = fromMemberDescription.getSourceType();

            String toLibraryName;
            String toFileName;
            String toSourceType;

            if (toFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                toLibraryName = toFileOrLibrary.getLibrary();
                toFileName = toFileOrLibrary.getName();
                toSourceType = fromSourceType;
            } else if (toFileOrLibrary.getObjectType().equals(ISeries.LIB)) {
                toLibraryName = toFileOrLibrary.getName();
                toFileName = fromFileName;
                toSourceType = fromSourceType;
            } else {
                throw new IllegalArgumentException("Invalid taget object type: " + toFileOrLibrary.getObjectType());
            }

            CopyMemberItem memberItem = new CopyMemberItem(fromFileName, fromLibraryName, memberName, fromSourceType);
            memberItem.setToLibrary(toLibraryName);
            memberItem.setToFile(toFileName);
            memberItem.setToSrcType(toSourceType);
            memberItem.setData(compareItem);
            membersToCopy.add(memberItem);
        }

        return membersToCopy.toArray(new CopyMemberItem[membersToCopy.size()]);
    }

    /**
     * PostRun method called by {@link ValidateMembersJob} at the end of the
     * validation process.
     * 
     * @param isCanceled - true, if the job has been canceled;otherwise false
     * @param countTotal - total number of members processed
     * @param countSkipped - number of members skipped
     * @param countValidated - number of members validated
     * @param countErrors - number of members that could not be processed
     * @param averageTime - average processing time per member
     */
    public void returnValidateMembersResult(boolean isCanceled, int countTotal, int countSkipped, int countValidated, int countErrors,
        long averageTime, MemberValidationError errorId, String cancelMessage) {

        syncResult.countErrors = syncResult.countErrors + countErrors;
        syncResult.countValidated = syncResult.countValidated + countValidated;

        debug("\nSynchronizeMembersJob.validateMembersPostRun:");
        debug("is canceled:    " + isCanceled);
        debug("total #:        " + countTotal);
        debug("skipped #:      " + countSkipped);
        debug("validated #:    " + countValidated);
        debug("errors #:       " + countErrors);
        debug("average time:   " + averageTime + "ms");
        debug("error id:       " + errorId);
        debug("cancel message: " + cancelMessage);
    }

    /**
     * PostRun methods called by {@link CopyMembersJob} at the end of the copy
     * member process.
     *
     * @param isCanceled - true, if the job has been canceled;otherwise false
     * @param countTotal - total number of members processed
     * @param countSkipped - number of members skipped
     * @param countProcessed - number of members processed fine
     * @param countErrors - number of members that could not be processed
     * @param averageTime - average processing time per member
     */
    public void returnCopyMembersResult(final boolean isCanceled, final int countTotal, final int countSkipped, final int countCopied,
        final int countErrors, final long averageTime, final MemberCopyError errorId, final String cancelMessage) {

        syncResult.countErrors = syncResult.countErrors + countErrors;
        syncResult.countCopied = syncResult.countCopied + countCopied;

        debug("\nSynchronizeMembersJob.copyMembersPostRun:");
        debug("is canceled:    " + isCanceled);
        debug("total #:        " + countTotal);
        debug("skipped #:      " + countSkipped);
        debug("copied #:       " + countCopied);
        debug("errors #:       " + countErrors);
        debug("average time:   " + averageTime + "ms");
        debug("error id:       " + errorId);
        debug("cancel message: " + cancelMessage);
    }

    private boolean isError() {
        if (syncResult.countErrors > 0) {
            return true;
        }
        return false;
    }

    private String getJobSuccessfulMessage() {
        return Messages.bind(Messages.Could_not_copy_A_members_due_to_errors, getCountErrors());
    }

    private String getMemberErrorMessage() {
        return Messages.bind(Messages.Could_not_copy_A_members_due_to_errors, getCountErrors());
    }

    private int getCountCopied() {
        int countCopied = syncResult.countCopied;
        return countCopied;
    }

    private int getCountErrors() {
        int countErrors = syncResult.countErrors;
        return countErrors;
    }

    public void cancelOperation() {
        monitor.setCanceled(true);
    }

    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    private void debug(String message) {
        System.out.println(message);
    }

    private class SyncResult {
        public int countValidated;
        public int countCopied;
        public int countErrors;
    }
}
