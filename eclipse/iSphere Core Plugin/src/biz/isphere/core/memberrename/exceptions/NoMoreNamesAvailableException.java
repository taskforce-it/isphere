/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.exceptions;

import biz.isphere.core.Messages;

public class NoMoreNamesAvailableException extends Exception {

    private static final long serialVersionUID = 4406671326183371654L;

    public NoMoreNamesAvailableException() {
        super();
    }

    @Override
    public String getMessage() {
        return Messages.Error_No_more_names_available_Delete_old_backups;
    }
}
