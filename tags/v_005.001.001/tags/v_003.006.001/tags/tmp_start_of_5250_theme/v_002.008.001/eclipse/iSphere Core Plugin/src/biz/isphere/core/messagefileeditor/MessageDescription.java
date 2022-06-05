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

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;

public class MessageDescription implements Serializable {

    private static final String CRLF = "\n";
    private static final String TAB = "\t";

    private static final long serialVersionUID = 5093317088102919464L;

    public static final String VALUE_NONE = "*NONE"; //$NON-NLS-1$

    public static final String CCSID_JOB = "*JOB"; //$NON-NLS-1$
    public static final String CCSID_HEX = "*HEX"; //$NON-NLS-1$

    public static final String REPLY_CHAR = "*CHAR"; //$NON-NLS-1$
    public static final String REPLY_DEC = "*DEC"; //$NON-NLS-1$
    public static final String REPLY_ALPHA = "*ALPHA"; //$NON-NLS-1$
    public static final String REPLY_NAME = "*NAME"; //$NON-NLS-1$

    private String connectionName;
    private String library;
    private String messageFile;
    private String messageId;
    private String message;
    private String helpText;
    private Integer severity;
    private Integer ccsid;
    private ArrayList<FieldFormat> fieldFormats;
    private String replyType;
    private int replyLength;
    private int replyDecimalPositions;
    private ArrayList<ValidReplyEntry> validReplyEntries;
    private ArrayList<SpecialReplyValueEntry> specialReplyValueEntries;
    private RangeOfReplyValues rangeOfReplyValue;
    private String defaultReplyValue;
    private RelationalTestEntry relationalTestEntry;

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
        replyType = VALUE_NONE;
        replyLength = 0;
        replyDecimalPositions = 0;
        validReplyEntries = new ArrayList<ValidReplyEntry>();
        specialReplyValueEntries = new ArrayList<SpecialReplyValueEntry>();
        rangeOfReplyValue = new RangeOfReplyValues();
        defaultReplyValue = VALUE_NONE;
        relationalTestEntry = new RelationalTestEntry();
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
            setCcsid(new Integer(65535));
        } else if (CCSID_JOB.equals(ccsid)) {
            setCcsid(new Integer(-1));
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

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }

    public int getReplyLength() {
        return replyLength;
    }

    public void setReplyLength(int replyLength) {
        this.replyLength = replyLength;
    }

    public int getReplyDecimalPositions() {
        return replyDecimalPositions;
    }

    public void setReplyDecimalPositions(int replyDecimalPositions) {
        this.replyDecimalPositions = replyDecimalPositions;
    }

    public String getDefaultReplyValue() {
        return this.defaultReplyValue;
    }

    public void setDefaultReplyValue(String defaultReplyValue) {
        this.defaultReplyValue = defaultReplyValue;
    }

    public ArrayList<ValidReplyEntry> getValidReplyEntries() {
        return validReplyEntries;
    }

    public void setValidReplyEntries(ArrayList<ValidReplyEntry> validReplyEntries) {
        this.validReplyEntries = validReplyEntries;
    }

    public ArrayList<SpecialReplyValueEntry> getSpecialReplyValueEntries() {
        return specialReplyValueEntries;
    }

    public void setSpecialReplyValueEntries(ArrayList<SpecialReplyValueEntry> specialReplyValueEntries) {
        this.specialReplyValueEntries = specialReplyValueEntries;
    }

    public RangeOfReplyValues getRangeOfReplyValue() {
        return rangeOfReplyValue;
    }

    public void setRangeOfReplyValue(RangeOfReplyValues rangeOfReplyValue) {
        this.rangeOfReplyValue = rangeOfReplyValue;
    }

    public RelationalTestEntry getRelationalTestEntry() {
        return relationalTestEntry;
    }

    public void setRelationalTestEntry(RelationalTestEntry relationalTestEntry) {
        this.relationalTestEntry = relationalTestEntry;
    }

    public String asComparableText(int width) {

        StringBuilder buffer = new StringBuilder();

        int tWidth = width;
        if (tWidth <= 0) {
            tWidth = Integer.MAX_VALUE;
        }

        addFormattedTextItem(buffer, Messages.Message_identifier_colon, getMessageId());
        addFormattedTextItem(buffer, Messages.First_level_message_text_colon, StringHelper.wrapAndIndentString(getMessage(), TAB, tWidth));
        addFormattedTextItem(buffer, Messages.Second_level_message_text_colon, StringHelper.wrapAndIndentString(getHelpText(), TAB, tWidth));
        addFormattedTextItem(buffer, Messages.Severity_code_colon, getSeverity().toString());
        addFormattedTextItem(buffer, Messages.Coded_character_set_ID_colon, getCcsidAsString());

        int i = 0;

        buffer.append(Messages.Message_data_fields_formats_colon + CRLF);
        if (getFieldFormats().size() == 0) {
            addFormattedItemValue(buffer, MessageDescription.VALUE_NONE);
        } else {
            i = 0;
            for (FieldFormat fieldFormat : getFieldFormats()) {
                i++;
                addFormattedItemValue(buffer, fieldFormat.asComparableText(i));
            }
        }
        buffer.append(CRLF);

        addFormattedTextItem(buffer, Messages.Reply_type_colon, getFullReplyType());

        buffer.append(Messages.Valid_reply_values_colon + CRLF);
        if (getValidReplyEntries().size() == 0) {
            addFormattedItemValue(buffer, MessageDescription.VALUE_NONE);
        } else {
            i = 0;
            for (ValidReplyEntry validReplyEntry : getValidReplyEntries()) {
                i++;
                addFormattedItemValue(buffer, validReplyEntry.asComparableText());
            }
        }
        buffer.append(CRLF);

        buffer.append(Messages.Special_reply_values_colon + CRLF);
        if (getValidReplyEntries().size() == 0) {
            addFormattedItemValue(buffer, MessageDescription.VALUE_NONE);
        } else {
            i = 0;
            for (SpecialReplyValueEntry specialReplyValueEntry : getSpecialReplyValueEntries()) {
                i++;
                addFormattedItemValue(buffer, specialReplyValueEntry.asComparableText());
            }
        }
        buffer.append(CRLF);

        addFormattedTextItem(buffer, Messages.Range_of_reply_values_colon, getRangeOfReplyValue().asComparableText());
        addFormattedTextItem(buffer, Messages.Default_reply_value_colon, getDefaultReplyValue());
        addFormattedTextItem(buffer, Messages.Relationship_for_valid_replies_colon, getRelationalTestEntry().asComparableText());

        return buffer.toString();
    }

    private void addFormattedTextItem(StringBuilder buffer, String name, String... values) {

        buffer.append(name);
        buffer.append(CRLF);

        for (String value : values) {
            addFormattedItemValue(buffer, value);
        }

        buffer.append(CRLF);
    }

    private void addFormattedItemValue(StringBuilder buffer, String value) {

        buffer.append(TAB);
        buffer.append(value);
        buffer.append(CRLF);
    }

    private String getFullReplyType() {

        if (VALUE_NONE.equals(getReplyType())) {
            return VALUE_NONE;
        }

        return getReplyType() + "(" + getReplyLength() + "," + getReplyDecimalPositions() + ")";
    }

    public String getFullQualifiedName() {
        return library + "/" + messageFile + "(" + messageId + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public String toString() {
        return getFullQualifiedName();
    }

}
