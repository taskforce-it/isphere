/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Years extends Number implements Comparable<Years>{
    
    private static final long serialVersionUID = 3033418957934552852L;
    
    private Integer days;
    
    public Years(Integer days) {
        this.days = days;
    }
    
    public Years(int days) {
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

    public int compareTo(Years anotherDays) {
        return days.compareTo(anotherDays.days);
    }
}
