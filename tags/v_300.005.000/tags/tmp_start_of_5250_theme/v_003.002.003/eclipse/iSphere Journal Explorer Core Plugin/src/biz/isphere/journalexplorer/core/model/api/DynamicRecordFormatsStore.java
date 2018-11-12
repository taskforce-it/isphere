/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import java.util.HashMap;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Class to retrieve and cache record formats.
 * 
 * @author Thomas Raddatz
 */
public class DynamicRecordFormatsStore {

    AS400 system;
    Map<String, DynamicRecordFormat> formats;

    public DynamicRecordFormatsStore(AS400 aSystem) {
        system = aSystem;
        formats = new HashMap<String, DynamicRecordFormat>();
    }

    public DynamicRecordFormat get(String anObject, String aLibrary) {
        if (formats.containsKey(getKey(system, anObject, aLibrary))) {
            return formats.get(getKey(system, anObject, aLibrary));
        }
        DynamicRecordFormat format = new DynamicRecordFormat(system, anObject, aLibrary);
        formats.put(getKey(system, anObject, aLibrary), format);
        return format;
    }

    private String getKey(AS400 as400, String anObject, String aLibrary) {
        QSYSObjectPathName tPathName = new QSYSObjectPathName(aLibrary, anObject, "FILE");
        return tPathName.getPath();
    }

}
