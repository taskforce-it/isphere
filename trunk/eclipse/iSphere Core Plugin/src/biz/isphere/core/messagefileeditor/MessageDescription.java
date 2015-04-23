/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageDescription implements Serializable {

    private static final long serialVersionUID = 5093317088102919464L;

    public static final String TEXT_NONE = "*NONE"; //$NON-NLS-1$

    public static final String CCSID_JOB = "*JOB"; //$NON-NLS-1$
    public static final String CCSID_HEX = "*HEX"; //$NON-NLS-1$

    private String connectionName;
    private String library;
    private String messageFile;
    private String messageId;
    private String message;
    private String helpText;
    private Integer severity;
    private Integer ccsid;
    private ArrayList<FieldFormat> fieldFormats;

    public MessageDescription() {
        connectionName = ""; //$NON-NLS-1$
        library = ""; //$NON-NLS-1$
        messageFile = ""; //$NON-NLS-1$
        messageId = ""; //$NON-NLS-1$
        message = ""; //$NON-NLS-1$
        helpText = ""; //$NON-NLS-1$
        severity = new Integer("0"); //$NON-NLS-1$
        setCcsid(CCSID_JOB);
        fieldFormats = new ArrayList<FieldFormat>();
    }

    public String getConnection() {
        return connectionName;
    }

    public void setConnection(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getMessageFile() {
        return messageFile;
    }

    public void setMessageFile(String messageFile) {
        this.messageFile = messageFile;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHelpText() {
        return helpText;
    }

    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Integer getCcsid() {
        return ccsid;
    }

    public String getCcsidAsString() {
        if (ccsid.intValue() == 65535) {
            return CCSID_HEX;
        } else if (ccsid == -1) {
            return CCSID_JOB;
        } else {
            return ccsid.toString();
        }
    }

    public void setCcsid(String ccsid) {
        if (CCSID_HEX.equals(ccsid)) {
            this.ccsid = new Integer(65535);
        } else if (CCSID_JOB.equals(ccsid)) {
            this.ccsid = new Integer(-1);
        } else {
            throw new IllegalArgumentException("Value " + ccsid + " not allowed."); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public void setCcsid(Integer ccsid) {
        this.ccsid = ccsid;
    }

    public ArrayList<FieldFormat> getFieldFormats() {
        return fieldFormats;
    }

    public void setFieldFormats(ArrayList<FieldFormat> fieldFormats) {
        this.fieldFormats = fieldFormats;
    }

    @Override
    public String toString() {
        return library + "/" + messageFile + "(" + messageId + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
