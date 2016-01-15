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

import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;

/**
 * This class copies a given list of members to another library, file or member
 * name.
 * <p>
 * Today 'fromConnection' must equal 'toConnection'.
 */
public class CopyMemberService implements CopyMemberItem.ModifiedListener {

    private String fromConnectionName;
    private String toConnectionName;
    private SortedSet<CopyMemberItem> members;

    private Set<String> fromLibraryNames = new HashSet<String>();
    private Set<String> fromFileNames = new HashSet<String>();

    private List<ModifiedListener> modifiedListeners;
    private int copiedCount;
    private boolean isActive;

    public CopyMemberService(String fromConnectionName) {
        this.fromConnectionName = fromConnectionName;
        this.toConnectionName = fromConnectionName;
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

    public int getFromFileNamesCount() {
        return fromFileNames.size();
    }

    public String[] getFromFileNames() {
        return fromFileNames.toArray(new String[fromFileNames.size()]);
    }

    public CopyMemberItem[] getItems() {
        return members.toArray(new CopyMemberItem[members.size()]);
    }

    public void setToLibrary(String libraryName) {

        startProcess();

        try {

            for (CopyMemberItem member : members) {
                member.setToLibrary(libraryName);
            }

        } finally {
            endProcess();
        }
    }

    public void setToFile(String fileName) {

        startProcess();

        try {

            for (CopyMemberItem member : members) {
                member.setToFile(fileName);
            }

        } finally {
            endProcess();
        }
    }

    public CopyMemberItem[] getCopiedCopied() {

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

    public boolean haveItemsToCopy() {

        if (copiedCount < members.size()) {
            return true;
        }

        return false;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Validates the job description.
     * 
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean validate(boolean replace) {

        startProcess();

        boolean isError = false;

        try {

            Set<String> targetMembers = new HashSet<String>();

            for (CopyMemberItem member : members) {
                if (member.isCopied()) {
                    continue;
                }

                String from = member.getFromQSYSName();
                String to = member.getToQSYSName();

                if (from.equals(to)) {
                    member.setErrorMessage(Messages.bind(Messages.Cannot_copy_A_to_the_same_name, from));
                    isError = true;
                } else if (targetMembers.contains(to)) {
                    member.setErrorMessage(Messages.Can_not_copy_member_twice_to_same_target_member);
                    isError = true;
                } else if (!IBMiHostContributionsHandler.checkMember(getFromConnectionName(), member.getFromLibrary(), member.getFromFile(),
                    member.getFromMember())) {
                    member.setErrorMessage(Messages.bind(Messages.From_member_A_not_found, from));
                    isError = true;
                } else if (!replace
                    && IBMiHostContributionsHandler.checkMember(getToConnectionName(), member.getToLibrary(), member.getToFile(),
                        member.getToMember())) {
                    member.setErrorMessage(Messages.bind(Messages.Target_member_A_already_exists, to));
                    isError = true;
                } else {
                    member.setErrorMessage(null);
                }

                targetMembers.add(to);
            }

        } finally {
            endProcess();
        }

        return !isError;
    }

    /**
     * Validates the job description.
     * 
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean execute() {

        startProcess();

        boolean isError = false;

        try {

            for (CopyMemberItem member : members) {
                if (member.isCopied()) {
                    continue;
                }

                if (!member.performCopyOperation(fromConnectionName, toConnectionName)) {
                    isError = true;
                } else {
                    copiedCount++;
                }
            }

        } finally {
            endProcess();
        }

        return !isError;
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
    public void modified(CopyMemberItem item, String property) {
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
}
