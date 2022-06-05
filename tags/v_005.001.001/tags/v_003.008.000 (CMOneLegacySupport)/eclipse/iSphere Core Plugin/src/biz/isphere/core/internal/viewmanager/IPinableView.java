/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

/**
 * This interfaces defined the methods, a pinable view has to implement.
 * 
 * @author Thomas Raddatz
 */
package biz.isphere.core.internal.viewmanager;

import java.util.Map;

import org.eclipse.ui.IViewSite;

public interface IPinableView {

    public boolean isPinned();

    public void setPinned(boolean pinned);

    public String getContentId();

    public Map<String, String> getPinProperties();

    public IViewSite getViewSite();
}
