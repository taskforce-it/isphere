/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy.rse;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.ISphereHelper;
import biz.isphere.core.memberrename.RenameMemberActor;
import biz.isphere.core.memberrename.rules.IMemberRenamingRule;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.IItemErrorListener;

public class CopyMembersJob extends Job {

    private boolean isActive;

    private DoCopyMembers doCopyMembers;
    private ICopyMembersPostRun postRun;

    public CopyMembersJob(String fromConnectionName, String toConnectionName, CopyMemberItem[] members, ExistingMemberAction existingMemberAction,
        ICopyMembersPostRun postRun) {
        super(Messages.Copying_dots);

        this.doCopyMembers = new DoCopyMembers(fromConnectionName, toConnectionName, members, existingMemberAction);
        this.postRun = postRun;
    }

    public void runInSameThread(IProgressMonitor monitor) {
        doCopyMembers.setMonitor(monitor);
        startProcess();
        doCopyMembers.run();
        postRun.returnResult(doCopyMembers.isError(), doCopyMembers.getMembersCopiedCount(), doCopyMembers.getAverageTime());
        endProcess();
    }

    public void addItemErrorListener(IItemErrorListener itemErrorListener) {
        doCopyMembers.addItemErrorListener(itemErrorListener);
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public IStatus run(IProgressMonitor monitor) {

        startProcess();

        try {

            doCopyMembers.setMonitor(monitor);
            doCopyMembers.start();

            while (doCopyMembers.isAlive()) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }

        } finally {
            postRun.returnResult(doCopyMembers.isError(), doCopyMembers.getMembersCopiedCount(), doCopyMembers.getAverageTime());
            endProcess();
        }

        return Status.OK_STATUS;
    }

    public void cancelOperation() {
        if (doCopyMembers != null) {
            doCopyMembers.cancel();
        }
    }

    private void startProcess() {
        isActive = true;
    }

    private void endProcess() {
        isActive = false;
    }

    public boolean isCanceled() {
        return doCopyMembers.isCanceled();
    }

    public boolean isError() {
        return doCopyMembers.isError();
    }

    private class DoCopyMembers extends Thread {

        private Set<IItemErrorListener> itemErrorListeners;

        private String fromConnectionName;
        private String toConnectionName;
        private CopyMemberItem[] members;
        private ExistingMemberAction existingMemberAction;

        private IProgressMonitor monitor;

        private boolean isCanceled;
        private boolean isError;
        private int copiedCount;
        private long averageTime;

        public DoCopyMembers(String fromConnectionName, String toConnectionName, CopyMemberItem[] members,
            ExistingMemberAction existingMemberAction) {

            this.itemErrorListeners = new HashSet<IItemErrorListener>();

            this.fromConnectionName = fromConnectionName;
            this.toConnectionName = toConnectionName;
            this.members = members;
            this.existingMemberAction = existingMemberAction;
            this.isCanceled = false;
        }

        public void addItemErrorListener(IItemErrorListener listener) {
            itemErrorListeners.add(listener);
        }

        public void setMonitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public void run() {

            monitor.beginTask(Messages.Copying_dots, members.length);

            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            try {

                isError = false;
                copiedCount = 0;

                long startTime = System.currentTimeMillis();

                int count = 0;
                for (CopyMemberItem member : members) {

                    count++;

                    if (monitor.isCanceled()) {
                        break;
                    }

                    if (member.isCopied()) {
                        monitor.worked(count);
                        continue;
                    }

                    if (monitor != null) {
                        monitor.setTaskName(Messages.bind(Messages.Copying_A_B_of_C, new Object[] { member.getFromMember(), count, members.length }));
                    }

                    boolean canCopy;
                    if (isMember(toSystem, member.getToLibrary(), member.getToFile(), member.getToMember())) {
                        if (ExistingMemberAction.RENAME.equals(existingMemberAction)) {
                            canCopy = performRenameMember(toSystem, member);
                        } else if (ExistingMemberAction.REPLACE.equals(existingMemberAction)) {
                            canCopy = true;
                        } else {
                            canCopy = false;
                            setMemberError(member, Messages.bind(Messages.Target_member_A_already_exists, member.getToMember()));
                        }
                    } else {
                        canCopy = true;
                    }

                    if (!canCopy || !member.performCopyOperation(fromConnectionName, toConnectionName)) {
                        isError = true;
                    } else {
                        copiedCount++;
                    }
                    copiedCount++;

                    monitor.worked(count);
                }

                if (copiedCount > 0) {
                    averageTime = (System.currentTimeMillis() - startTime) / copiedCount;
                }

            } finally {
                monitor.done();
            }
        }

        private boolean isMember(AS400 toSystem, String library, String file, String member) {

            boolean isMember = ISphereHelper.checkMember(toSystem, library, file, member);

            return isMember;
        }

        private boolean performRenameMember(AS400 system, CopyMemberItem copyMemberItem) {

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
                    // printDebug(message);
                    StringBuilder errorMessage = new StringBuilder();
                    for (AS400Message as400Message : rtnMessages) {
                        if (errorMessage.length() > 0) {
                            errorMessage.append(" :: "); //$NON-NLS-1$
                        }
                        errorMessage.append(as400Message.getText());
                    }
                    setMemberError(copyMemberItem, errorMessage.toString());
                    return false;
                }

                return true;

            } catch (Exception e) {
                setMemberError(copyMemberItem, ExceptionHelper.getLocalizedMessage(e));
                return false;
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
            return isError;
        }

        public int getMembersCopiedCount() {
            return copiedCount;
        }

        public long getAverageTime() {
            return averageTime;
        }
    }
}
