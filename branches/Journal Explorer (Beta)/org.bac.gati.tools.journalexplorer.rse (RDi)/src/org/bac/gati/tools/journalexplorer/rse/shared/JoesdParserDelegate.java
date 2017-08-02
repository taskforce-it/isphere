/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.bac.gati.tools.journalexplorer.rse.shared;

import org.bac.gati.tools.journalexplorer.rse.base.interfaces.IJoesdParserDelegate;

import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.AS400Time;
import com.ibm.as400.access.AS400Timestamp;
import com.ibm.as400.access.DateFieldDescription;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.TimeFieldDescription;
import com.ibm.as400.access.TimestampFieldDescription;

public class JoesdParserDelegate implements IJoesdParserDelegate {

    public FieldDescription getDateFieldDescription(String name) {
        return new DateFieldDescription(new AS400Date(), name);
    }

    public FieldDescription getTimeFieldDescription(String name) {
        return new TimeFieldDescription(new AS400Time(), name);
    }

    public FieldDescription getTimestampFieldDescription(String name) {
        return new TimestampFieldDescription(new AS400Timestamp(), name);
    }

}
