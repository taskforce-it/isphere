/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.file.description;

import java.util.HashMap;
import java.util.Map;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;

/**
 * Class to retrieve and cache record formats.
 * 
 * @author Thomas Raddatz
 */
public class RecordFormatDescriptionsStore {

    AS400 system;
    Map<String, RecordFormatDescription> formats;

    public RecordFormatDescriptionsStore(AS400 system) {
        this.system = system;
        this.formats = new HashMap<String, RecordFormatDescription>();
    }

    public RecordFormatDescription get(String file, String library) {
        if (formats.containsKey(getKey(system, file, library))) {
            return formats.get(getKey(system, file, library));
        }
        RecordFormatDescription format = new RecordFormatDescription(system, file, library);
        formats.put(getKey(system, file, library), format);
        return format;
    }

    private String getKey(AS400 as400, String file, String library) {
        QSYSObjectPathName tPathName = new QSYSObjectPathName(library, file, "FILE");
        return tPathName.getPath();
    }

}
