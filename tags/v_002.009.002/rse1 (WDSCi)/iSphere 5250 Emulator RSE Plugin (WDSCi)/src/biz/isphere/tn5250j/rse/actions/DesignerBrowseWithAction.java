/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.actions;

import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;

import com.ibm.etools.systems.core.ui.SystemMenuManager;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

public class DesignerBrowseWithAction extends DesignerOpenWithAction {

    public DesignerBrowseWithAction() {
        setContextMenuGroup("group.browsewith");
        setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_BROWSE_DESIGNER));
    }

    @Override
    public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
        setShell(shell);
        menu.add("group.browsewith", this);
    }

    @Override
    protected String getMode() {
        return "*BROWSE";
    }

}