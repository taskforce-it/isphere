/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.model;

import org.eclipse.core.runtime.IAdapterFactory;
import com.ibm.etools.systems.core.ui.view.*;
import org.eclipse.ui.views.properties.IPropertySource;

public class RSESessionAdapterFactory extends AbstractSystemRemoteAdapterFactory implements IAdapterFactory {

    private RSESessionAdapter rseSessionAdapter = new RSESessionAdapter();

    public RSESessionAdapterFactory() {
        super();
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        ISystemViewElementAdapter adapter = null;
        if (adaptableObject instanceof RSESession) adapter = rseSessionAdapter;
        if ((adapter != null) && (adapterType == IPropertySource.class)) adapter.setPropertySourceInput(adaptableObject);
        return adapter;
    }

}
