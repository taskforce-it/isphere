/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.rse.core.subsystems.AbstractResource;
import org.eclipse.rse.core.subsystems.ISubSystem;

import biz.isphere.core.spooledfiles.SpooledFile;


public class SpooledFileResource extends AbstractResource {
	
	private SpooledFile spooledFile;

	public SpooledFileResource() {
		super();
	}

	public SpooledFileResource(ISubSystem subSystem) {
		super(subSystem);
	}

	public SpooledFile getSpooledFile() {
		return spooledFile;
	}

	public void setSpooledFile(SpooledFile spooledFile) {
		this.spooledFile = spooledFile;
	}

}
