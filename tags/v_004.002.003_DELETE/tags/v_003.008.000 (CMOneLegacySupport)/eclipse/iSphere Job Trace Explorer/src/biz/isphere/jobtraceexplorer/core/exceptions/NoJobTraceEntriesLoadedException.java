/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.exceptions;

import biz.isphere.jobtraceexplorer.core.Messages;

public class NoJobTraceEntriesLoadedException extends Exception {

    private static final long serialVersionUID = 1424721558051476509L;

    private String libraryName;
    private String sessionID;

    public NoJobTraceEntriesLoadedException(String libraryName, String sessionID) {

        this.libraryName = libraryName;
        this.sessionID = sessionID;
    }

    @Override
    public String getMessage() {

        return Messages.bind(Messages.Exception_No_job_trace_entries_loaded_from_library_A_and_session_id_B, libraryName, sessionID);
    }
}
