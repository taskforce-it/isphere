/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import org.medfoster.sqljep.ParserUtils;
import org.medfoster.sqljep.exceptions.UnknownDateFormatException;

public abstract class AbstractDatePortion<M extends Comparable<?>> extends AbstractDateTimePortion<M> {

    public AbstractDatePortion(int calendarField) {
        super(calendarField);
    }

    @Override
    protected Comparable<?> parseObject(Comparable<?> param) throws org.medfoster.sqljep.ParseException {

        try {

            OracleDateFormat format = new OracleDateFormat(ParserUtils.getDateFormat((String)param));
            param = format.parseObject((String)param);

        } catch (java.text.ParseException e) {
            throw new UnknownDateFormatException((String)param);
        }

        return param;
    }

    @Override
    protected boolean isSupportedType(Object object) {

        if (object instanceof java.sql.Time || object instanceof java.sql.Timestamp) {
            return false;
        }

        if (object instanceof java.sql.Date) {
            return true;
        }

        return false;
    }
}
