/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.jobs.ISynchronizeMembersPostRun;
import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator.MemberValidationError;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.IItemErrorListener;
import biz.isphere.core.sourcemembercopy.IValidateMembersPostRun;
import biz.isphere.core.sourcemembercopy.rse.CopyMembersJob;
import biz.isphere.core.sourcemembercopy.rse.ExistingMemberAction;

public class SynchronizeMembersJob extends Job {

    private boolean isActive;

    private DoSynchronizeMembers doSynchronizeMembers;
    private ISynchronizeMembersPostRun postRun;

    private SortedSet<MemberCompareItem> copyToLeftItems;
    private SortedSet<MemberCompareItem> copyToRightItems;

    private Map<Integer, String> copyToLeftErrors;
    private Map<Integer, String> copyToRightErrors;

    public SynchronizeMembersJob(RemoteObject leftFileOrLibrary, RemoteObject rightFileOrLibrary, ISynchronizeMembersPostRun postRun) {
        super(Messages.Copying_source_members);

        this.copyToLeftItems = new TreeSet();
        this.copyToRightItems = new TreeSet();

        this.copyToLeftErrors = new HashMap<Integer, String>();
        this.copyToRightErrors = new HashMap<Integer, String>();

        this.doSynchronizeMembers = new DoSynchronizeMembers(leftFileOrLibrary, rightFileOrLibrary, copyToLeftErrors, copyToRightErrors);
        this.postRun = postRun;
    }

    public void setItemErrorListener(IItemErrorListener itemErrorListener) {
        doSynchronizeMembers.setItemErrorListener(itemErrorListener);
    }

    public void addItem(MemberCompareItem compareItem, CompareOptions compareOptions) {

        if (compareItem.getCompareStatus(compareOptions) == MemberCompareItem.LEFT_MISSING) {
            copyToLeftItems.add(compareItem);
        } else if (compareItem.getCompareStatus(compareOptions) == MemberCompareItem.RIGHT_MISSING) {
            copyToRightItems.add(compareItem);
        }
    }

    public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
        this.doSynchronizeMembers.setExistingMemberAction(existingMemberAction);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {

        startProcess();

        try {

            doSynchronizeMembers.setCopyToLeftMembers(copyToLeftItems.toArray(new MemberCompareItem[copyToLeftItems.size()]));
            doSynchronizeMembers.setCopyToRightMembers(copyToRightItems.toArray(new MemberCompareItem[copyToRightItems.size()]));
            doSynchronizeMembers.setMonitor(monitor);

            doSynchronizeMembers.start();

            while (doSynchronizeMembers.isAlive()) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }

        } finally {
            postRun.returnResult(doSynchronizeMembers.isError(), doSynchronizeMembers.getMembersCopiedCount());
            endProcess();
        }

        return Status.OK_STATUS;
    }

    public void reportError(CopyMemberItem item, String errorMessage) {
        return;
    }

    public void cancelOperation() {
        if (doSynchronizeMembers != null) {
            doSynchronizeMembers.cancel();
        }
    }

    private void startProcess() {
        isActive = true;
    }

    private void endProcess() {
        isActive = false;
    }

    public boolean isCanceled() {
        return doSynchronizeMembers.isCanceled();
    }

    public boolean isError() {
        return doSynchronizeMembers.isError();
    }

    public int getMembersCopiedCount() {
        return doSynchronizeMembers.getMembersCopiedCount();
    }

    private class DoSynchronizeMembers extends Thread implements IValidateMembersPostRun, ICopyMembersPostRun {

        private RemoteObject leftFileOrLibrary;
        private RemoteObject rightFileOrLibrary;
        private MemberCompareItem[] copyToLeftItems;
        private MemberCompareItem[] copyToRightItems;

        private IProgressMonitor monitor;

        private boolean isCanceled;
        private IItemErrorListener itemErrorListener;

        private boolean isLeftValidationError;
        private boolean isRightValidationError;

        private int copiedToLeftCount;
        private int copiedToRightCount;
        private boolean isCopyToLeftError;
        private boolean isCopyToRightError;

        private ExistingMemberAction existingMemberAction;

        private boolean isError;
        private int countMembersCopied;

        public DoSynchronizeMembers(RemoteObject leftFileOrLibrary, RemoteObject rightFileOrLibrary, Map<Integer, String> copyToLeftErrors,
            Map<Integer, String> copyToRightErrors) {

            this.leftFileOrLibrary = leftFileOrLibrary;
            this.rightFileOrLibrary = rightFileOrLibrary;
            this.copyToLeftItems = new MemberCompareItem[0];
            this.copyToRightItems = new MemberCompareItem[0];

            this.existingMemberAction = ExistingMemberAction.ERROR;
        }

        public void setItemErrorListener(IItemErrorListener itemErrorListener) {
            this.itemErrorListener = itemErrorListener;
        }

        public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
            this.existingMemberAction = existingMemberAction;
        }

        public void setMonitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        public void setCopyToLeftMembers(MemberCompareItem[] compareIems) {
            this.copyToLeftItems = compareIems;
        }

        public void setCopyToRightMembers(MemberCompareItem[] compareIems) {
            this.copyToRightItems = compareIems;
        }

        @Override
        public void run() {

            isCanceled = false;

            isLeftValidationError = false;
            isRightValidationError = false;

            copiedToLeftCount = 0;
            copiedToRightCount = 0;
            isCopyToLeftError = false;
            isCopyToRightError = false;

            int totalSize = (copyToLeftItems.length + copyToRightItems.length) * 2;

            monitor.beginTask(Messages.Copying_dots, totalSize);

            try {

                CopyMemberItem[] toLeftMembers = createMemberArray(rightFileOrLibrary, leftFileOrLibrary, copyToLeftItems,
                    MemberCompareItem.LEFT_MISSING);
                CopyMemberItem[] toRightMembers = createMemberArray(leftFileOrLibrary, rightFileOrLibrary, copyToRightItems,
                    MemberCompareItem.RIGHT_MISSING);

                boolean isPreCheck = false;
                if (isPreCheck) {
                    isError = false;
                    validateMembers(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
                    isLeftValidationError = isError;

                    isError = false;
                    validateMembers(leftFileOrLibrary, leftFileOrLibrary, toRightMembers);
                    isRightValidationError = isError;
                }

                isError = false;
                copyToLeftOrRight(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
                copiedToRightCount = countMembersCopied;

                isError = false;
                copyToLeftOrRight(leftFileOrLibrary, leftFileOrLibrary, toRightMembers);
                copiedToLeftCount = countMembersCopied;

            } finally {
                monitor.done();
            }
        }

        private void validateMembers(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] copyMemberItems) {

            // TODO: implement validation
            return;
        }

        private void copyToLeftOrRight(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] copyMemberItems) {

            isError = false;
            countMembersCopied = 0;

            String fromConnectionName = fromFileOrLibrary.getConnectionName();
            String toConnectionName = toFileOrLibrary.getConnectionName();

            CopyMembersJob copyJob = new CopyMembersJob(fromConnectionName, toConnectionName, copyMemberItems, existingMemberAction, this);
            copyJob.addItemErrorListener(itemErrorListener);
            copyJob.runInSameThread(monitor);
        }

        private CopyMemberItem[] createMemberArray(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary,
            MemberCompareItem[] memberCompareItems, int compareStatus) {

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
                    toLibraryName = toFileOrLibrary.getLibrary();
                    toFileName = fromFileName;
                    toSourceType = fromSourceType;
                } else {
                    throw new IllegalArgumentException("Inavlid taget object type: " + toFileOrLibrary.getObjectType());
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
         * PostRun method called by {@link CopyMemberValidator} at the end of
         * the validation process.
         * 
         * @param errorId - value indicating the error type
         * @param errorMessage - error message text
         */
        public void returnResult(MemberValidationError errorId, String errorMessage) {

            if (errorId != MemberValidationError.ERROR_NONE) {
                isError = true;
            }

        }

        /**
         * PostRun methods called by {@link CopyMembersJob} at the end of the
         * copy member process.
         * 
         * @param isError - <code>true</code> if there was an error; otherwise
         *        <code>false</code>
         * @param countMembersCopied - number of members that have been copied
         * @param averageTime - average time it took for copying a member
         */
        public void returnResult(boolean isError, int countMembersCopied, long averageTime) {

            this.isError = isError;
            this.countMembersCopied = countMembersCopied;

            System.out.println("copyMembersPostRun:");
            System.out.println("isError: " + isError);
            System.out.println("countMembersCopied: " + countMembersCopied);
        }

        public void cancel() {
            monitor.setCanceled(true);
        }

        private boolean isCanceled() {

            if (monitor != null) {
                if (monitor.isCanceled()) {
                    isCanceled = true;
                }
            }

            return isCanceled;
        }

        public boolean isError() {
            return isLeftValidationError || isRightValidationError || isCopyToLeftError || isCopyToRightError;
        }

        public int getMembersCopiedCount() {
            return copiedToLeftCount + copiedToRightCount;
        }
    }
}
