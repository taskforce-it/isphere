/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;

public final class JDTCoreUtils {

    public static String getJDTCoreKey(String key) {
        return getKey(JavaCore.PLUGIN_ID, key);
    }

    private static String getKey(String plugin, String key) {
        String pref = Platform.getPreferencesService().getString(plugin, key, "", null);
        return pref;
    }

}
