/*******************************************************************************
 * Copyright (c) project_year-2021 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit.base;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import biz.isphere.base.internal.IBMiHelper;

public class TestIBMiHelper {

    @Test
    public void testHhmmssToTime() {

        String time;
        String nanos;
        Date expected;
        Date actual;

        time = "031844";
        expected = getExpectedTime(time);
        actual = IBMiHelper.hhmmssToTime(time);
        assertEquals(expected.getTime(), actual.getTime());

        time = "031844";
        nanos = "545559";
        expected = getExpectedTime(time, nanos);
        actual = IBMiHelper.hhmmssToTime(time + nanos);
        assertEquals(expected.getTime(), actual.getTime());

        time = "131844";
        expected = getExpectedTime(time);
        actual = IBMiHelper.hhmmssToTime(time);
        assertEquals(expected.getTime(), actual.getTime());

        time = "131844";
        nanos = "545559";
        expected = getExpectedTime(time, nanos);
        actual = IBMiHelper.hhmmssToTime(time + nanos);
        assertEquals(expected.getTime(), actual.getTime());
    }

    private Timestamp getExpectedTime(String time) {
        if (time.length() > 6) {
            String nanos = time.substring(6);
            return getExpectedTime(time, nanos);
        } else {
            return getExpectedTime(time, null);
        }
    }

    private Timestamp getExpectedTime(String time, String nanos) {

        String hours = time.substring(0, 2);
        String minutes = time.substring(2, 4);
        String seconds = time.substring(4, 6);

        return getExpectedTime(hours, minutes, seconds, nanos);
    }

    private Timestamp getExpectedTime(String hours, String minutes, String seconds, String nanos) {

        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
        c.set(Calendar.MINUTE, Integer.parseInt(minutes));
        c.set(Calendar.SECOND, Integer.parseInt(seconds));

        Timestamp timestamp = new Timestamp(c.getTimeInMillis());

        if (nanos != null) {
            timestamp.setNanos(Integer.parseInt(nanos) * 1000);
        }

        return timestamp;
    }

}
