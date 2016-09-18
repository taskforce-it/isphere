/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class HexOnlyVerifyListener implements VerifyListener {

    public HexOnlyVerifyListener() {
    }

    public void verifyText(VerifyEvent event) {
        switch (event.keyCode) {
        case SWT.BS: // Backspace
        case SWT.DEL: // Delete
        case SWT.HOME: // Home
        case SWT.END: // End
        case SWT.ARROW_LEFT: // Left arrow
        case SWT.ARROW_RIGHT: // Right arrow
            return;
        }

        if (event.keyCode != 0) {
            switch (event.character) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                event.doit = true;
                event.text = event.text.toUpperCase();
                break;

            default:
                event.doit = false;
                break;
            }
        }
    }
}
