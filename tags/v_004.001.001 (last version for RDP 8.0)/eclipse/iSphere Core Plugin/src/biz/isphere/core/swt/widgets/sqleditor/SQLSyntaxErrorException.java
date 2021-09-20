/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.sqleditor;

import biz.isphere.core.Messages;

public class SQLSyntaxErrorException extends Exception {

    private static final long serialVersionUID = -3433495686966681950L;

    public SQLSyntaxErrorException(Exception e) {
        super(Messages.bind(Messages.Error_in_SQL_WHERE_CLAUSE_A, e.getLocalizedMessage()));
    }
}
