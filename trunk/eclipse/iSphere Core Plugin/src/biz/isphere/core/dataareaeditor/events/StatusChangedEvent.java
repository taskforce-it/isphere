/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor.events;

import java.util.EventObject;

public class StatusChangedEvent extends EventObject {

    private static final long serialVersionUID = -3632244163034792956L;
    
    public int position;
    public int row;
    public int column;
    public boolean insertMode;
    public boolean dirty;
    public String message;
    
    public StatusChangedEvent(Object aSource, int aPosition, int aRow, int aColumn, boolean anInsertMode, boolean anIsDirty) {
        super(aSource);
        position = aPosition;
        row = aRow;
        column = aColumn;
        insertMode = anInsertMode;
        dirty = anIsDirty;
        message = "";
    }
}
