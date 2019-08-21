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

package biz.isphere.rse.spooledfiles;

import biz.isphere.core.preferencepages.IPreferences;

public class SpooledFileSaveAsHTMLAction extends AbstractSpooledFileAction {

	public String execute(SpooledFileResource spooledFileResource) {
		return spooledFileResource.getSpooledFile().save(getShell(), IPreferences.OUTPUT_FORMAT_HTML);
	}

}