/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import biz.isphere.journalexplorer.base.interfaces.IJoesdParserDelegate;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Date;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DateFormat;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DecDouble;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400DecReal;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400Time;
import biz.isphere.journalexplorer.rse.shared.as400fields.AS400TimeFormat;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.HexFieldDescription;

public final class JoesdParserDelegate implements IJoesdParserDelegate {

    public FieldDescription getDateFieldDescription(String name, String format, String separator) {

        int bufferLength;
        if ("*IMPLIED".equals(separator)) {
            bufferLength = AS400Date.getByteLength(AS400Date.toFormat(format), AS400DateFormat.valueOf(format).separator());
        } else {
            bufferLength = AS400Date.getByteLength(AS400Date.toFormat(format), toChar(separator));
        }

        return new CharacterFieldDescription(new AS400Text(bufferLength), name);
    }

    public FieldDescription getTimeFieldDescription(String name, String format, String separator) {

        int bufferLength;
        if ("*IMPLIED".equals(separator)) {
            bufferLength = AS400Time.getByteLength(AS400Time.toFormat(format), AS400TimeFormat.valueOf(format).separator());
        } else {
            bufferLength = AS400Time.getByteLength(AS400Time.toFormat(format), toChar(separator));
        }

        return new CharacterFieldDescription(new AS400Text(bufferLength), name);
    }

    public FieldDescription getTimestampFieldDescription(String name) {
        return new CharacterFieldDescription(new AS400Text(26), name);
    }

    public FieldDescription getDecRealFieldDescription(String name) {
        return new HexFieldDescription(new AS400DecReal(), name);
    }

    public FieldDescription getDecDoubleFieldDescription(String name) {
        return new HexFieldDescription(new AS400DecDouble(), name);
    }

    private Character toChar(String separator) {

        if (separator == null || separator.length() == 0) {
            return null;
        }

        if (separator.length() != 1) {
            throw new IllegalArgumentException("Invalid length 'separator': " + separator);
        }

        return separator.toCharArray()[0];
    }
}
