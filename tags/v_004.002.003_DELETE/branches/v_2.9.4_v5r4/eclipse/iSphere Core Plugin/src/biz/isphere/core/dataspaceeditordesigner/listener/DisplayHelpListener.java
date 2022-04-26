/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.PlatformUI;

public class DisplayHelpListener implements SelectionListener {

    public void widgetSelected(SelectionEvent event) {
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.core.help/html/dataspaces/designer/dataspaceeditordesigner.html");
    }

    public void widgetDefaultSelected(SelectionEvent event) {
    }
}
