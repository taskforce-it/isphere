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
import java.util.Date;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Date;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Time;

public class JournalEntryDelegate {

    public static Date getDate(String date, int dateFormat, Character dateSeparator) {

        AS400Date as400date = new AS400Date(Calendar.getInstance().getTimeZone(), dateFormat, dateSeparator);
        java.sql.Date dateObject = as400date.parse(date);

        return new Date(dateObject.getTime());
    }

    public static Time getTime(int time, Character timeSeparator) {

        AS400Time as400time = new AS400Time(Calendar.getInstance().getTimeZone(), AS400Time.FORMAT_HMS, timeSeparator);
        Time timeObject = as400time.parse(StringHelper.getFixLengthLeading(Integer.toString(time), 6));

        return new Time(timeObject.getTime());
    }
}
