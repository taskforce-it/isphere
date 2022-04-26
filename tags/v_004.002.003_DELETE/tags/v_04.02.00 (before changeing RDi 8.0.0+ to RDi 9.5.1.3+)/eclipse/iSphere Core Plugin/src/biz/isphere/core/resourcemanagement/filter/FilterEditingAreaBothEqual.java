/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractResource;

public class FilterEditingAreaBothEqual extends AbstractFilterEditingArea {

    public FilterEditingAreaBothEqual(Composite parent, AbstractResource[] resources, boolean both, boolean singleFilterPool) {
        super(parent, resources, both, singleFilterPool);
    }

    @Override
    protected String[] getActions(boolean both) {
        return getActionsBothEqual();
    }

    public String getTitle() {
        return Messages.Filters + " " + getTitleBothEqual() + " " + Messages.type_and_string + " (" + getNumberOfItems() + ")";
    }

}
