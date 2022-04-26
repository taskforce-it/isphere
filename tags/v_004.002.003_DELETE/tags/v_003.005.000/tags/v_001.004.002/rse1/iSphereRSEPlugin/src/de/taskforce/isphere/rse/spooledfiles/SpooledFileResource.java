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

package de.taskforce.isphere.rse.spooledfiles;

import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractResource;

import de.taskforce.isphere.spooledfiles.SpooledFile;

public class SpooledFileResource extends AbstractResource {
	
	private SpooledFile spooledFile;

	public SpooledFileResource(SubSystem subSystem) {
		super(subSystem);
	}

	public SpooledFileResource() {
		super();
	}

	public SpooledFile getSpooledFile() {
		return spooledFile;
	}

	public void setSpooledFile(SpooledFile spooledFile) {
		this.spooledFile = spooledFile;
	}

}
