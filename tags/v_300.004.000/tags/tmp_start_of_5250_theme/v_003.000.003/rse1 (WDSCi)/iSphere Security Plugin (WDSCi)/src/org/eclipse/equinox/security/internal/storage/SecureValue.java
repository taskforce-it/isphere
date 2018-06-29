/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.eclipse.equinox.security.internal.storage;

public class SecureValue {

    private Object value;
    private boolean isEncrypted;

    public SecureValue(Object value, boolean isEncrypted) {
        this.value = value;
        this.isEncrypted = isEncrypted;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public Object getValue() {
        return value;
    }

}
