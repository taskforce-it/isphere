/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.Job;

public final class JUnitHelper {

    private JUnitHelper() {
        // Helper, which must not be instantiated.
    }

    public static void main(String[] args) throws Exception {
        getCurrentLibrary(new AS400("ghentw.gfd.de", "webuser", "webuser"));
    }

    public static String getCurrentLibrary(AS400 _as400) throws Exception {

        String currentLibrary = null;

        try {

            _as400.connectService(AS400.COMMAND);
            Job[] jobs = _as400.getJobs(AS400.COMMAND);

            if (jobs.length == 1) {

                if (!jobs[0].getCurrentLibraryExistence()) {
                    currentLibrary = "*CRTDFT"; //$NON-NLS-1$
                } else {
                    currentLibrary = jobs[0].getCurrentLibrary();
                }

            } else {
                System.out.println("ERROR: Current library could not be retrieved!");
            }

        } finally {

            if (_as400.isConnected(AS400.COMMAND)) {
                _as400.disconnectService(AS400.COMMAND);
            }

        }

        return currentLibrary;
    }

    public static boolean setCurrentLibrary(AS400 _as400, String currentLibrary) throws Exception {

        String command = "CHGCURLIB CURLIB(" + currentLibrary + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        CommandCall commandCall = new CommandCall(_as400);

        if (commandCall.run(command)) {
            return true;
        } else {
            return false;
        }
    }
}
