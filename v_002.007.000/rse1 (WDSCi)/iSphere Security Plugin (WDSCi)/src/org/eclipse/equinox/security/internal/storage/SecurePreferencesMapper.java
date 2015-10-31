/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.eclipse.equinox.security.internal.storage;

import java.net.URL;
import java.util.Map;

import org.eclipse.equinox.security.storage.ISecurePreferences;


public class SecurePreferencesMapper {

    static private ISecurePreferences defaultPreferences = null;

    static public ISecurePreferences getDefault() {
        if (defaultPreferences == null) {
            defaultPreferences = open(null, null);
        }
        return defaultPreferences;
    }

    static public ISecurePreferences open(URL location, Map options) {
        return new SecurePreferences();
    }
}
