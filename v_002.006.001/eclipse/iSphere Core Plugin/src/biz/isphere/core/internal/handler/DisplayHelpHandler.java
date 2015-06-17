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

    public static final String ID = "biz.isphere.core.internal.handler.DisplayHelpHandler";

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {

        /*
         * When the help displayed for the first time after a restart of RDP,
         * the iSphere topic is displayed, but not linked to the menu to the
         * left. For now, there is no idea how to get around that. The problem
         * does not exist in WDSCi.
         */
        PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.base.help/html/introduction/introduction.html");

        return null;
    }
}
