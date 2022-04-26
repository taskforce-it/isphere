/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

public class HandleBindingService {

    private static ArrayList<ITN5250JPart> tn5250jParts = new ArrayList<ITN5250JPart>();

    public static void addTN5250JPart(ITN5250JPart tn5250jPart) {
        tn5250jParts.add(tn5250jPart);
    }

    public static void removeTN5250JPart(ITN5250JPart tn5250jPart) {
        tn5250jParts.remove(tn5250jPart);
        if (tn5250jParts.size() == 0) {
            setBindingService(true);
        }
    }

    public static void setBindingService(boolean state) {

        BindingService bindingService = (BindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
        bindingService.setKeyFilterEnabled(state);

        for (int idx = 0; idx < tn5250jParts.size(); idx++) {
            tn5250jParts.get(idx).setBindingService(!state);
        }

    }

}
