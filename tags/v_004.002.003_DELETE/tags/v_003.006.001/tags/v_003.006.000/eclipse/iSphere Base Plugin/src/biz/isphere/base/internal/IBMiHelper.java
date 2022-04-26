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
    private static final SimpleDateFormat ddmmyyFormatter = new SimpleDateFormat("ddMMyy");
    private static final SimpleDateFormat mmddyyFormatter = new SimpleDateFormat("MMddyy");
    private static final SimpleDateFormat julianFormatter = new SimpleDateFormat("yyDDD");

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

            int year = calendar.get(Calendar.YEAR);

            if (year >= 2800) {
                cyymmdd = "9" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2700) {
                cyymmdd = "8" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2600) {
                cyymmdd = "7" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2500) {
                cyymmdd = "6" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2400) {
                cyymmdd = "5" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2300) {
                cyymmdd = "4" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2200) {
                cyymmdd = "3" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2100) {
                cyymmdd = "2" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 2000) {
                cyymmdd = "1" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else if (year >= 1900) {
                cyymmdd = "0" + yymmddFormatter.format(calendar.getTime()); //$NON-NLS-1$
            } else {
                // Illegal parameter value
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }

        return cyymmdd;
    }

    public static Date cyymmddToDate(String cyymmdd) {

        int century = Integer.parseInt(cyymmdd.substring(0, 1));
        String date6 = cyymmdd.substring(1);

        switch (century) {
        case 0:
            cyymmdd = "19" + date6; //$NON-NLS-1$
            break;
        case 1:
            cyymmdd = "20" + date6; //$NON-NLS-1$
            break;
        case 2:
            cyymmdd = "21" + date6; //$NON-NLS-1$
            break;
        case 3:
            cyymmdd = "22" + date6; //$NON-NLS-1$
            break;
        case 4:
            cyymmdd = "23" + date6; //$NON-NLS-1$
            break;
        case 5:
            cyymmdd = "24" + date6; //$NON-NLS-1$
            break;
        case 6:
            cyymmdd = "25" + date6; //$NON-NLS-1$
            break;
        case 7:
            cyymmdd = "26" + date6; //$NON-NLS-1$
            break;
        case 8:
            cyymmdd = "27" + date6; //$NON-NLS-1$
            break;
        case 9:
            cyymmdd = "28" + date6; //$NON-NLS-1$
            break;
        default:
            throw new IllegalArgumentException("Parameter 'century' of 'cyymmdd' is out of range 0-9: " + century); //$NON-NLS-1$
        }

        Date date;
        try {
            date = cyymmddFormatter.parse(cyymmdd);
        } catch (ParseException e) {
            return null;
        }

        return date;
    }

    public static String dateToYMD(Date date, String defaultValue) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        if (!isValid4DigitYear(year)) {
            // Illegal parameter value
            return defaultValue;
        }

        String yymmdd;
        try {
            yymmdd = yymmddFormatter.format(date);
        } catch (Exception e) {
            return defaultValue;
        }

        return yymmdd;
    }

    public static Date ymdToDate(String ymd) {

        int year = get2DigitInt(ymd, 0);
        if (!isValid2DigitYear(year)) {
            throw new IllegalArgumentException("Parameter 'ymd' is out of range 1940-2039: " + ymd); //$NON-NLS-1$
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, get4DigitYear(year));
            calendar.set(Calendar.MONTH, get2DigitInt(ymd, 2) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, get2DigitInt(ymd, 4));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToDMY(Date date, String defaultValue) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        if (!isValid4DigitYear(year)) {
            // Illegal parameter value
            return defaultValue;
        }

        String ddmmyy;
        try {
            ddmmyy = ddmmyyFormatter.format(date);
        } catch (Exception e) {
            return defaultValue;
        }

        return ddmmyy;
    }

    public static Date dmyToDate(String dmy) {

        int year = get2DigitInt(dmy, 4);
        if (!isValid2DigitYear(year)) {
            throw new IllegalArgumentException("Parameter 'dmy' is out of range 1940-2039: " + dmy); //$NON-NLS-1$
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, get4DigitYear(year));
            calendar.set(Calendar.MONTH, get2DigitInt(dmy, 2) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, get2DigitInt(dmy, 0));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToMDY(Date date, String defaultValue) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        if (!isValid4DigitYear(year)) {
            // Illegal parameter value
            return defaultValue;
        }

        String mmssyy;
        try {
            mmssyy = mmddyyFormatter.format(date);
        } catch (Exception e) {
            return defaultValue;
        }

        return mmssyy;
    }

    public static Date mdyToDate(String mdy) {

        int year = get2DigitInt(mdy, 4);
        if (!isValid2DigitYear(year)) {
            throw new IllegalArgumentException("Parameter 'mdy' is out of range 1940-2039: " + mdy); //$NON-NLS-1$
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, get4DigitYear(year));
            calendar.set(Calendar.MONTH, get2DigitInt(mdy, 0) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, get2DigitInt(mdy, 2));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    public static String dateToJulian(Date date, String defaultValue) {

        String julian;
        try {
            julian = julianFormatter.format(date);
        } catch (Exception e) {
            return defaultValue;
        }

        return julian;
    }

    public static Date julianToDate(String julian) {

        int year = get2DigitInt(julian, 0);
        if (!isValid2DigitYear(year)) {
            throw new IllegalArgumentException("Parameter 'julian' is out of range 1940-2039: " + julian); //$NON-NLS-1$
        }

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, get4DigitYear(year));
            calendar.set(Calendar.DAY_OF_YEAR, get2DigitInt(julian, 2));
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
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

    private static boolean isValid4DigitYear(int year) {
        return year >= 1940 && year <= 2039;
    }

    private static boolean isValid2DigitYear(int year) {
        return true;
    }

    private static int get4DigitYear(int year2Digit) {

        if (year2Digit <= 39) {
            return 2000 + year2Digit;
        }

        return 1900 + year2Digit;
    }

    private static int get2DigitInt(String value, int offset) {
        return Integer.parseInt(value.substring(offset, offset + 2));
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
