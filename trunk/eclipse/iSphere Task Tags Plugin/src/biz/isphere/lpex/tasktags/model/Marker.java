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

import org.eclipse.ui.texteditor.MarkerUtilities;

public class Marker {

    private Map<String, Object> attrs;

    public Marker(LPEXTask task) {

        attrs = new HashMap<String, Object>();

        attrs.put("userEditable", false); //$NON-NLS-1$
        attrs.put("priority", new Integer(task.getPriority())); //$NON-NLS-1$

        MarkerUtilities.setLineNumber(attrs, task.getLine());
        MarkerUtilities.setMessage(attrs, task.getMessage());
        MarkerUtilities.setCharStart(attrs, task.getCharStart());
        MarkerUtilities.setCharEnd(attrs, task.getCharEnd());
    }

    public Map<String, Object> getAttributes() {
        return attrs;
    }
}
