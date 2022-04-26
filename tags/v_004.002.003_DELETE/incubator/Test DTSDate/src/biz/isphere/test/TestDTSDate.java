/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.DateTimeConverter;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.ObjectDoesNotExistException;

public class TestDTSDate {

    /**
     * Set the following system properties to run this test class:
     * 
     * <pre>
     * -DHOST=your host name
     * -DUSER=user name
     * -DPASSWORD=password
     * 
     * Performance data:
     * 
     * Loading journal 1283 journal entries with DateTimeConverter (QWCCVTDT API):
     * - Loading journal entries took: 65535 mSecs
     * - Time per entry; 47 mSecs
     * 
     * Loading journal 1283 journal entries with DTSDate:
     * - Loading journal entries took: 4361 mSecs
     * - Time per entry; 3 mSecs
     * 
     * <pre>
     */

    private AS400 system;
    private TimeZone timeZone;
    private Calendar remoteCalendar;
    private int offsetMinutes;

    public TestDTSDate() {
        String host = System.getProperty("HOST");
        String user = System.getProperty("USER");
        String password = System.getProperty("PASSWORD");
        this.system = new AS400(host, user, password);

        try {
            timeZone = IBMiHelper.timeZoneForSystem(system);
            remoteCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
            Calendar localCalendar = GregorianCalendar.getInstance();
            int remoteOffset2GMT = (remoteCalendar.get(Calendar.ZONE_OFFSET) + remoteCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
            int localOffset2GMT = (localCalendar.get(Calendar.ZONE_OFFSET) + localCalendar.get(Calendar.DST_OFFSET)) / (60 * 1000);
            offsetMinutes = localOffset2GMT - remoteOffset2GMT;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        new TestDTSDate().run();
    }

    private void run() throws Exception {

        List<byte[]> falseTestCases = new ArrayList<byte[]>();

        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEB, (byte)0x10, (byte)0xAD, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0x01, (byte)0x5C, (byte)0x30, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0x0A, (byte)0x62, (byte)0x3E, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0x17, (byte)0x3A, (byte)0xD2, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0x1C, (byte)0x8E, (byte)0x53, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0xAE, (byte)0x89, (byte)0xB2, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0xCB, (byte)0x3D, (byte)0x58, (byte)0x00, (byte)0x00 });
        falseTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2D, (byte)0xE7, (byte)0xEC, (byte)0xD8, (byte)0x00, (byte)0x00 });

        System.out.println("Num errors: " + showResult(falseTestCases) + "\n");

        List<byte[]> goodTestCases = new ArrayList<byte[]>();

        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEB, (byte)0x11, (byte)0x03, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEB, (byte)0x11, (byte)0x50, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEB, (byte)0x11, (byte)0x50, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xED, (byte)0x2C, (byte)0x01, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xED, (byte)0x2C, (byte)0x01, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEF, (byte)0x43, (byte)0x7C, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEF, (byte)0x43, (byte)0x7C, (byte)0x00, (byte)0x00 });
        goodTestCases.add(new byte[] { (byte)0xA6, (byte)0x04, (byte)0x2C, (byte)0xEF, (byte)0x8C, (byte)0x70, (byte)0x00, (byte)0x00 });

        System.out.println("Num errors: " + showResult(goodTestCases) + "\n");
    }

    private int showResult(List<byte[]> falseTestCases) throws AS400SecurityException, ErrorCompletingRequestException, InterruptedException,
        IOException, ObjectDoesNotExistException {

        // IBMiHelper.timeZoneForSystem(system);

        DTSDate dtsDateCnv = new DTSDate();
        DateTimeConverter dateTimeConverter = new DateTimeConverter(system);

        int count = 0;

        for (byte[] testCase : falseTestCases) {

            Date dtsDateCnvDate = convertToLocalTimeZone(dtsDateCnv.getDate(testCase));
            long dtsDateMSecs = dtsDateCnvDate.getTime();

            System.out.println("Timezone offset (dts): " + dtsDateCnvDate.getTimezoneOffset());
            System.out.println("Timestamp (dts): " + getTimestamp(dtsDateMSecs));

            Date dateTimeConverterDate = dateTimeConverter.convert(testCase, "*DTS");
            long dateTimeConverterMSecs = dateTimeConverterDate.getTime();

            System.out.println("Timezone offset (jto): " + dateTimeConverterDate.getTimezoneOffset());
            System.out.println("Timestamp (jto): " + getTimestamp(dateTimeConverterMSecs));

            if (dtsDateMSecs != dateTimeConverterMSecs) {
                count++;
                System.out.println("Error: Unexpected difference #" + (dtsDateMSecs - dateTimeConverterMSecs));
            }
            System.out.println();
        }

        return count;
    }

    private String getTimestamp(long milliSeconds) {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliSeconds);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS");

        return formatter.format(c.getTime());
    }

    private Date convertToLocalTimeZone(Date timestamp) {

        /*
         * Do not convert when the IDE is WDSCi 7.0. In this case the IBM
         * DateTimeConverter did not return the timestamp with the timezone of
         * the IBM i, but with the local timezone of the PC client.
         */

        // if (isWDSCi) {
        // return timestamp;
        // }

        if (offsetMinutes >= 0) {
            remoteCalendar.clear();
            remoteCalendar.setTime(timestamp);
            remoteCalendar.add(Calendar.MINUTE, offsetMinutes * -1);
            timestamp = remoteCalendar.getTime();
        }

        return timestamp;
    }
}
