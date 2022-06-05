/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public final class KeyHelper {

    private KeyHelper() {
    }

    public static boolean isCtrlKey(Event event) {
        return isCtrlKey(event.stateMask);
    }

    public static boolean isCtrlKey(int stateMask) {
        return ((stateMask & SWT.CTRL) == SWT.CTRL);
    }

    public static boolean isShiftKey(Event event) {
        return isShiftKey(event.stateMask);
    }

    public static boolean isShiftKey(int stateMask) {
        return ((stateMask & SWT.SHIFT) == SWT.SHIFT);
    }
}
