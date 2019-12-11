/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Microseconds extends Number implements Comparable<Microseconds> {

    private static final long serialVersionUID = -5047847131341767688L;

    private Integer milliseconds;

    public Microseconds(Integer days) {
        this.milliseconds = days;
    }

    public Microseconds(int days) {
        this.milliseconds = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return milliseconds.doubleValue();
    }

    @Override
    public float floatValue() {
        return milliseconds.floatValue();
    }

    @Override
    public int intValue() {
        return milliseconds.intValue();
    }

    @Override
    public long longValue() {
        return milliseconds.longValue();
    }

    public int compareTo(Microseconds anotherDays) {
        return milliseconds.compareTo(anotherDays.milliseconds);
    }
}
