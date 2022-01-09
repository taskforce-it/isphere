/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import biz.isphere.core.Messages;

public class DialogActionTypes {

    public static final int CREATE = 1;
    public static final int CHANGE = 2;
    public static final int COPY = 3;
    public static final int DELETE = 4;
    public static final int DISPLAY = 5;

    public static String getText(int actionType) {
        switch (actionType) {
        case CREATE: {
            return Messages.CREATEX;
        }
        case CHANGE: {
            return Messages.CHANGEX;
        }
        case COPY: {
            return Messages.COPYX;
        }
        case DELETE: {
            return Messages.DELETEX;
        }
        case DISPLAY: {
            return Messages.DISPLAYX;
        }
        }
        return "";
    }
    
    public static int getSubEditorActionType(String mode) {
        int actionType;
        if (IEditor.EDIT.equals(mode)) {
            actionType = DialogActionTypes.CHANGE;
        } else {
            actionType = DialogActionTypes.DISPLAY;
        }
        return actionType;
    }

}
