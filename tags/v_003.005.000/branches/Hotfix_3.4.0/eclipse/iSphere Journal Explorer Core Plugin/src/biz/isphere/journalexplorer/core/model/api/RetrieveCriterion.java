/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import com.ibm.as400.access.AS400DataType;

/**
 * Class, representing a selection criterion.
 * 
 * @author Thomas Raddatz
 */
public class RetrieveCriterion {

    RetrieveKey key;

    AS400DataType dataType;

    Object value;

    public RetrieveCriterion(RetrieveKey aKey, AS400DataType aDataType, Object aValue) {
        key = aKey;
        dataType = aDataType;
        value = aValue;
    }

    public RetrieveKey getKey() {
        return key;
    }

    public AS400DataType getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
