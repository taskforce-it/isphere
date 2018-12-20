/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Hours extends Number implements Comparable<Hours>{

    private static final long serialVersionUID = -6531520516090932903L;

    private Integer hours;
    
    public Hours(Integer days) {
        this.hours = days;
    }
    
    public Hours(int days) {
        this.hours = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return hours.doubleValue();
    }

    @Override
    public float floatValue() {
        return hours.floatValue();
    }

    @Override
    public int intValue() {
        return hours.intValue();
    }

    @Override
    public long longValue() {
        return hours.longValue();
    }

    public int compareTo(Hours anotherDays) {
        return hours.compareTo(anotherDays.hours);
    }
}
