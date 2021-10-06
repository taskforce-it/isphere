/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import biz.isphere.core.dataspaceeditordesigner.model.AbstractDWidget;

public class DataModifiedEvent {

    private AbstractDWidget widget;

    public DataModifiedEvent(AbstractDWidget widget) {
        this.widget = widget;
    }

    public AbstractDWidget getSource() {
        return widget;
    }

}
