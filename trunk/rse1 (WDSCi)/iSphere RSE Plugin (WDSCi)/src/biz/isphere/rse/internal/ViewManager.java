/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import biz.isphere.core.internal.viewmanager.AbstractViewManager;

public class ViewManager extends AbstractViewManager {

    public ViewManager(String name) {
        super(name);
    }

    @Override
    public boolean isInitialized(int timeout) {
        return true;
    }

}
