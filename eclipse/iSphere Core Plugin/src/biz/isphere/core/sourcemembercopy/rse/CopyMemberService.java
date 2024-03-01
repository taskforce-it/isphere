/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
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

import com.ibm.as400.access.AS400;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;
import biz.isphere.core.sourcemembercopy.ICopyMembersPostRun;
import biz.isphere.core.sourcemembercopy.MemberCopyError;

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
    private ExistingMemberAction existingMemberAction;
    private MissingFileAction missingFileAction;
    private boolean isIgnoreDataLostError;
    private boolean isIgnoreUnsavedChangesError;
    private boolean isFullErrorCheck;
    private boolean isRenameMemberCheck;

    private Set<String> fromLibraryNames = new HashSet<String>();
    private Set<String> fromFileNames = new HashSet<String>();

    private Shell shell;
    private List<ModifiedListener> modifiedListeners;
    private int copiedCount;
    private boolean isCanceled;

    private CopyMembersJob copyMembersJob;

    public CopyMemberService(Shell shell, String fromConnectionName) {
        this.shell = shell;
        this.fromConnectionName = fromConnectionName;
        this.toConnectionName = fromConnectionName;
        this.toLibrary = null;
        this.toFile = null;
        this.members = new TreeSet<CopyMemberItem>();
        this.existingMemberAction = ExistingMemberAction.ERROR;
        this.missingFileAction = MissingFileAction.ERROR;
        this.isIgnoreDataLostError = false;
        this.isIgnoreUnsavedChangesError = false;
        this.isFullErrorCheck = false;
        this.isRenameMemberCheck = true;
    }

    public void setMissingFileAction(MissingFileAction missingFileAction) {
        this.missingFileAction = missingFileAction;
    }

    public void setExistingMemberAction(ExistingMemberAction action) {
        this.existingMemberAction = action;
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

    public CopyMemberItem addItem(String file, String library, String member, String srcType) {

        CopyMemberItem copyMemberItem = new CopyMemberItem(file, library, member, srcType);
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
        this.toConnectionName = connectionName.trim();
    }

    public void setToLibrary(String libraryName) {
        this.toLibrary = libraryName.trim();
    }

    public void setToFile(String fileName) {
        this.toFile = fileName.trim();
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

    /**
     * Copies the members.
     */
    public void execute() {

        isCanceled = false;

        notifyModifiedListeners(null);

        copyMembersJob = new CopyMembersJob(fromConnectionName, toConnectionName, members.toArray(new CopyMemberItem[members.size()]), this);
        copyMembersJob.setExistingMemberAction(existingMemberAction);
        copyMembersJob.setMissingFileAction(missingFileAction);
        copyMembersJob.setIgnoreDataLostError(isIgnoreDataLostError);
        copyMembersJob.setIgnoreUnsavedChanges(isIgnoreUnsavedChangesError);
        copyMembersJob.setFullErrorCheck(isFullErrorCheck);
        copyMembersJob.schedule();
    }

    public void returnCopyMembersResult(boolean isCanceled, int countTotal, int countSkipped, int countCopied, int countErrors, long averageTime,
        MemberCopyError errorId, String cancelMessage) {

        this.copyMembersJob = null;

        this.isCanceled = isCanceled;

        this.copiedCount = this.copiedCount + countCopied;

        if (isCanceled && !hasItemsToCopy()) {
            isCanceled = false;
        }

        notifyModifiedListeners(null);
    }

    public boolean isActive() {

        if (copyMembersJob != null) {
            return true;
        }

        return false;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void reset() {

        for (CopyMemberItem member : members) {
            member.reset();
        }

        copiedCount = 0;
    }

    public void cancel() {

        if (copyMembersJob != null) {
            copyMembersJob.cancelOperation();
        }
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
}
