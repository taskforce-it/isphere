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

public final class HandleBindingService {

    /**
     * The instance of this Singleton class.
     */
    private static HandleBindingService instance;

    private static ArrayList<ITN5250JPart> tn5250jParts = new ArrayList<ITN5250JPart>();
    private boolean isKeyFilterEnabled;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private HandleBindingService() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static HandleBindingService getInstance() {
        if (instance == null) {
            instance = new HandleBindingService();
        }
        return instance;
    }

    public void addTN5250JPart(ITN5250JPart tn5250jPart) {
        tn5250jParts.add(tn5250jPart);
    }

    public void removeTN5250JPart(ITN5250JPart tn5250jPart) {
        tn5250jParts.remove(tn5250jPart);
        if (tn5250jParts.size() == 0) {
            setBindingService(true);
        }
    }

    public void enableEclipseKeyFilter() {
        setBindingService(true);
    }

    public void restoreKeyFilterEnablement() {
        setBindingService(isKeyFilterEnabled);
    }

    public void toggleKeyFilterEnablement() {
        isKeyFilterEnabled = !isKeyFilterEnabled;
        setBindingService(isKeyFilterEnabled);
    }

    public void setKeyFilterEnabled(boolean isKeyFilterEnabled) {
        this.isKeyFilterEnabled = isKeyFilterEnabled;
        setBindingService(isKeyFilterEnabled);
    }

    private void setBindingService(boolean isKeyFilterEnabled) {

        BindingService bindingService = (BindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
        bindingService.setKeyFilterEnabled(isKeyFilterEnabled);

        for (int idx = 0; idx < tn5250jParts.size(); idx++) {
            tn5250jParts.get(idx).setBindingService(!isKeyFilterEnabled);
        }

    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }

}
