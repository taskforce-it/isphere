/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import java.util.ArrayList;
import java.util.List;

public class RSEFilterPool {

    private RSEProfile profile;
    private String name;
    private boolean _default;
    private Object origin;
    private List<RSEFilter> filters;

    public RSEFilterPool() {
        this(null, null, false, null);
    }

    public RSEFilterPool(RSEProfile profile, String name, boolean _default, Object origin) {
        this.profile = profile;
        this.name = name;
        this._default = _default;
        this.origin = origin;
        filters = new ArrayList<RSEFilter>();
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

    public RSEFilter[] getFilters() {
        return filters.toArray(new RSEFilter[filters.size()]);
    }

    public RSEFilter[] getFilters(String type) {

        List<RSEFilter> filters = new ArrayList<RSEFilter>();

        for (RSEFilter filter : this.filters) {
            if (type == null || filter.getType().equals(type)) {
                filters.add(filter);
            }
        }

        return filters.toArray(new RSEFilter[filters.size()]);
    }

    public String[] getFilterNames() {
        return getFilterNames(null);
    }

    public String[] getFilterNames(String type) {

        List<String> filterNames = new ArrayList<String>();

        RSEFilter[] filters = getFilters(type);
        for (RSEFilter filter : filters) {
            filterNames.add(filter.getName());
        }

        return filterNames.toArray(new String[filterNames.size()]);
    }

    public void addFilter(RSEFilter filter) {
        filters.add(filter);
    }

}
