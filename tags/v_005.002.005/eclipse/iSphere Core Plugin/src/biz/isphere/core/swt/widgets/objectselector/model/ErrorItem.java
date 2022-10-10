/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

public class ErrorItem extends AbstractListItem {

    public ErrorItem(String errorMessage) {
        super(null, null, errorMessage, null);
    }

    @Override
    public String getLabel() {
        return super.getName();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public AbstractListItem[] resolveChildren() {
        return null;
    }
}
