/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import biz.isphere.journalexplorer.base.interfaces.IJoesdParserDelegate;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DecDouble;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DecReal;

import com.ibm.as400.access.AS400Date;
import com.ibm.as400.access.AS400Time;
import com.ibm.as400.access.AS400Timestamp;
import com.ibm.as400.access.DateFieldDescription;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.HexFieldDescription;
import com.ibm.as400.access.TimeFieldDescription;
import com.ibm.as400.access.TimestampFieldDescription;

public class JoesdParserDelegate implements IJoesdParserDelegate {

    private static final String IMPLIED = "*IMPLIED";

    public FieldDescription getDateFieldDescription(String name, String format, String separator) {

        int dateFormat = AS400Date.toFormat(format);

        DateFieldDescription dateField;
        if (IMPLIED.equals(separator)) {
            dateField = new DateFieldDescription(new AS400Date(dateFormat), name);
        } else {
            char dateSeparator = separator.toCharArray()[0];
            dateField = new DateFieldDescription(new AS400Date(dateFormat, dateSeparator), name);
        }

        return dateField;
    }

    public FieldDescription getTimeFieldDescription(String name, String format, String separator) {

        int timeFormat = AS400Time.toFormat(format);

        FieldDescription timeField;
        if (IMPLIED.equals(separator)) {
            timeField = new TimeFieldDescription(new AS400Time(timeFormat), name);
        } else {
            char timeSeparator = separator.toCharArray()[0];
            timeField = new TimeFieldDescription(new AS400Time(timeFormat, timeSeparator), name);
        }

        return timeField;
    }

    public FieldDescription getTimestampFieldDescription(String name) {
        return new TimestampFieldDescription(new AS400Timestamp(), name);
    }

    public FieldDescription getDecRealFieldDescription(String name) {
        return new HexFieldDescription(new AS400DecReal(), name);
    }

    public FieldDescription getDecDoubleFieldDescription(String name) {
        return new HexFieldDescription(new AS400DecDouble(), name);
    }
}
