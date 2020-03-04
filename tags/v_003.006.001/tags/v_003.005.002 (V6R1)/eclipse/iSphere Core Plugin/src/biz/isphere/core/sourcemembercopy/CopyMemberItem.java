/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.SourceLine;

import com.ibm.as400.access.AS400Message;

public class CopyMemberItem implements Comparable<CopyMemberItem> {

    private String fromFile;
    private String fromLibrary;
    private String fromMember;

    private String toFile;
    private String toLibrary;
    private String toMember;

    private String errorMessage;
    private boolean copied;

    private List<ModifiedListener> modifiedListeners;

    public CopyMemberItem(String fromFile, String fromLibrary, String fromMember) {
        this.fromFile = fromFile;
        this.fromLibrary = fromLibrary;
        this.fromMember = fromMember;
        this.toFile = fromFile;
        this.toLibrary = fromLibrary;
        this.toMember = fromMember;
        this.errorMessage = null;
        this.copied = false;
    }

    public String getFromFile() {
        return fromFile;
    }

    public String getFromLibrary() {
        return fromLibrary;
    }

    public String getFromMember() {
        return fromMember;
    }

    public String getToFile() {
        return toFile;
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
        notifyModifiedListeners("toMember");
    }

    public String getToLibrary() {
        return toLibrary;
    }

    public void setToLibrary(String toLibrary) {
        this.toLibrary = toLibrary;
        notifyModifiedListeners("toMember");
    }

    public String getToMember() {
        return toMember;
    }

    public void setToMember(String toMember) {
        this.toMember = toMember;
        notifyModifiedListeners("toMember");
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
        notifyModifiedListeners("errorMessage");
    }

    public boolean isCopied() {
        return copied;
    }

    private void setCopyStatus(boolean copied) {
        this.copied = copied;
        setErrorMessage(null);
    }

    public String getFromQSYSName() {
        return getQSYSName(getFromFile(), getFromLibrary(), getFromMember());
    }

    public String getToQSYSName() {
        return getQSYSName(getToFile(), getToLibrary(), getToMember());
    }

    private String getQSYSName(String file, String library, String member) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(library);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(file);
        buffer.append("("); //$NON-NLS-1$
        buffer.append(member);
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }

    public int compareTo(CopyMemberItem item) {

        if (this.equals(item)) {
            return 0;
        }

        int result;

        result = getFromLibrary().compareTo(item.getFromLibrary());
        if (result != 0) {
            return result;
        }

        if (getToLibrary() == null) {
            return -1;
        } else if (item.getToLibrary() == null) {
            return 1;
        } else {
            result = getToLibrary().compareTo(item.getToLibrary());
            if (result != 0) {
                return result;
            }
        }

        result = getFromFile().compareTo(item.getFromFile());
        if (result != 0) {
            return result;
        }

        if (getToFile() == null) {
            return -1;
        } else if (item.getToFile() == null) {
            return 1;
        } else {
            result = getToFile().compareTo(item.getToFile());
            if (result != 0) {
                return result;
            }
        }

        result = getFromMember().compareTo(item.getFromMember());
        if (result != 0) {
            return result;
        }

        if (getToMember() == null) {
            return -1;
        } else if (item.getToMember() == null) {
            return 1;
        } else {
            result = getToMember().compareTo(item.getToMember());
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(fromLibrary); //$NON-NLS-1$
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(fromFile); //$NON-NLS-1$
        buffer.append("("); //$NON-NLS-1$
        buffer.append(fromMember); //$NON-NLS-1$
        buffer.append(") -> "); //$NON-NLS-1$
        buffer.append(toLibrary); //$NON-NLS-1$
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(toFile); //$NON-NLS-1$
        buffer.append("("); //$NON-NLS-1$
        buffer.append(toMember); //$NON-NLS-1$
        buffer.append(")"); //$NON-NLS-1$
        return buffer.toString();
    }

    public boolean performCopyOperation(String fromConnectionName, String toConnectionName) {

        String message;

        if (fromConnectionName.equalsIgnoreCase(toConnectionName)) {
            message = performLocalCopy(fromConnectionName);
        } else {
            message = performCopyBetweenConnections(fromConnectionName, toConnectionName);
        }

        if (message != null) {
            setErrorMessage(message);
            return false;
        }

        setCopyStatus(true);

        return true;
    }

    public void reset() {

        setErrorMessage(null);
        setCopyStatus(false);
    }

    private String performLocalCopy(String connectionName) {

        try {

            String message = null;

            Member fromSourceMember = IBMiHostContributionsHandler.getMember(connectionName, getFromLibrary(), getFromFile(), getFromMember());
            if (fromSourceMember == null) {
                return Messages.bind(Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] { getFromLibrary(), getFromFile(),
                    getFromMember() });
            }

            if (!IBMiHostContributionsHandler.checkMember(connectionName, getToLibrary(), getToFile(), getToMember())) {
                message = addSourceMember(connectionName, getToLibrary(), getToFile(), getToMember());
            } else {
                message = prepareSourceMember(connectionName, getToLibrary(), getToFile(), getToMember());
            }
            if (message != null) {
                return message;
            }

            List<AS400Message> rtnMessages = new ArrayList<AS400Message>();
            StringBuilder command = new StringBuilder();

            command.append("CPYF"); //$NON-NLS-1$
            command.append(" FROMFILE("); //$NON-NLS-1$
            command.append(getFromLibrary());
            command.append("/"); //$NON-NLS-1$
            command.append(getFromFile());
            command.append(")"); //$NON-NLS-1$
            command.append(" TOFILE("); //$NON-NLS-1$
            command.append(getToLibrary());
            command.append("/"); //$NON-NLS-1$
            command.append(getToFile());
            command.append(")"); //$NON-NLS-1$
            command.append(" FROMMBR("); //$NON-NLS-1$
            command.append(getFromMember());
            command.append(")"); //$NON-NLS-1$
            command.append(" TOMBR("); //$NON-NLS-1$
            command.append(getToMember());
            command.append(")"); //$NON-NLS-1$
            command.append(" MBROPT(*REPLACE)"); //$NON-NLS-1$
            command.append(" CRTFILE(*NO)"); //$NON-NLS-1$
            command.append(" FMTOPT(*MAP)"); //$NON-NLS-1$

            message = IBMiHostContributionsHandler.executeCommand(connectionName, command.toString(), rtnMessages);
            if (message != null) {
                return buildMMessageString(rtnMessages);
            }

            Member toSourceMember = IBMiHostContributionsHandler.getMember(connectionName, getToLibrary(), getToFile(), getToMember());
            if (toSourceMember == null) {
                return Messages.bind(Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] { getToLibrary(), getToFile(), getToMember() });
            }

            message = setToMemberAttributes(fromSourceMember, toSourceMember);
            if (message != null) {
                return message;
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when copying member ***", e);
            return ExceptionHelper.getLocalizedMessage(e);
        }

        return null;
    }

    private String performCopyBetweenConnections(String fromConnectionName, String toConnectionName) {

        try {

            String message = null;

            Member fromSourceMember = IBMiHostContributionsHandler.getMember(fromConnectionName, getFromLibrary(), getFromFile(), getFromMember());
            if (fromSourceMember == null) {
                return Messages.bind(Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] { getFromLibrary(), getFromFile(),
                    getFromMember() });
            }

            SourceLine[] sourceLines = fromSourceMember.downloadSourceMember(null);

            if (sourceLines.length == 0) {
                return Messages.bind(Messages.Could_not_download_member_2_of_file_1_of_library_0, new Object[] { getFromLibrary(), getFromFile(),
                    getFromMember() });
            }

            if (!IBMiHostContributionsHandler.checkMember(toConnectionName, getToLibrary(), getToFile(), getToMember())) {
                message = addSourceMember(toConnectionName, getToLibrary(), getToFile(), getToMember());
            } else {
                message = prepareSourceMember(toConnectionName, getToLibrary(), getToFile(), getToMember());
            }

            if (message != null) {
                return message;
            }

            Member toSourceMember = IBMiHostContributionsHandler.getMember(toConnectionName, getToLibrary(), getToFile(), getToMember());
            if (toSourceMember == null) {
                return Messages.bind(Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] { getToLibrary(), getToFile(), getToMember() });
            }

            message = toSourceMember.uploadSourceMember(sourceLines, null);
            if (message != null) {
                return message;
            }

            message = setToMemberAttributes(fromSourceMember, toSourceMember);
            if (message != null) {
                return message;
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Unexpected error when copying member ***", e); //$NON-NLS-1$
            return ExceptionHelper.getLocalizedMessage(e);
        }

        return null;
    }

    private String addSourceMember(String connectionName, String libraryName, String fileName, String memberName) {

        StringBuilder command = new StringBuilder();
        command.append("ADDPFM"); //$NON-NLS-1$
        command.append(" FILE("); //$NON-NLS-1$
        command.append(libraryName);
        command.append("/"); //$NON-NLS-1$
        command.append(fileName);
        command.append(")"); //$NON-NLS-1$
        command.append(" MBR("); //$NON-NLS-1$
        command.append(memberName);
        command.append(")"); //$NON-NLS-1$
        command.append(" TEXT('*** iSphere Copying Member ***')"); //$NON-NLS-1$
        command.append(" SRCTYPE('*NONE')"); //$NON-NLS-1$

        List<AS400Message> rtnMessages = new ArrayList<AS400Message>();
        String message = IBMiHostContributionsHandler.executeCommand(connectionName, command.toString(), rtnMessages);
        if (message != null) {
            return buildMMessageString(rtnMessages);
        }

        return null;
    }

    private String prepareSourceMember(String connectionName, String libraryName, String fileName, String memberName) {

        String message = null;
        List<AS400Message> rtnMessages = new ArrayList<AS400Message>();
        StringBuilder command = new StringBuilder();

        rtnMessages.clear();
        command.delete(0, command.length());

        command.append("CHGPFM"); //$NON-NLS-1$
        command.append(" FILE("); //$NON-NLS-1$
        command.append(libraryName);
        command.append("/"); //$NON-NLS-1$
        command.append(fileName);
        command.append(")"); //$NON-NLS-1$
        command.append(" MBR("); //$NON-NLS-1$
        command.append(memberName);
        command.append(")"); //$NON-NLS-1$
        command.append(" TEXT('*** iSphere Copying Member ***')"); //$NON-NLS-1$
        command.append(" SRCTYPE('*NONE')"); //$NON-NLS-1$

        message = IBMiHostContributionsHandler.executeCommand(connectionName, command.toString(), rtnMessages);
        if (message != null) {
            return buildMMessageString(rtnMessages);
        }

        rtnMessages.clear();
        command.delete(0, command.length());

        command.append("CLRPFM"); //$NON-NLS-1$
        command.append(" FILE("); //$NON-NLS-1$
        command.append(libraryName);
        command.append("/"); //$NON-NLS-1$
        command.append(fileName);
        command.append(")"); //$NON-NLS-1$
        command.append(" MBR("); //$NON-NLS-1$
        command.append(memberName);
        command.append(")"); //$NON-NLS-1$

        message = IBMiHostContributionsHandler.executeCommand(connectionName, command.toString(), rtnMessages);
        if (message != null) {
            return buildMMessageString(rtnMessages);
        }

        return null;
    }

    private String setToMemberAttributes(Member fromSourceMember, Member toSourceMember) {

        String description = fromSourceMember.getDescription();
        String sourceType = fromSourceMember.getSourceType();

        StringBuilder command = new StringBuilder();
        command.append("CHGPFM"); //$NON-NLS-1$
        command.append(" FILE("); //$NON-NLS-1$
        command.append(getToLibrary());
        command.append("/"); //$NON-NLS-1$
        command.append(getToFile());
        command.append(") "); //$NON-NLS-1$
        command.append("MBR("); //$NON-NLS-1$
        command.append(getToMember());
        command.append(") "); //$NON-NLS-1$
        command.append("TEXT("); //$NON-NLS-1$
        command.append(IBMiHelper.quote(description));
        command.append(")"); //$NON-NLS-1$
        command.append(" SRCTYPE("); //$NON-NLS-1$
        command.append(sourceType);
        command.append(")"); //$NON-NLS-1$

        List<AS400Message> rtnMessages = new ArrayList<AS400Message>();
        String message = IBMiHostContributionsHandler.executeCommand(toSourceMember.getConnection(), command.toString(), rtnMessages);
        if (message != null) {
            return buildMMessageString(rtnMessages);
        }

        return null;
    }

    private String buildMMessageString(List<AS400Message> rtnMessages) {

        StringBuilder message = new StringBuilder();

        Iterator<AS400Message> iterator = rtnMessages.iterator();
        while (iterator.hasNext()) {
            if (message.length() > 0) {
                message.append(" "); //$NON-NLS-1$
            }
            message.append(iterator.next().getText());
        }

        return message.toString();
    }

    public void addModifiedListener(ModifiedListener listener) {

        if (modifiedListeners == null) {
            modifiedListeners = new ArrayList<ModifiedListener>();
        }

        modifiedListeners.add(listener);
    }

    public void removeModifiedListener(ModifiedListener listener) {

        if (modifiedListeners != null) {
            modifiedListeners.remove(listener);
        }
    }

    private void notifyModifiedListeners(String property) {
        if (modifiedListeners == null) {
            return;
        }

        for (int i = 0; i < modifiedListeners.size(); ++i) {
            modifiedListeners.get(i).modified(this, property);
        }
    }

    public interface ModifiedListener {
        public void modified(CopyMemberItem item, String property);
    }
}
