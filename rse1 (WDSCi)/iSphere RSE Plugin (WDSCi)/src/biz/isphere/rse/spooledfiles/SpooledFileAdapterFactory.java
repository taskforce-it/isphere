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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.ibm.etools.systems.core.ui.view.AbstractSystemRemoteAdapterFactory;
import com.ibm.etools.systems.core.ui.view.ISystemViewElementAdapter;

public class SpooledFileAdapterFactory extends AbstractSystemRemoteAdapterFactory implements IAdapterFactory {

    private SpooledFileResourceAdapter spooledFileAdapter = new SpooledFileResourceAdapter();

    public SpooledFileAdapterFactory() {
        super();
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        ISystemViewElementAdapter adapter = null;
        if (adaptableObject instanceof SpooledFileResource) adapter = spooledFileAdapter;
        if ((adapter != null) && (adapterType == IPropertySource.class)) adapter.setPropertySourceInput(adaptableObject);
        return adapter;

    }

}
