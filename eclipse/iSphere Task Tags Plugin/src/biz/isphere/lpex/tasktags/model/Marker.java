/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class Marker {

    private Map<String, Object> attrs;

    public Marker(LPEXTask task) {

        attrs = new HashMap<String, Object>();

        attrs.put(IMarker.USER_EDITABLE, false);
        attrs.put(IMarker.PRIORITY, new Integer(task.getPriority()));

        MarkerUtilities.setLineNumber(attrs, task.getLine());
        MarkerUtilities.setMessage(attrs, task.getMessage());
        MarkerUtilities.setCharStart(attrs, task.getCharStart());
        MarkerUtilities.setCharEnd(attrs, task.getCharEnd());
    }

    public int getLineNumber() {
        return (Integer)getAttributes().get(IMarker.LINE_NUMBER);
    }

    public String getMessage() {
        return (String)getAttributes().get(IMarker.MESSAGE);
    }

    public int getCharStart() {
        return (Integer)getAttributes().get(IMarker.CHAR_START);
    }

    public int getCharEnd() {
        return (Integer)getAttributes().get(IMarker.CHAR_END);
    }

    public boolean getUserEditable() {
        return (Boolean)getAttributes().get(IMarker.USER_EDITABLE);
    }

    public int getPriority() {
        return (Integer)getAttributes().get(IMarker.PRIORITY);
    }

    private Map<String, Object> getAttributes() {
        return attrs;
    }
}
