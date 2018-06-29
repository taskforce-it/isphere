/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.preferencepages;

import java.net.URL;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PlatformUI;

public class LinkSelectionListener extends SelectionAdapter {
    @Override
    public void widgetSelected(SelectionEvent event) {
        try {
            // Open default external browser
            PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL("http://" + event.text));
        } catch (Exception e) {
            // ignore all errors
        }
    }
}
