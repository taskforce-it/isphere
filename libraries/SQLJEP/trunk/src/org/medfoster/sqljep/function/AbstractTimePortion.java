/*******************************************************************************
 * Copyright (c) project_year-2018 project_team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

public abstract class AbstractTimePortion<M extends Comparable<?>> extends AbstractDateTimePortion<M> {


    public AbstractTimePortion(int calendarField) {
        super(calendarField);
    }

    @Override
    protected boolean isSupportedType(Object object) {

        if (object instanceof java.sql.Time || object instanceof java.sql.Timestamp) {
            return true;
        }

        return false;
    }
}
