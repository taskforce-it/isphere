/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.lpex.menu.model;

public class UserAction extends AbstractLpexAction<UserAction> implements Comparable<UserAction> {

    private String className;

    public UserAction(String actionId, String className) {
        super(actionId);
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return getActionId() + ACTION_DELIMITER + className;
    }

    public int compareTo(UserAction action) {

        if (action == null || action.getActionId() == null) {
            return 1;
        }

        return getActionId().compareTo(action.getActionId());
    }
}
