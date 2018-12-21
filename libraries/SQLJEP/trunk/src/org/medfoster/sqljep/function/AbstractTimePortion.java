/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import java.text.ParseException;

import org.medfoster.sqljep.ParserUtils;

public abstract class AbstractTimePortion<M extends Comparable<?>> extends AbstractDateTimePortion<M> {

    public AbstractTimePortion(int calendarField) {
        super(calendarField);
    }

    protected Comparable<?> parseObject(Comparable<?> param) throws java.text.ParseException, org.medfoster.sqljep.ParseException {

        OracleTimeFormat format = new OracleTimeFormat(ParserUtils.getTimeFormat((String)param));
        param = format.parseObject((String)param);

        return param;
    }

    @Override
    protected boolean isSupportedType(Object object) {

        if (object instanceof java.sql.Time || object instanceof java.sql.Timestamp) {
            return true;
        }

        return false;
    }
}
