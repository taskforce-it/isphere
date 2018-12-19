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
    
    private Integer years;
    
    public Years(Integer days) {
        this.years = days;
    }
    
    public Years(int days) {
        this.years = new Integer(days);
    }

    @Override
    public double doubleValue() {
        return years.doubleValue();
    }

    @Override
    public float floatValue() {
        return years.floatValue();
    }

    @Override
    public int intValue() {
        return years.intValue();
    }

    @Override
    public long longValue() {
        return years.longValue();
    }

    public int compareTo(Years anotherDays) {
        return years.compareTo(anotherDays.years);
    }
}
