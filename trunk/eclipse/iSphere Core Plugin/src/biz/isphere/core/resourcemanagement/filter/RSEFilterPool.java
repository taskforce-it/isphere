/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

public class RSEFilterPool {

    private RSEProfile profile;
    private String name;
    private boolean _default;
    private Object origin;

    public RSEFilterPool() {
        profile = null;
        name = null;
        _default = false;
        origin = null;
    }

    public RSEFilterPool(RSEProfile profile, String name, boolean _default, Object origin) {
        this.profile = profile;
        this.name = name;
        this._default = _default;
        this.origin = origin;
    }

    public RSEProfile getProfile() {
        return profile;
    }

    public void setProfile(RSEProfile profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return _default;
    }

    public void setDefault(boolean _default) {
        this._default = _default;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

}
