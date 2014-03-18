/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.rse.ui.view.AbstractSystemRemoteAdapterFactory;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class SpooledFileAdapterFactory extends AbstractSystemRemoteAdapterFactory implements IAdapterFactory {		
	
	private SpooledFileResourceAdapter spooledFileAdapter = new SpooledFileResourceAdapter();
	
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		ISystemViewElementAdapter adapter = null;
		if ((adaptableObject instanceof SpooledFileResource))
			adapter = spooledFileAdapter;
		if ((adapter != null) && (adapterType == IPropertySource.class))
			adapter.setPropertySourceInput(adaptableObject);
		return adapter;
	}

}
