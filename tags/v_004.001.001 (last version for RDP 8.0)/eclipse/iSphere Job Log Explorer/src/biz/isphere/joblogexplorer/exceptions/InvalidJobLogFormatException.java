/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

import biz.isphere.joblogexplorer.Messages;

public class InvalidJobLogFormatException extends BasicJobLogLoaderException {

    private static final long serialVersionUID = 411713919999706178L;

    public InvalidJobLogFormatException() {
        super(Messages.Invalid_job_log_Format_Could_not_find_first_line_of_job_log);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return message;
    }
}
