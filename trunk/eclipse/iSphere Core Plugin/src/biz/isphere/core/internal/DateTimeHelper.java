/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.Calendar;
import java.util.Date;

import biz.isphere.core.preferences.Preferences;

public final class DateTimeHelper {

    public static String getDateFormatted(Date date) {
        return Preferences.getInstance().getDateFormatter().format(date);
    }

    public static String getTimeFormatted(Date time) {
        return Preferences.getInstance().getTimeFormatter().format(time);
    }

    public static String getTimestampFormatted(Date timestamp) {

        StringBuilder buffer = new StringBuilder();

        buffer.append(getDateFormatted(timestamp));
        buffer.append("   ");
        buffer.append(getTimeFormatted(timestamp));

        return buffer.toString();
    }

    public static Date combineDateTime(Date date, Date time) {

        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(time);

        calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
        calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
        calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
        calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

        Date timestamp = calendarA.getTime();

        return timestamp;
    }
}
