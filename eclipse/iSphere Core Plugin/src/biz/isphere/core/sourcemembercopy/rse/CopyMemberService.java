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

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.file.description.RecordFormatDescription;
import biz.isphere.core.file.description.RecordFormatDescriptionsStore;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.sourcemembercopy.CopyMemberItem;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.FieldDescription;

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

    private boolean hasDataLostError;

    private Shell shell;
    private List<ModifiedListener> modifiedListeners;
    private int copiedCount;
    private boolean isActive;

    public CopyMemberService(Shell shell, String fromConnectionName) {
        this.shell = shell;
        this.fromConnectionName = fromConnectionName;
        this.toConnectionName = fromConnectionName;
        this.members = new TreeSet<CopyMemberItem>();
        this.hasDataLostError = false;
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

    public int getFromFileNamesCount() {
        return fromFileNames.size();
    }

    public String[] getFromFileNames() {
        return fromFileNames.toArray(new String[fromFileNames.size()]);
    }

    public CopyMemberItem[] getItems() {
        return members.toArray(new CopyMemberItem[members.size()]);
    }

    public void setToConnection(String connectionName) {

        startProcess();

        try {

            this.toConnectionName = connectionName;

        } finally {
            endProcess();
        }
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
    public boolean validate(boolean replace, boolean ignoreDataLostError) {

        startProcess();

        boolean isError = false;

        try {

            hasDataLostError = false;
            boolean isSeriousError = false;

            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);
            AS400 toSystem = IBMiHostContributionsHandler.getSystem(toConnectionName);

            RecordFormatDescriptionsStore fromSourceFiles = new RecordFormatDescriptionsStore(fromSystem);
            RecordFormatDescriptionsStore toSourceFiles = new RecordFormatDescriptionsStore(toSystem);

            Set<String> targetMembers = new HashSet<String>();

            for (CopyMemberItem member : members) {
                if (member.isCopied()) {
                    continue;
                }

                if (isSeriousError) {
                    member.setErrorMessage(Messages.Canceled_due_to_previous_error);
                    continue;
                }

                String from = member.getFromQSYSName();
                String to = member.getToQSYSName();

                member.setErrorMessage(null);

                if (from.equals(to) && fromConnectionName.equalsIgnoreCase(toConnectionName)) {
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
                } else if (!ignoreDataLostError) {

                    RecordFormatDescription fromRecordFormatDescription = fromSourceFiles.get(member.getFromFile(), member.getFromLibrary());
                    RecordFormatDescription toRecordFormatDescription = toSourceFiles.get(member.getToFile(), member.getToLibrary());

                    FieldDescription fromSrcDta = fromRecordFormatDescription.getFieldDescription("SRCDTA");
                    if (fromSrcDta == null) {
                        member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A, new String[] {
                            member.getFromFile(), member.getFromLibrary(), "SRCDTA" }));
                        isError = true;
                        isSeriousError = true;
                    } else {

                        FieldDescription toSrcDta = toRecordFormatDescription.getFieldDescription("SRCDTA");
                        if (toSrcDta == null) {
                            member.setErrorMessage(Messages.bind(Messages.Could_not_retrieve_field_description_of_field_C_of_file_B_A, new String[] {
                                member.getToFile(), member.getToLibrary(), "SRCDTA" }));
                            isError = true;
                            isSeriousError = true;
                        } else {

                            if (fromSrcDta.getLength() > toSrcDta.getLength()) {
                                member.setErrorMessage(Messages.Data_lost_error_From_source_line_is_longer_than_target_source_line);
                                hasDataLostError = true;
                                isError = true;
                            }
                        }
                    }
                }

                targetMembers.add(to);
            }

        } finally {
            endProcess();
        }

        return !isError;
    }

    public boolean hasDataLostError() {
        return hasDataLostError;
    }

    /**
     * Copies the members.
     * 
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean execute() {

        startProcess();

        boolean isError = false;

        try {

            CopyMembersJob copyMembersJob = new CopyMembersJob();
            BusyIndicator.showWhile(shell.getDisplay(), copyMembersJob);

            isError = copyMembersJob.isError();
            copiedCount = copiedCount + copyMembersJob.getCopiedCount();

        } finally {
            endProcess();
        }

        return !isError;
    }

    public void reset() {

        for (CopyMemberItem member : members) {
            member.reset();
        }

        copiedCount = 0;
        hasDataLostError = false;
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

    private class CopyMembersJob implements Runnable {

        private boolean isError;
        private int copiedCount;

        public void run() {

            isError = false;
            copiedCount = 0;

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
        }

        public boolean isError() {
            return isError;
        }

        public int getCopiedCount() {
            return copiedCount;
        }
    }
}
