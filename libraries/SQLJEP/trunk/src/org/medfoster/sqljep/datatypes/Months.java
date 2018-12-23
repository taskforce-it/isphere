/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Months extends Number implements Comparable<Months> {

    private static final long serialVersionUID = -2107158394104233397L;

    private Integer months;

    public Months(Integer days) {
        this.months = days;
    }

    public Months(int days) {
        this.months = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return months.doubleValue();
    }

    @Override
    public float floatValue() {
        return months.floatValue();
    }

    @Override
    public int intValue() {
        return months.intValue();
    }

    @Override
    public long longValue() {
        return months.longValue();
    }

    public int compareTo(Months anotherDays) {
        return months.compareTo(anotherDays.months);
    }
}
