/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;

public class RSEDomain implements Comparable<RSEDomain> {

    public final static int OBJECT = 0;
    public final static int MEMBER = 1;

    private RSEProfile profile;
    private int domainType;
    private String name;
    private List<RSEUserAction> userActions;

    public RSEDomain(RSEProfile profile) {
        this(profile, -1, null);
    }

    public RSEDomain(RSEProfile profile, int domainType, String name) {
        this.profile = profile;
        this.domainType = domainType;
        this.name = name;
        this.userActions = new ArrayList<RSEUserAction>();
    }

    public RSEProfile getProfile() {
        return profile;
    }

    public void setProfile(RSEProfile profile) {
        this.profile = profile;
    }

    public int getDomainType() {
        return domainType;
    }

    public void setDomainType(int domain) {
        this.domainType = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Integer.toString(getDomainType()) + ":" + getName();
    }

    public int compareTo(RSEDomain o) {

        if (o == null) {
            return 1;
        } else {
            if (getDomainType() < o.getDomainType()) {
                return -1;
            } else if (getDomainType() > o.getDomainType()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
