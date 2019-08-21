/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FileRecordDescription;
import com.ibm.as400.access.FieldDescription;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.RecordFormat;

/**
 * Class to represent the record format of a System physical file.
 * 
 * @author Thomas Raddatz
 */
public class DynamicRecordFormat {

    private QSYSObjectPathName file;

    private FieldDescription[] fieldDescriptions;

    public DynamicRecordFormat(AS400 aSystem, String aFile, String aLibrary) {
        file = new QSYSObjectPathName(aLibrary, aFile, "FILE");

        try {
            AS400FileRecordDescription recordDescription = new AS400FileRecordDescription(aSystem, file.getPath());
            RecordFormat[] format = recordDescription.retrieveRecordFormat();
            fieldDescriptions = format[0].getFieldDescriptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FieldDescription[] getFieldDescriptions() {
        return fieldDescriptions;
    }

    @Override
    public String toString() {
        return file.getLibraryName() + "/" + file.getObjectName();
    }

}
