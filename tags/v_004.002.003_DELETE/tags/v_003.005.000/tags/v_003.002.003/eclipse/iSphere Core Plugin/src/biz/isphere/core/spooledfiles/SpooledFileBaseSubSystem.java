/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.sql.Connection;

import org.eclipse.swt.widgets.Shell;

public class SpooledFileBaseSubSystem {

    public SpooledFile[] internalResolveFilterString(Shell shell, String connectionName, Connection jdbcConnection, String filterString) {
        return SpooledFileFactory.getSpooledFiles(shell, connectionName, jdbcConnection, new SpooledFileFilter(filterString));
    }

}
