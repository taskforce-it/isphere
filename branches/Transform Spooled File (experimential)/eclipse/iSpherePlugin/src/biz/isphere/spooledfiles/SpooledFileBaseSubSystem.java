/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.spooledfiles;

import java.sql.Connection;

import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.AS400;

public class SpooledFileBaseSubSystem {

	public SpooledFile[] internalResolveFilterString(Shell shell, AS400 as400, Connection jdbcConnection, String filterString) {
		return SpooledFileFactory.getSpooledFiles(shell, as400, jdbcConnection, new SpooledFileFilter(filterString));
	}
	
}
