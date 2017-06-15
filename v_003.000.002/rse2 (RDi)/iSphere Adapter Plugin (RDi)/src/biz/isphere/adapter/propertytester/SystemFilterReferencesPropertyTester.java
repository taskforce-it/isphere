/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.rse.core.filters.SystemFilterReference;

public class SystemFilterReferencesPropertyTester extends PropertyTester {

    public static final String PROPERTY_NAMESPACE = "biz.isphere.adapter.propertytester.systemfilterreference";

    public static final String PROPERTY_SUBSYSTEM = "subsystem";

    public static final String PROPERTY_TYPE = "type";

    public boolean test(Object aReceiver, String aProperty, Object[] anArgs, Object anExpectedValue) {

        if (!(aReceiver instanceof SystemFilterReference)) {
            return false;
        }

        SystemFilterReference filter = (SystemFilterReference)aReceiver;

        if (anExpectedValue instanceof String) {
            String expectedValue = (String)anExpectedValue;
            String actualValue = null;
            if (PROPERTY_SUBSYSTEM.equals(aProperty)) {
                actualValue = filter.getSubSystem().getClass().getName();
            }
            if (PROPERTY_TYPE.equals(aProperty)) {
                actualValue = filter.getReferencedFilter().getType();
            }

            return expectedValue.equalsIgnoreCase(actualValue);
        }

        return false;
    }

}
