/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import java.sql.Time;
import java.util.Calendar;

import biz.isphere.base.internal.StringHelper;

import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.AS400Time;

public class JournalEntryDelegate {

    public static java.sql.Date getDate(String date, int dateFormat, Character dateSeparator) {

        AS400Date as400date = new AS400Date(Calendar.getInstance().getTimeZone(), dateFormat, dateSeparator);
        java.sql.Date dateObject = as400date.parse(date);

        return new java.sql.Date(dateObject.getTime());
    }

    public static Time getTime(int time, Character timeSeparaCharacter) {

        AS400Time as400time = new AS400Time(Calendar.getInstance().getTimeZone(), AS400Time.FORMAT_HMS, timeSeparaCharacter);
        Time timeObject = as400time.parse(StringHelper.getFixLengthLeading(Integer.toString(time), 6, "0")); //$NON-NLS-1$

        return new Time(timeObject.getTime());
    }
}
