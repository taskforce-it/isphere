/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.swt.events.TypedEvent;

import biz.isphere.core.swt.widgets.internal.HexEditorInternal;

public class CaretEvent extends TypedEvent {

    private static final long serialVersionUID = -4351282007326910073L;

    public int caretOffset;

    public CaretEvent(HexEditorInternal e) {
        super(e);
        caretOffset = e.getCaretOffset();
    }

}
