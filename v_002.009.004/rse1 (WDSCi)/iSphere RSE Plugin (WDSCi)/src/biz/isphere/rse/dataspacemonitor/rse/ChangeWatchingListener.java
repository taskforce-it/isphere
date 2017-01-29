/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspacemonitor.rse;

import biz.isphere.core.dataspacemonitor.rse.AbstractChangeWatchingListener;
import biz.isphere.core.dataspacemonitor.rse.WatchItemManager;
import biz.isphere.core.internal.IControlDecoration;

public class ChangeWatchingListener extends AbstractChangeWatchingListener {

    private WatchItemManager watchManager;
    private IControlDecoration decorator;

    public ChangeWatchingListener(WatchItemManager watchManager, IControlDecoration decorator) {
        this.watchManager = watchManager;
        this.decorator = decorator;
    }

    @Override
    protected boolean isVisible() {
        if (decorator == null) {
            return false;
        }
        return decorator.isVisible();
    }

    @Override
    protected void setVisible(boolean visible) {

        if (visible) {
            watchManager.addControl(decorator, getControlValue(decorator.getControl()));
        } else {
            watchManager.removeControl(decorator);
        }
    }
}
