/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import biz.isphere.core.Messages;

public class DuplicateCommandException extends Exception {

    private static final long serialVersionUID = -4674500763577067935L;

    public DuplicateCommandException() {
        super(Messages.Cannot_load_the_selected_repository_Duplicate_commands);
    }
}
