/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

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