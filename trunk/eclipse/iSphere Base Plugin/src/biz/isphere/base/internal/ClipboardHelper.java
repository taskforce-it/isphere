/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.PlatformUI;

public final class ClipboardHelper {

    private static Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());

    public static void setText(String text) {

        TextTransfer textTransfer = TextTransfer.getInstance();
        clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
    }
}
