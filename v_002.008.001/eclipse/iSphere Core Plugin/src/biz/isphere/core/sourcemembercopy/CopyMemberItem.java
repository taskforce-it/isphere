/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import biz.isphere.base.internal.IBMiHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.api.retrievememberdescription.MBRD0100;
import biz.isphere.core.internal.api.retrievememberdescription.QUSRMBRD;

import com.ibm.as400.access.AS400;
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
        buffer.append("/");
        buffer.append(file);
        buffer.append("(");
        buffer.append(member);
        buffer.append(")");

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
        buffer.append(fromLibrary);
        buffer.append("/");
        buffer.append(fromFile);
        buffer.append("(");
        buffer.append(fromMember);
        buffer.append(") -> ");
        buffer.append(toLibrary);
        buffer.append("/");
        buffer.append(toFile);
        buffer.append("(");
        buffer.append(toMember);
        buffer.append(")");
        return buffer.toString();
    }

    public boolean performCopyOperation(String fromConnectionName, String toConnectionName) {

        String fromText = null;

        try {
            AS400 fromSystem = IBMiHostContributionsHandler.getSystem(fromConnectionName);

            QUSRMBRD qusrmbrd = new QUSRMBRD(fromSystem);
            qusrmbrd.setFile(getFromFile(), getFromLibrary(), getFromMember());
            MBRD0100 mbrd0100 = new MBRD0100(fromSystem);
            if (qusrmbrd.execute(mbrd0100)) {
                fromText = mbrd0100.getMeberDescription();
            } else {
                fromText = Messages.EMPTY;
            }

        } catch (Throwable e) {
            setErrorMessage(e.getLocalizedMessage());
        }

        String message;
        List<AS400Message> rtnMessages = new ArrayList<AS400Message>();

        message = IBMiHostContributionsHandler.executeCommand(toConnectionName, getCopyFileCommand(), rtnMessages);

        if (message != null) {
            setErrorMessage(buildMMessageString(rtnMessages));
            return false;
        }

        message = IBMiHostContributionsHandler.executeCommand(toConnectionName, getChangeMemberTextCommand(fromText));

        if (message != null) {
            setErrorMessage(message);
            return false;
        }

        setCopyStatus(true);

        return true;
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

    private String getChangeMemberTextCommand(String text) {

        // CHGPFM FILE(LIB/FILE) MBR(MBR) TEXT(TEXT)

        StringBuilder command = new StringBuilder();

        command.append("CHGPFM");
        command.append(" FILE(");
        command.append(getToLibrary());
        command.append("/");
        command.append(getToFile());
        command.append(")");
        command.append(" MBR(");
        command.append(getToMember());
        command.append(")");
        command.append(" TEXT(");
        command.append(IBMiHelper.quote(text));
        command.append(")");

        return command.toString();
    }

    private String getCopyFileCommand() {

        // CPYF FROMFILE(FROMLIB/FROMFILE) TOFILE(TOLIB/TOFILE) FROMMBR(FROMMBR)
        // TOMBR(TOMBR) MBROPT(*REPLACE) CRTFILE(*NO)

        StringBuilder command = new StringBuilder();

        command.append("CPYF");
        command.append(" FROMFILE(");
        command.append(getFromLibrary());
        command.append("/");
        command.append(getFromFile());
        command.append(")");
        command.append(" TOFILE(");
        command.append(getToLibrary());
        command.append("/");
        command.append(getToFile());
        command.append(")");
        command.append(" FROMMBR(");
        command.append(getFromMember());
        command.append(")");
        command.append(" TOMBR(");
        command.append(getToMember());
        command.append(")");
        command.append(" MBROPT(*REPLACE)");
        command.append(" CRTFILE(*NO)");

        return command.toString();
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
