/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.as400.access.AS400;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.jobs.ISynchronizeMembersPostRun;
import biz.isphere.core.objectsynchronization.rse.MemberCompareItem;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator;
import biz.isphere.core.sourcemembercopy.CopyMemberValidator.MemberValidationError;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.IItemMessageListener;
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

        this.copyToLeftItems = new TreeSet<MemberCompareItem>();
        this.copyToRightItems = new TreeSet<MemberCompareItem>();

        this.copyToLeftErrors = new HashMap<Integer, String>();
        this.copyToRightErrors = new HashMap<Integer, String>();

        this.doSynchronizeMembers = new DoSynchronizeMembers(leftFileOrLibrary, rightFileOrLibrary, copyToLeftErrors, copyToRightErrors);
        this.postRun = postRun;
    }

    public void setItemErrorListener(IItemMessageListener itemErrorListener) {
        doSynchronizeMembers.setItemErrorListener(itemErrorListener);
    }

    public void addItem(MemberCompareItem compareItem, CompareOptions compareOptions) {

        if (compareItem.getCompareStatus(compareOptions) == MemberCompareItem.LEFT_MISSING) {
            copyToLeftItems.add(compareItem);
        } else if (compareItem.getCompareStatus(compareOptions) == MemberCompareItem.RIGHT_MISSING) {
            copyToRightItems.add(compareItem);
        }
    }

    public int getNumCopyLeftToRight() {
        return copyToRightItems.size();
    }

    public int getNumCopyRightToLeft() {
        return copyToLeftItems.size();
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
            if (doSynchronizeMembers.isError()) {
                postRun.returnResultPostRun(SynchronizationResult.ERROR, doSynchronizeMembers.getMembersCopiedCount(),
                    doSynchronizeMembers.getMessage());
            } else if (doSynchronizeMembers.isCanceled()) {
                postRun.returnResultPostRun(SynchronizationResult.CANCELED, doSynchronizeMembers.getMembersCopiedCount(),
                    doSynchronizeMembers.getMessage());
            } else if (!doSynchronizeMembers.isError()) {
                postRun.returnResultPostRun(SynchronizationResult.OK, doSynchronizeMembers.getMembersCopiedCount(), null);
            }
            endProcess();
        }

        return Status.OK_STATUS;
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
        private int totalWorked;

        private boolean isCanceled;
        private IItemMessageListener itemErrorListener;

        private boolean isLeftValidationError;
        private boolean isRightValidationError;

        private int copiedToLeftCount;
        private int copiedToRightCount;
        private boolean isCopyToLeftError;
        private boolean isCopyToRightError;

        private String errorMessage;

        private ExistingMemberAction existingMemberAction;

        // Set is returnResult() callbacks.
        private boolean isValidationError;
        private boolean isCopyError;

        private int countMembersCopied;

        public DoSynchronizeMembers(RemoteObject leftFileOrLibrary, RemoteObject rightFileOrLibrary, Map<Integer, String> copyToLeftErrors,
            Map<Integer, String> copyToRightErrors) {

            if (!leftFileOrLibrary.getObjectType().equals(rightFileOrLibrary.getObjectType())) {
                throw new IllegalArgumentException(
                    String.format("Object types do not match: %s vs. %s", leftFileOrLibrary.getObjectType(), rightFileOrLibrary.getObjectType()));
            }

            this.leftFileOrLibrary = leftFileOrLibrary;
            this.rightFileOrLibrary = rightFileOrLibrary;
            this.copyToLeftItems = new MemberCompareItem[0];
            this.copyToRightItems = new MemberCompareItem[0];

            this.existingMemberAction = ExistingMemberAction.ERROR;
        }

        public void setItemErrorListener(IItemMessageListener itemErrorListener) {
            this.itemErrorListener = itemErrorListener;
        }

        public void setExistingMemberAction(ExistingMemberAction existingMemberAction) {
            this.existingMemberAction = existingMemberAction;
        }

        public void setMonitor(IProgressMonitor monitor) {
            this.monitor = SubMonitor.convert(monitor);
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

            // Size is 2 times the number of members because of:
            // - validation
            // - copy
            int totalSize = (copyToLeftItems.length + copyToRightItems.length) * 2;
            totalWorked = 0;

            monitor.beginTask(Messages.Copying_dots, totalSize);

            try {

                CopyMemberItem[] toLeftMembers = createMemberArray(leftFileOrLibrary, copyToLeftItems, MemberCompareItem.LEFT_MISSING);
                CopyMemberItem[] toRightMembers = createMemberArray(rightFileOrLibrary, copyToRightItems, MemberCompareItem.RIGHT_MISSING);

                if (leftFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                    validateFileSync(toLeftMembers, toRightMembers);
                } else if (leftFileOrLibrary.getObjectType().equals(ISeries.LIB)) {
                    validateLibrarySync(toLeftMembers, toRightMembers);
                } else {
                    throw new IllegalArgumentException("Invalid taget object type: " + leftFileOrLibrary.getObjectType());
                }

                if (monitor.isCanceled()) {
                    return;
                }

                if (leftFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                    copyFileSync(toLeftMembers, toRightMembers);
                } else if (leftFileOrLibrary.getObjectType().equals(ISeries.LIB)) {
                    copyLibrarySync(toLeftMembers, toRightMembers);
                } else {
                    throw new IllegalArgumentException("Invalid taget object type: " + leftFileOrLibrary.getObjectType());
                }

                if (monitor.isCanceled()) {
                    return;
                }

            } finally {
                monitor.done();
            }
        }

        private void copyLibrarySync(CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

            isCopyToLeftError = copyFileOfLibrarySync(leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
            if (monitor.isCanceled()) {
                return;
            }

            isCopyToRightError = copyFileOfLibrarySync(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
            if (monitor.isCanceled()) {
                return;
            }
        }

        private boolean copyFileOfLibrarySync(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] toRMembers) {

            boolean isCopyFileError = false;

            List<CopyMemberItem> tempMembers = new LinkedList<CopyMemberItem>();
            String lastFileName = null;
            for (CopyMemberItem copyMemberItem : toRMembers) {

                String fileName = copyMemberItem.getFromFile();
                if (lastFileName == null) {
                    lastFileName = fileName;
                    tempMembers.clear();
                }

                if (fileName.equals(lastFileName)) {
                    tempMembers.add(copyMemberItem);
                } else {

                    boolean isCopyMemberError = copyChunkOfLibrarySync(fromFileOrLibrary, toFileOrLibrary, tempMembers, lastFileName);
                    if (isCopyFileError == false) {
                        isCopyFileError = isCopyMemberError;
                    }
                    lastFileName = null;
                    tempMembers.clear();

                    tempMembers.add(copyMemberItem);
                    lastFileName = fileName;
                }
            }

            if (lastFileName != null && tempMembers.size() > 0) {
                boolean isCopyMemberError = copyChunkOfLibrarySync(fromFileOrLibrary, toFileOrLibrary, tempMembers, lastFileName);
                if (isCopyFileError == false) {
                    isCopyFileError = isCopyMemberError;
                }
            }

            return isCopyFileError;
        }

        private boolean copyChunkOfLibrarySync(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, List<CopyMemberItem> tempMembers,
            String lastFileName) {

            RemoteObject fromFile = resolveFile(fromFileOrLibrary.getConnectionName(), fromFileOrLibrary.getName(), lastFileName);

            String toConnectionName = toFileOrLibrary.getConnectionName();
            String toLibraryName = toFileOrLibrary.getName();
            String toFileName = lastFileName;

            RemoteObject toFile = new RemoteObject(toConnectionName, toFileName, toLibraryName, ISeries.FILE, "");

            boolean isCopyMemberError = copyToLeftOrRight(fromFile, toFile, tempMembers.toArray(new CopyMemberItem[tempMembers.size()]));

            return isCopyMemberError;
        }

        private boolean copyFileSync(CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

            isCopyToLeftError = copyToLeftOrRight(leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
            copiedToLeftCount = countMembersCopied;

            if (monitor.isCanceled()) {
                return true;
            }

            isCopyToRightError = copyToLeftOrRight(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
            copiedToRightCount = countMembersCopied;

            if (monitor.isCanceled()) {
                return true;
            }

            return false; // no error
        }

        private void validateLibrarySync(CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

            isLeftValidationError = validateFileOfLibrarySync(leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
            if (monitor.isCanceled()) {
                return;
            }

            isRightValidationError = validateFileOfLibrarySync(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
            if (monitor.isCanceled()) {
                return;
            }
        }

        private boolean validateFileOfLibrarySync(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] toRMembers) {

            boolean isFileValidationError = false;

            List<CopyMemberItem> tempMembers = new LinkedList<CopyMemberItem>();
            String lastFileName = null;
            for (CopyMemberItem copyMemberItem : toRMembers) {

                if (isCanceled()) {
                    break;
                }

                String fileName = copyMemberItem.getFromFile();
                if (lastFileName == null) {
                    lastFileName = fileName;
                    tempMembers.clear();
                }

                if (fileName.equals(lastFileName)) {
                    tempMembers.add(copyMemberItem);
                } else {

                    boolean isMemberValidationError = validateChunkOfLibrarySync(fromFileOrLibrary, toFileOrLibrary, tempMembers, lastFileName);
                    if (isFileValidationError == false) {
                        isFileValidationError = isMemberValidationError;
                    }
                    lastFileName = null;
                    tempMembers.clear();

                    tempMembers.add(copyMemberItem);
                    lastFileName = fileName;
                }
            }

            if (!isCanceled()) {
                if (lastFileName != null && tempMembers.size() > 0) {
                    boolean isMemberValidationError = validateChunkOfLibrarySync(fromFileOrLibrary, toFileOrLibrary, tempMembers, lastFileName);
                    if (isFileValidationError == false) {
                        isFileValidationError = isMemberValidationError;
                    }
                }
            }

            return isFileValidationError;
        }

        private boolean validateChunkOfLibrarySync(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, List<CopyMemberItem> tempMembers,
            String lastFileName) {

            RemoteObject fromFile = resolveFile(fromFileOrLibrary.getConnectionName(), fromFileOrLibrary.getName(), lastFileName);

            String toConnectionName = toFileOrLibrary.getConnectionName();
            String toLibraryName = toFileOrLibrary.getName();
            String toFileName = lastFileName;

            RemoteObject toFile = new RemoteObject(toConnectionName, toFileName, toLibraryName, ISeries.FILE, "");

            boolean isMemberValidationError = validateMembers(fromFile, toFile, tempMembers.toArray(new CopyMemberItem[tempMembers.size()]));

            return isMemberValidationError;
        }

        private boolean validateFileSync(CopyMemberItem[] toLeftMembers, CopyMemberItem[] toRightMembers) {

            isLeftValidationError = validateMembers(rightFileOrLibrary, leftFileOrLibrary, toLeftMembers);
            if (monitor.isCanceled()) {
                return true;
            }

            isRightValidationError = validateMembers(leftFileOrLibrary, rightFileOrLibrary, toRightMembers);
            if (monitor.isCanceled()) {
                return true;
            }

            return false; // no error
        }

        private boolean validateMembers(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] copyMemberItems) {

            // Updated in
            isValidationError = false;

            String fromConnectionName = fromFileOrLibrary.getConnectionName();

            // Report all errors to the caller
            boolean ignoreDataLostError = false;
            boolean ignoreUnsavedChangesError = false;
            boolean fullErrorCheck = true;

            CopyMemberValidator validatorJob = new CopyMemberValidator(fromConnectionName, copyMemberItems, existingMemberAction, ignoreDataLostError,
                ignoreUnsavedChangesError, fullErrorCheck, this);
            validatorJob.addItemErrorListener(itemErrorListener);

            String toLibraryName;
            String toFileName;

            if (toFileOrLibrary.getObjectType().equals(ISeries.FILE)) {
                toLibraryName = toFileOrLibrary.getLibrary();
                toFileName = toFileOrLibrary.getName();
            } else {
                throw new IllegalArgumentException("Invalid taget object type: " + toFileOrLibrary.getObjectType());
            }

            validatorJob.setToConnectionName(toFileOrLibrary.getConnectionName());
            validatorJob.setToLibraryName(toLibraryName);
            validatorJob.setToFileName(toFileName);
            validatorJob.setToCcsid(getSystemCcsid(toFileOrLibrary.getConnectionName()));

            validatorJob.runInSameThread(monitor);

            totalWorked = totalWorked + copyMemberItems.length;
            monitor.worked(totalWorked);

            return isValidationError;
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

        private boolean copyToLeftOrRight(RemoteObject fromFileOrLibrary, RemoteObject toFileOrLibrary, CopyMemberItem[] copyMemberItems) {

            isCopyError = false;
            countMembersCopied = 0;

            String fromConnectionName = fromFileOrLibrary.getConnectionName();
            String toConnectionName = toFileOrLibrary.getConnectionName();

            CopyMembersJob copyJob = new CopyMembersJob(fromConnectionName, toConnectionName, copyMemberItems, existingMemberAction, this);
            copyJob.addItemErrorListener(itemErrorListener);
            copyJob.runInSameThread(monitor);

            totalWorked = totalWorked + copyMemberItems.length;
            monitor.worked(totalWorked);

            return isCopyError;
        }

        private CopyMemberItem[] createMemberArray(RemoteObject toFileOrLibrary, MemberCompareItem[] memberCompareItems, int compareStatus) {

            SortedSet<CopyMemberItem> membersToCopy = new TreeSet<CopyMemberItem>();

            for (MemberCompareItem compareItem : memberCompareItems) {

                // Continue on errors from the copy member validator.
                if (compareItem.isError()) {
                    continue;
                }

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
         * PostRun method called by {@link CopyMemberValidator} at the end of
         * the validation process.
         * 
         * @param errorId - value indicating the error type
         * @param errorMessage - error message text
         */
        public void returnResult(MemberValidationError errorId, String errorMessage) {

            if (errorId == MemberValidationError.ERROR_CANCELED) {
                if (StringHelper.isNullOrEmpty(this.errorMessage)) {
                    this.errorMessage = errorMessage;
                }
                return;
            }

            /*
             * Set error marker, so that validateMembers() can return true or
             * false.
             */
            if (errorId != MemberValidationError.ERROR_NONE) {
                this.errorMessage = errorMessage;
                isValidationError = true;
            }

            debug("\nSynchronizeMembersJob.validateMembersPostRun:");
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

            /*
             * Set error marker, so that copyToLeftOrRight() can return true or
             * false.
             */
            this.isCopyError = isError;
            this.countMembersCopied = countMembersCopied;

            debug("\nSynchronizeMembersJob.copyMembersPostRun:");
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

        public String getMessage() {
            return errorMessage;
        }

        public int getMembersCopiedCount() {
            return copiedToLeftCount + copiedToRightCount;
        }

    }

    private void debug(String message) {
        System.out.println(message);
    }
}
