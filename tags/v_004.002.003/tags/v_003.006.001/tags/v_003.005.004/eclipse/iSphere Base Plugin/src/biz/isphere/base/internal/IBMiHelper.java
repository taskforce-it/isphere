/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.InternalErrorException;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.as400.access.RequestNotSupportedException;
import com.ibm.as400.access.SystemValue;
import com.ibm.as400.access.Trace;

public final class IBMiHelper {

    private static final SimpleDateFormat cyymmddFormatter = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat hhmmssFormatter = new SimpleDateFormat("HHmmss");
    private static final SimpleDateFormat yymmddFormatter = new SimpleDateFormat("yyMMdd");

    public static String quote(String text) {

        StringBuilder quotedText = new StringBuilder();

        quotedText.append("'");
        quotedText.append(text.replaceAll("'", "''"));
        quotedText.append("'");

        return quotedText.toString();
    }

    public static String dateToCyymmdd(Date date, String defaultValue) {

        String cyymmdd;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (calendar.get(Calendar.YEAR) >= 2000) {
                cyymmdd = "1" + yymmddFormatter.format(calendar.getTime());
            } else {
                cyymmdd = "0" + yymmddFormatter.format(calendar.getTime());
            }
        } catch (Exception e) {
            return defaultValue;
        }

        return cyymmdd;
    }

    public static Date cyymmddToDate(String cyymmdd) {

        if ("1".equals(cyymmdd.substring(0, 1))) {
            cyymmdd = "20" + cyymmdd.substring(1);
        } else {
            cyymmdd = "19" + cyymmdd.substring(1);
        }

        Date date;
        try {
            date = cyymmddFormatter.parse(cyymmdd);
        } catch (ParseException e) {
            return null;
        }

        return date;
    }

    public static String timeToHhmmss(Date time, String defaultValue) {

        String hhmmss;
        try {
            hhmmss = hhmmssFormatter.format(time);
        } catch (Exception e) {
            return defaultValue;
        }

        return hhmmss;
    }

    public static Date hhmmssToTime(String hhmmss) {

        Date time;
        try {
            time = hhmmssFormatter.parse(hhmmss);
        } catch (ParseException e) {
            return null;
        }

        return time;
    }

    private static TimeZone callIBMDateTimeConverter(AS400 system) {

        try {

            String className = com.ibm.as400.access.DateTimeConverter.class.getName();
            Object object = Class.forName(className).getConstructor(AS400.class).newInstance(system);
            Method method = object.getClass().getMethod("timeZoneForSystem", AS400.class);

            if (method != null) {
                Object timeZone = method.invoke(object, system);
                return (TimeZone)timeZone;
            }

        } catch (Throwable e) {
            // Ignore errors
        }

        return null;
    }

    // /////////////////////////////////////////////////////////////////////////////
    //
    // JTOpen (IBM Toolbox for Java - OSS version)
    //
    // Filename: DateTimeConverter.java
    //
    // The source code contained herein is licensed under the IBM Public License
    // Version 1.0, which has been approved by the Open Source Initiative.
    // Copyright (C) 1997-2004 International Business Machines Corporation and
    // others. All rights reserved.
    //
    // /////////////////////////////////////////////////////////////////////////////
    // 2008-05-21 @A1 Changes for *CURRENT when returning Date objects. Adjust
    // the AS400 system time to the local client time.
    // /////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a TimeZone object to represent the time zone for the specified
     * system. The TimeZone object will have the correct UTC offset for the
     * system.
     * 
     * @param system The IBM i system.
     * @return A TimeZone object representing the time zone for the system.
     * @exception AS400SecurityException If a security or authority error
     *            occurs.
     * @exception ErrorCompletingRequestException If an error occurs before the
     *            request is completed.
     * @exception InterruptedException If this thread is interrupted.
     * @exception IOException If an error occurs while communicating with the
     *            system.
     * @exception ObjectDoesNotExistException If the API used to retrieve the
     *            information does not exist on the system.
     * @see AS400#getTimeZone()
     **/
    public static TimeZone timeZoneForSystem(AS400 system) throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException,
        IOException, ObjectDoesNotExistException {

        // iSphere: first try to call the original IBM method.
        // That should work for RDi 8.0+.
        TimeZone timeZone = callIBMDateTimeConverter(system);
        if (timeZone != null) {
            return timeZone;
        }

        // iSphere: For WDSCi use the JT400 source code.

        // Using the UTC offset does not properly account for the use of
        // daylight savings time. We use
        // the QTIMZON value to adjust for those areas that use daylight savings
        // time.

        try {
            SystemValue sv = new SystemValue(system, "QTIMZON");
            // returns a value such as "-0500"
            String iTimeZone = (String)sv.getValue();
            String javaTimeZoneName = iTimeZoneToJavaTimeZone(iTimeZone);
            if (javaTimeZoneName != null) {
                return TimeZone.getTimeZone(javaTimeZoneName);
            }
        } catch (Exception e) {
            // Log the exception and continue
            Trace.log(Trace.ERROR, e);
        }

        // If the new method does not work, fall back to the old method of
        // getting the timezone.
        // To obtain a standard ID for the time zone, simply concatenate "GMT"
        // and the QUTCOFFSET value.

        String utcOffset = null;
        try {
            SystemValue sv = new SystemValue(system, "QUTCOFFSET");
            // returns a value such as "-0500"
            utcOffset = (String)sv.getValue();
            if (utcOffset == null || utcOffset.length() == 0) {
                if (Trace.isTraceOn()) {
                    Trace.log(Trace.DIAGNOSTIC, "QUTCOFFSET is not set. Assuming server is in the same time zone as client application.");
                }
                return TimeZone.getDefault();
            } else
                return TimeZone.getTimeZone("GMT" + utcOffset);
        } catch (RequestNotSupportedException e) // this won't happen
        { // ... but if it does happen, trace it and rethrow as a runtime
          // exception
            Trace.log(Trace.ERROR, e);
            throw new InternalErrorException(InternalErrorException.UNEXPECTED_EXCEPTION);
        } catch (RuntimeException e) {
            // Note: We've observed the following error during testing:
            // java.lang.NullPointerException at
            // java.util.TimeZone.parseCustomTimeZone()
            if (Trace.isTraceOn()) {
                Trace.log(Trace.WARNING, "[" + e.toString() + "] Unable to determine time zone of system. " + "QUTCOFFSET value is " + utcOffset
                    + ". " + "Assuming server is in the same time zone as client application.");
            }
            return TimeZone.getDefault();
        }
    }

    static Hashtable<String, String> iTimeZoneToJavaTimeZoneHash = null;

    /**
     * Returns the name of the java timezone corresponding to the IBM i
     * timezone.
     */
    static String iTimeZoneToJavaTimeZone(String iTimeZone) {
        iTimeZone = iTimeZone.toUpperCase();
        synchronized (iTimeZoneTojavaTimeZoneMapping) {
            if (iTimeZoneToJavaTimeZoneHash == null) {
                iTimeZoneToJavaTimeZoneHash = new Hashtable<String, String>();
                for (int i = 0; i < iTimeZoneTojavaTimeZoneMapping.length; i++) {
                    iTimeZoneToJavaTimeZoneHash.put(iTimeZoneTojavaTimeZoneMapping[i][0], iTimeZoneTojavaTimeZoneMapping[i][1]);
                }
            }
        }
        return (String)iTimeZoneToJavaTimeZoneHash.get(iTimeZone);
    }

    //
    // These are the values shipped with the system.
    // See
    // http://publib.boulder.ibm.com/infocenter/iseries/v7r1m0/index.jsp?topic=/rzati/rzatitimezone.htm
    // @CAA
    // iSphere:
    // https://www.ibm.com/support/knowledgecenter/ssw_ibm_i_73/rzati/rzatitimezone.htm
    static String iTimeZoneTojavaTimeZoneMapping[][] = {
        // @formatter:off
        {"Q0000UTC","UTC"},
        {"Q0000GMT","GMT"},
        {"Q0000GMT2","Europe/London"},
        {"Q000GMT3","Europe/London"},
        {"QN0100UTCS","GMT-1"},
        {"QN0200UTCS","GMT-2"},
        {"QN0300UTCS","GMT-3"},
        {"QN0300CLT", "America/Santiago"}, // iSphere: new with 7.3
        {"QN0300UTC2","America/Sao_Paulo"},
        {"QN0330NST","America/St_Johns"},
        {"QN0330NST2","America/St_Johns"},
        {"QN0330NST3","America/St_Johns"},
        {"QN0330NST4","America/St_Johns"},
        {"QN0400UTCS","GMT-4"},
        {"QN0400AST","Atlantic/Bermuda"},
        {"QN0400AST2","Atlantic/Bermuda"},
        {"QN0400CLT","America/Santiago"},
        {"QN0400UTC2","America/Caracas"},
        {"QN0500UTCS","GMT-5"},
        {"QN0500EST","America/New_York"},
        {"QN0500EST2","GMT-5"},
        {"QN0500EST3","America/New_York"},
        {"QN0600UTCS","GMT-6"},
        {"QN0600CST","America/Chicago"},
        {"QN0600CST2","America/Chicago"},
        {"QN0600CST3","America/Mexico_City"},
        {"QN0600S","America/Chicago"},
        {"QN0700UTCS","GMT-7"},
        {"QN0700MST","America/Denver"},
        {"QN0700MST2","America/Phoenix"},
        {"QN0700MST3","America/Denver"},
        {"QN0700MST4","America/Mazatlan"},
        {"QN0700T","America/Denver"},
        {"QN0800UTCS","GMT-8"},
        {"QN0800PST","America/Los_Angeles"},
        {"QN0800PST2","America/Los_Angeles"},
        {"QN0800PST3","America/Tijuana"},
        {"QN0800U","America/Los_Angeles"},
        {"QN0900UTCS","GMT-9"},
        {"QN0900AST","America/Anchorage"},
        {"QN0900AST2","America/Anchorage"},
        {"QN1000UTCS","GMT-10"},
        {"QN1000HAST","America/Adak"},
        {"QN1000HAS2","America/Adak"},
        {"QN1000HST","Pacific/Honolulu"},
        {"QN1100UTCS","GMT-11"},
        {"QN1200UTCS","GMT-12"},
        {"QP1245UTCS","Pacific/Chatham"},
        {"QP1245UTC2","Pacific/Chatham"},
        {"QP1200UTCS","GMT+12"},
        {"QP1200NZST","Pacific/Auckland"},
        {"QP1200NZS2","Pacific/Auckland"},
        {"QP1200NZS3","Pacific/Auckland"},
        {"QP1100UTCS","GMT+11"},
        {"QP1000UTCS","GMT+10"},
        {"QP1000AEST","Australia/Sydney"},
        {"QP1000AES2","Australia/Sydney"},
        {"QP0930ACST","Australia/Adelaide"},
        {"QP0930ACS2","Australia/Adelaide"},
        {"QP0900UTCS","GMT+9"},
        {"QP0900JST","Asia/Tokyo"},
        {"QP0900KST","Asia/Seoul"},
        {"QP0900WIT","Asia/Jayapura"},
        {"QP0800UTCS","GMT+8"},
        {"QP0800AWST","Australia/Perth"},
        {"QP0800AWS2","Australia/Perth"},
        {"QP0800AWS3","Australia/Perth"},
        {"QP0800BST","Asia/Shanghai"},
        {"QP0800JIST","Asia/Hong_Kong"},
        {"QP0800WITA","Asia/Ujung_Pandang"},
        {"QP0700UTCS","GMT+7"},
        {"QP0700WIB","Asia/Jakarta"},
        {"QP0600UTCS","GMT+6"},
        {"QP0600UTC2","Asia/Almaty"},
        {"QP0600UTC3","Asia/Almaty"},
        {"QP0530IST","Asia/Calcutta"},
        {"QP0500UTCS","GMT+5"},
        {"QP0500UTC2","Asia/Aqtobe"},
        {"QP0500UTC3","Asia/Aqtobe"},
        {"QP0400UTCS","GMT+4"},
        {"QP0400UTC2","Asia/Aqtau"},
        {"QP0400UTC3","Asia/Aqtau"},
        {"QP0300MSK","Europe/Moscow"},
        {"QP0300MSK2","Europe/Moscow"}, // iSphere: new with 7.3
        {"QP0300UTCS","GMT+3"},
        {"QP0200UTCS","GMT+2"},
        {"QP0200EET","Europe/Tallinn"},
        {"QP0200EET2","GMT+2"},
        {"QP0200EET3","Europe/Tallinn"},
        {"QP0200SAST","Africa/Johannesburg"},
        {"QP0100UTCS","GMT+1"},
        {"QP0100CET","Europe/Zurich"},
        {"QP0100CET2","Europe/Zurich"},
        {"QP0100CET3","Europe/Zurich"},
        {"QP0100CET4","Europe/Zurich"},
        // @formatter:on
    };

}
