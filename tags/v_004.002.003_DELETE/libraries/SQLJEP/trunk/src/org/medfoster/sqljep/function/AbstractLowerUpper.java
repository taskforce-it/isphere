/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.medfoster.sqljep.function;

import org.medfoster.sqljep.ParseException;
import org.medfoster.sqljep.exceptions.UnexpectedTypeException;

public abstract class AbstractLowerUpper extends PostfixCommand {

    protected abstract String performOperation(Comparable<?> param);

    @Override
    final public int getNumberOfParameters() {
        return 1;
    }

    public String lower_upper(Comparable<?> param) throws ParseException {

        if (param == null) {
            return null;
        }

        if (isSupportedType(param)) {
            return performOperation(param);
        }

        throw new UnexpectedTypeException(getFunctionName(), "expression", "String|Number");
    }

    protected boolean isSupportedType(Object object) {

        if (object instanceof String || object instanceof Number) {
            return true;
        }

        return false;
    }
}
