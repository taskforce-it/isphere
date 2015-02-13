/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.events;

import java.util.EventObject;

public class StatusChangedEvent extends EventObject {

    private static final long serialVersionUID = -3632244163034792956L;

    public int topIndex;
    public int position;
    public int row;
    public int column;
    public boolean insertMode;
    public boolean dirty;
    public String message;

    public StatusChangedEvent(Object aSource, int aTopIndex, int aPosition, int aRow, int aColumn, boolean anInsertMode, boolean anIsDirty) {
        super(aSource);
        topIndex = aTopIndex;
        position = aPosition;
        row = aRow;
        column = aColumn;
        insertMode = anInsertMode;
        dirty = anIsDirty;
        message = null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + column;
        result = prime * result + (dirty ? 1231 : 1237);
        result = prime * result + (insertMode ? 1231 : 1237);
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + position;
        result = prime * result + row;
        result = prime * result + topIndex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        StatusChangedEvent other = (StatusChangedEvent)obj;
        if (column != other.column) return false;
        if (dirty != other.dirty) return false;
        if (insertMode != other.insertMode) return false;
        if (message == null) {
            if (other.message != null) return false;
        } else if (!message.equals(other.message)) return false;
        if (position != other.position) return false;
        if (row != other.row) return false;
        if (topIndex != other.topIndex) return false;
        return true;
    }

}
