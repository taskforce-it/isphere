/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide.lpex.menu.model;

public abstract class AbstractLpexAction<T> implements Comparable<T> {

    public static final String ACTION_DELIMITER = " "; //$NON-NLS-1$

    private String actionId;

    public AbstractLpexAction(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }

    public abstract int compareTo(T action);
}
