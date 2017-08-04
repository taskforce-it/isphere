/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.base.interfaces;

import java.sql.Time;
import java.util.Date;

public interface IJournalEntry {

    public static final String USER_GENERATED = "U"; //$NON-NLS-1$

    public int getRrn();

    public String getEntryType();

    public long getSequenceNumber();

    public String getJournalCode();

    public int getEntryLength();

    public Date getDate();

    public Time getTime();

    public String getJobName();

    public String getJobUserName();

    public int getJobNumber();

    public String getProgramName();

    public String getObjectLibrary();

    public String getMemberName();

    public String getObjectName();

    public String getMinimizedSpecificData();

    public String getStringSpecificData();

}
