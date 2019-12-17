/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy.rse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;

import com.ibm.as400.access.AS400;

/**
 * This class copies a given list of members to another library, file or member
 * name.
 * <p>
 * Today 'fromConnection' must equal 'toConnection'.
 */
public class CopyMemberService implements CopyMemberItem.ModifiedListener, ICopyMembersPostRun {

    private String fromConnectionName;
    private String toConnectionName;
    private String toLibrary;
    private String toFile;
    private SortedSet<CopyMemberItem> members;

    private Set<String> fromLibraryNames = new HashSet<String>();
    private Set<String> fromFileNames = new HashSet<String>();

    private Shell shell;
    private List<ModifiedListener> modifiedListeners;
    private int copiedCount;
    private boolean isActive;
    private boolean isCanceled;

    private CopyMembersJob copyMembersJob;

    public CopyMemberService(Shell shell, String fromConnectionName) {
        this.shell = shell;
        this.fromConnectionName = fromConnectionName;
        this.toConnectionName = fromConnectionName;
        this.toLibrary = null;
        this.toFile = null;
        this.members = new TreeSet<CopyMemberItem>();
    }

    public CopyMemberItem addItem(String file, String library, String member) {

        CopyMemberItem copyMemberItem = new CopyMemberItem(file, library, member);
        copyMemberItem.addModifiedListener(this);

        members.add(copyMemberItem);

        fromLibraryNames.add(copyMemberItem.getFromLibrary());
        fromFileNames.add(copyMemberItem.getFromFile());

        return copyMemberItem;
    }

    public int getFromConnectionCcsid() {
        return getSystemCcsid(fromConnectionName);
    }

    public int getToConnectionCcsid() {
        return getSystemCcsid(toConnectionName);
    }

    private int getSystemCcsid(String connectionName) {
        AS400 system = IBMiHostContributionsHandler.getSystem(connectionName);
        if (system != null) {
            return system.getCcsid();
        }

        return -1;
    }

    public String getFromConnectionName() {
        return fromConnectionName;
    }

    public String getToConnectionName() {
        return toConnectionName;
    }

    public int getFromLibraryNamesCount() {
        return fromLibraryNames.size();
    }

    public String[] getFromLibraryNames() {
        return fromLibraryNames.toArray(new String[fromLibraryNames.size()]);
    }

    public String getToLibrary() {
        return toLibrary;
    }

    public int getFromFileNamesCount() {
        return fromFileNames.size();
    }

    public String[] getFromFileNames() {
        return fromFileNames.toArray(new String[fromFileNames.size()]);
    }

    public String getToFile() {
        return toFile;
    }

    public CopyMemberItem[] getItems() {
        return members.toArray(new CopyMemberItem[members.size()]);
    }

    public void setToConnection(String connectionName) {
        this.toConnectionName = connectionName;
    }

    public void setToLibrary(String libraryName) {
        this.toLibrary = libraryName;
    }

    public void setToFile(String fileName) {
        this.toFile = fileName;
    }

    public CopyMemberItem[] getCopiedItems() {

        SortedSet<CopyMemberItem> copied = new TreeSet<CopyMemberItem>();

        for (CopyMemberItem member : members) {
            if (member.isCopied()) {
                copied.add(member);
            }
        }

        return copied.toArray(new CopyMemberItem[copied.size()]);
    }

    public CopyMemberItem[] getItemsToCopy() {

        SortedSet<CopyMemberItem> toCopy = new TreeSet<CopyMemberItem>();

        for (CopyMemberItem member : members) {
            if (!member.isCopied()) {
                toCopy.add(member);
            }
        }

        return toCopy.toArray(new CopyMemberItem[toCopy.size()]);
    }

    public boolean hasItemsToCopy() {

        if (copiedCount < members.size()) {
            return true;
        }

        return false;
    }

    public int getItemsCopiedCount() {
        return copiedCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    /**
     * Copies the members.
     */
    public void execute() {

        isCanceled = false;

        copyMembersJob = new CopyMembersJob(fromConnectionName, toConnectionName, members, this);
        copyMembersJob.start();
    }

    public void updateMembersWithTargetSourceFile() {

        startProcess();

        try {

            for (CopyMemberItem member : members) {
                member.setToLibrary(this.toLibrary);
                member.setToFile(this.toFile);
                member.setErrorMessage(null);
            }

        } finally {
            endProcess();
        }

    }

    public void returnResult(boolean isError, int countMembersCopied) {

        this.copyMembersJob = null;

        this.copiedCount = this.copiedCount + countMembersCopied;

        if (isCanceled && !hasItemsToCopy()) {
            isCanceled = false;
        }
    }

    public void reset() {

        for (CopyMemberItem member : members) {
            member.reset();
        }

        copiedCount = 0;
        isCanceled = false;
    }

    public void cancel() {

        if (copyMembersJob != null) {
            copyMembersJob.cancel();
            isCanceled = true;
        }
    }

    private void startProcess() {
        isActive = true;
        notifyModifiedListeners(null);
    }

    private void endProcess() {
        isActive = false;
        notifyModifiedListeners(null);
    }

    /**
     * Copy member item has been modified. Forward notification to listeners of
     * this service.
     */
    public void modified(CopyMemberItem item) {
        notifyModifiedListeners(item);
    }

    /**
     * Adds a modified listener to this service.
     * 
     * @param listener - modified listener that is added
     */
    public void addModifiedListener(ModifiedListener listener) {

        if (modifiedListeners == null) {
            modifiedListeners = new ArrayList<ModifiedListener>();
        }

        modifiedListeners.add(listener);
    }

    /**
     * Removes a modified listener that listens to this service.
     * 
     * @param listener - modified listener that is removed
     */
    public void removeModifiedListener(ModifiedListener listener) {

        if (modifiedListeners != null) {
            modifiedListeners.remove(listener);
        }
    }

    /**
     * Notifies modified listeners about modifications to this service.
     * 
     * @param item - copy member item that has been changed
     */
    private void notifyModifiedListeners(CopyMemberItem item) {
        if (modifiedListeners == null) {
            return;
        }

        for (int i = 0; i < modifiedListeners.size(); ++i) {
            modifiedListeners.get(i).modified(item);
        }
    }

    public interface ModifiedListener {
        public void modified(CopyMemberItem item);
    }

    private class CopyMembersJob extends Thread {

        private DoCopyMembers doCopyMembers;
        private ICopyMembersPostRun postRun;

        public CopyMembersJob(String fromConnectionName, String toConnectionName, SortedSet<CopyMemberItem> members, ICopyMembersPostRun postRun) {
            this.doCopyMembers = new DoCopyMembers(fromConnectionName, toConnectionName, members);
            this.postRun = postRun;
        }

        @Override
        public void run() {

            startProcess();

            try {

                doCopyMembers.start();

                while (doCopyMembers.isAlive()) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }

            } finally {
                postRun.returnResult(doCopyMembers.isError(), doCopyMembers.getMembersCopiedCount());
                endProcess();
            }
        }

        public void cancel() {
            doCopyMembers.cancel();
        }
    }

    private class DoCopyMembers extends Thread {

        private String fromConnectionName;
        private String toConnectionName;
        private SortedSet<CopyMemberItem> members;

        private boolean isCanceled;
        private boolean isError;
        private int copiedCount;

        public DoCopyMembers(String fromConnectionName, String toConnectionName, SortedSet<CopyMemberItem> members) {
            this.fromConnectionName = fromConnectionName;
            this.toConnectionName = toConnectionName;
            this.members = members;
            this.isCanceled = false;
        }

        @Override
        public void run() {

            isError = false;
            copiedCount = 0;

            for (CopyMemberItem member : members) {

                if (isCanceled) {
                    break;
                }

                if (member.isCopied()) {
                    continue;
                }

                if (!member.performCopyOperation(fromConnectionName, toConnectionName)) {
                    isError = true;
                } else {
                    copiedCount++;
                }
            }
        }

        public void cancel() {
            isCanceled = true;
        }

        public boolean isError() {
            return isError;
        }

        public int getMembersCopiedCount() {
            return copiedCount;
        }
    }
}
