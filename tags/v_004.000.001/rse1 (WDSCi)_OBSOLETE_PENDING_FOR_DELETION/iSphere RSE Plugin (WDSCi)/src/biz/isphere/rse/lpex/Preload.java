/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.lpex;

import biz.isphere.ide.lpex.menu.MenuExtension;
import biz.isphere.rse.ISphereRSEPlugin;

import com.ibm.lpex.alef.LpexPreload;

/**
 * This class is a dummy that is installed in plugin.xml. All it has to do is to
 * activate the plug-in class when it is loaded.
 * 
 * @author Thomas Raddatz
 */
public class Preload implements LpexPreload {

    public Preload() {
        return;
    }

    public void preload() {

        MenuExtension menuExtension = new MenuExtension();
        menuExtension.initializeLpexEditor(ISphereRSEPlugin.getDefault());

        return;
    }
}
