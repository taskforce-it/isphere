/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.sqleditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;

public class DisplaySQLHelpListener extends MouseAdapter {

    @Override
    public void mouseUp(MouseEvent event) {
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.base.help/html/sql/sql_reference.html");
    }

    public static Label createLabel(Composite parent) {

        Label helpItem = new Label(parent, SWT.NONE);
        helpItem.setLayoutData(new GridData());
        helpItem.setImage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SYSTEM_HELP));
        helpItem.addMouseListener(new DisplaySQLHelpListener());

        return helpItem;
    }
}
