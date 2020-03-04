/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.namedtype;

public class RSENamedType {

    private int domain;
    private String label;
    private String types;
    private Object origin;

    public RSENamedType() {
        this(-1, null, null, null);
    }

    public RSENamedType(int domain, String label, String types, Object origin) {

        setDomain(domain);
        setLabel(label);
        setTypes(types);
        setOrigin(origin);
    }

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Object getOrigin() {
        return origin;
    }

    public void setOrigin(Object origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return Integer.toString(getDomain()) + ": " + getLabel();
    }
}
