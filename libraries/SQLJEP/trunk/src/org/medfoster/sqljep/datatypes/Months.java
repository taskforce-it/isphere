/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Months extends Number implements Comparable<Months>{
    
    private static final long serialVersionUID = -2107158394104233397L;
    
    private Integer days;
    
    public Months(Integer days) {
        this.days = days;
    }
    
    public Months(int days) {
        this.days = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return days.doubleValue();
    }

    @Override
    public float floatValue() {
        return days.floatValue();
    }

    @Override
    public int intValue() {
        return days.intValue();
    }

    @Override
    public long longValue() {
        return days.longValue();
    }

    public int compareTo(Months anotherDays) {
        return days.compareTo(anotherDays.days);
    }
}
