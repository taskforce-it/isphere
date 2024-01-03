/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.util.Calendar;

public final class TimeHelper {

    private TimeHelper() {
    }

    public static long getStartTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static void printTimeUsed(long startTime) {
        System.out.println("Time used: " + (Calendar.getInstance().getTimeInMillis() - startTime) + "ms");
    }

    public static void printTimeUsed(String text, long startTime) {
        System.out.println(text + (Calendar.getInstance().getTimeInMillis() - startTime) + "ms");
    }
}
