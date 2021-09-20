/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.logger;

public final class Logger {
    /**
     * When things go wrong, it is useful to be able to debug the ROT.
     */
    private static final boolean DEBUG =
    // true;
    "true".equalsIgnoreCase(System.getProperty("biz.isphere.antcontrib.debug"));

    private static boolean isDebugEnabled() {
        return DEBUG;
    }

    public void logError(String message) {
        logError(message, null);
    }

    public static void logError(String message, Throwable e) {

        System.out.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }

    public static void logWarning(String message) {

        System.out.println("WARNING: " + message);

    }

    public static void logInfo(String message) {

        if (!isDebugEnabled()) {
            return;
        }

        System.out.println("INFO: " + message);
    }
}
