/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.exceptions;

import biz.isphere.journalexplorer.core.Messages;

public class BufferTooSmallException extends Exception {

    private static final long serialVersionUID = 1133894286633692196L;

    public static final String ID = "RJE0001";

    public BufferTooSmallException() {
    }

    @Override
    public String getMessage() {
        return Messages.Exception_Buffer_too_small_to_retrieve_next_journal_entry_Check_preferences;
    }
}
