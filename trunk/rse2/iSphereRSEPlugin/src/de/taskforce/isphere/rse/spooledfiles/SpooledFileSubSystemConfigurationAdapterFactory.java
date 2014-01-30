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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.rse.ui.subsystems.ISubSystemConfigurationAdapter;

public class SpooledFileSubSystemConfigurationAdapterFactory implements IAdapterFactory {
	
	private ISubSystemConfigurationAdapter ssConfigAdapter = new SpooledFileSubSystemConfigurationAdapter();

	public Class[] getAdapterList() {
		return new Class[] { ISubSystemConfigurationAdapter.class };
	}

	public void registerWithManager(IAdapterManager manager) {
		manager.registerAdapters(this, SpooledFileSubSystemFactory.class);
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object adapter = null;
		if ((adaptableObject instanceof SpooledFileSubSystemFactory)) {
			adapter = this.ssConfigAdapter;
		}
		return adapter;
	}
	
}