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

import biz.isphere.core.ISpherePlugin;
import biz.isphere.messagesubsystem.internal.QSYRUSRI;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.MessageQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.data.PcmlException;

public class QueuedMessageFilter {

    public static final String MSGQ_CURRENT = "*CURRENT"; //$NON-NLS-1$

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
        setFilterString(filterString);
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
        return getPath(null);
    }

    public String getPath(AS400 system) {
        String resolvedMessageQueue = getMessageQueue(system);
        if (resolvedMessageQueue.equals(MessageQueue.CURRENT))
            return resolvedMessageQueue;
        else {
            if (library.equals("QSYS"))return "/QSYS.LIB/" + resolvedMessageQueue.trim() + ".MSGQ"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            else
                return "/QSYS.LIB/" + library.trim() + ".LIB/" + resolvedMessageQueue.trim() + ".MSGQ"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    private String getMessageQueue(AS400 system) {
        if (MessageQueue.CURRENT.equals(messageQueue) && system != null) {
            try {
                resolveMessageQueuePath(system);
            } catch (Throwable e) {
                ISpherePlugin.logError("*** Failed to resolve message queue name *CURRENT ***", null); //$NON-NLS-1$
            }
        }
        return messageQueue;
    }

    private void resolveMessageQueuePath(AS400 system) throws PcmlException {

        QSYRUSRI qsysusri = new QSYRUSRI();
        qsysusri.retrieveUserProfile(system, system.getUserId());

        QSYSObjectPathName pathName = new QSYSObjectPathName(qsysusri.getMessageQueuePath());
        messageQueue = pathName.getObjectName();
        library = pathName.getLibraryName();
    }

    public String getFilterString() {

        StringBuffer filterString = new StringBuffer();

        if (messageQueue == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(messageQueue + "/"); //$NON-NLS-1$
        }

        if (library == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(library + "/"); //$NON-NLS-1$
        }

        if (user == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(user + "/"); //$NON-NLS-1$
        }

        if (id == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(id + "/"); //$NON-NLS-1$
        }

        if (severity == -1) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(new Integer(severity).toString() + "/"); //$NON-NLS-1$
        }

        if (messageType == -1) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(new Integer(messageType).toString() + "/"); //$NON-NLS-1$
        }

        if (fromJobName == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(fromJobName + "/"); //$NON-NLS-1$
        }

        if (fromJobNumber == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(fromJobNumber + "/"); //$NON-NLS-1$
        }

        if (fromProgram == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(fromProgram + "/"); //$NON-NLS-1$
        }

        if (text == null) {
            filterString.append("*/"); //$NON-NLS-1$
        } else {
            filterString.append(text + "/"); //$NON-NLS-1$
        }

        return filterString.toString();
    }

    public static String getDefaultFilterString() {

        QueuedMessageFilter filter = new QueuedMessageFilter();
        filter.setMessageQueue(MSGQ_CURRENT); //$NON-NLS-1$

        return filter.getFilterString();
    }

    public void setFilterString(String filterString) {

        int index;

        index = filterString.indexOf("/"); //$NON-NLS-1$
        String temp = filterString.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setMessageQueue(temp);
        }

        String parseText = filterString.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setLibrary(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setUser(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setId(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setSeverity(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setMessageType(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setFromJobName(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setFromJobNumber(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        temp = parseText.substring(0, index);
        if (!temp.equals("*")) { //$NON-NLS-1$
            setFromProgram(temp);
        }

        parseText = parseText.substring(index + 1);
        index = parseText.indexOf("/"); //$NON-NLS-1$
        if (index == -1) {
            temp = parseText;
        } else {
            temp = parseText.substring(0, index);
        }

        if (!temp.equals("*")) { //$NON-NLS-1$
            setText(temp);
        }
    }

}
