/*******************************************************************************
 * Copyright (c) 2005 SoftLanding Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     SoftLanding - initial API and implementation
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/
package biz.isphere.messagesubsystem.rse;

import java.util.Date;

import com.ibm.as400.access.MessageQueue;

public class QueuedMessageFilter {

    private String description;
    private String messageQueue;
    private String library;
    private String user;
    private String id;
    private int severity = -1;
    private int messageType = -1;
    private Date date;
    private String fromJobName;
    private String fromJobNumber;
    private String fromProgram;
    private String text;

    public QueuedMessageFilter() {
        super();
    }

    public QueuedMessageFilter(String filterString) {
        this();
        setFilters(filterString);
    }

    public String getDescription() {
        return description;
    }

    public String getLibrary() {
        return library;
    }

    public String getMessageQueue() {
        return messageQueue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public void setMessageQueue(String messageQueue) {
        this.messageQueue = messageQueue;
    }

    public Date getDate() {
        return date;
    }

    public String getFromJobName() {
        return fromJobName;
    }

    public String getFromJobNumber() {
        return fromJobNumber;
    }

    public String getFromProgram() {
        return fromProgram;
    }

    public String getId() {
        return id;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getSeverity() {
        return severity;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFromJobName(String fromJobName) {
        this.fromJobName = fromJobName;
    }

    public void setFromJobNumber(String fromJobNumber) {
        this.fromJobNumber = fromJobNumber;
    }

    public void setFromProgram(String fromProgram) {
        this.fromProgram = fromProgram;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = new Integer(messageType).intValue();
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public void setSeverity(String severity) {
        this.severity = new Integer(severity).intValue();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPath() {
        if (messageQueue.equals(MessageQueue.CURRENT))
            return MessageQueue.CURRENT;
        else {
            if (library.equals("QSYS"))return "/QSYS.LIB/" + messageQueue.trim() + ".MSGQ"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            else
                return "/QSYS.LIB/" + library.trim() + ".LIB/" + messageQueue.trim() + ".MSGQ"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    public String getFilterString() {
        StringBuffer filterString = new StringBuffer();
        if (messageQueue == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(messageQueue + "/"); //$NON-NLS-1$
        if (library == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(library + "/"); //$NON-NLS-1$
        if (user == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(user + "/"); //$NON-NLS-1$
        if (id == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(id + "/"); //$NON-NLS-1$
        if (severity == -1)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(new Integer(severity).toString() + "/"); //$NON-NLS-1$
        if (messageType == -1)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(new Integer(messageType).toString() + "/"); //$NON-NLS-1$
        if (fromJobName == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(fromJobName + "/"); //$NON-NLS-1$
        if (fromJobNumber == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(fromJobNumber + "/"); //$NON-NLS-1$
        if (fromProgram == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(fromProgram + "/"); //$NON-NLS-1$
        if (text == null)
            filterString.append("*/"); //$NON-NLS-1$
        else
            filterString.append(text + "/"); //$NON-NLS-1$
        return filterString.toString();
    }

    public void setFilters(String filterString) {
        int index;
        index = filterString.indexOf("/"); //$NON-NLS-1$
        String temp = filterString.substring(0, index);
        if (!temp.equals("*")) setMessageQueue(temp); //$NON-NLS-1$
        String parseText = filterString.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setLibrary(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setUser(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setId(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setSeverity(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setMessageType(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setFromJobName(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setFromJobNumber(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) setFromProgram(temp); //$NON-NLS-1$
        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        if (index == -1)
            temp = parseText;
        else
            temp = parseText.substring(0, index);
        if (!temp.equals("*")) setText(temp); //$NON-NLS-1$
    }

}
