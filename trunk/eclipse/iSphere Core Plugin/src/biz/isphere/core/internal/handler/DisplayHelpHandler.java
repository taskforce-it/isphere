/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;

public class DisplayHelpHandler extends AbstractHandler implements IHandler {

    private static boolean isInitialized = false;

    public static final String ID = "biz.isphere.core.internal.handler.DisplayHelpHandler";

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        // Hack, to open the iSphere book on first call (RDP, RDi?).
        if (!isInitialized) {
            PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
            isInitialized = true;
        }

        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.base.help/html/introduction/introduction.html");

        return null;
    }
}
