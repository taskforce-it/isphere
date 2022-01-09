/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.datatypes;

public class Days extends Number implements Comparable<Days> {

    private static final long serialVersionUID = 2942756787983219124L;

    private Integer days;

    public Days(Integer days) {
        this.days = days;
    }

    public Days(int days) {
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

    public int compareTo(Days anotherDays) {
        return days.compareTo(anotherDays.days);
    }
}
