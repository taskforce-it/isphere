/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import biz.isphere.core.Messages;

public class InvalidRepositoryVersionException extends Exception {

    private static final long serialVersionUID = -4598425311514493441L;

    public InvalidRepositoryVersionException(String versionNumber) {
        super(Messages.bind(Messages.Cannot_load_the_selected_repository_Version_number_too_old, versionNumber));
    }
}
