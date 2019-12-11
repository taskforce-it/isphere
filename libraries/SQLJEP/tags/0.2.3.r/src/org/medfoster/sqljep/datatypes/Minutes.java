/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Minutes extends Number implements Comparable<Minutes> {

    private static final long serialVersionUID = 9090026444707000653L;

    private Integer minutes;

    public Minutes(Integer days) {
        this.minutes = days;
    }

    public Minutes(int days) {
        this.minutes = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return minutes.doubleValue();
    }

    @Override
    public float floatValue() {
        return minutes.floatValue();
    }

    @Override
    public int intValue() {
        return minutes.intValue();
    }

    @Override
    public long longValue() {
        return minutes.longValue();
    }

    public int compareTo(Minutes anotherDays) {
        return minutes.compareTo(anotherDays.minutes);
    }
}
