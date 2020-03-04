/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.sessionseditor;

import biz.isphere.tn5250j.core.tn5250jeditor.TN5250JEditor;

public abstract class CoreSessionsEditor extends TN5250JEditor {

    @Override
    public boolean isMultiSession() {
        return true;
    }

}
