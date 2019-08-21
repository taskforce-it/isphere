/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.file.description;

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
public class RecordFormatDescription {

    private QSYSObjectPathName file;
    private FieldDescription[] fieldDescriptions;

    public RecordFormatDescription(AS400 system, String file, String library) {
        this.file = new QSYSObjectPathName(library, file, "FILE");

        try {
            AS400FileRecordDescription recordDescription = new AS400FileRecordDescription(system, this.file.getPath());
            RecordFormat[] format = recordDescription.retrieveRecordFormat();
            fieldDescriptions = format[0].getFieldDescriptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FieldDescription[] getFieldDescriptions() {
        return fieldDescriptions;
    }

    public FieldDescription getFieldDescription(String fieldName) {
        
        if (fieldDescriptions == null || fieldDescriptions.length == 0) {
            return null;
        }
        
        for (FieldDescription fieldDescription : fieldDescriptions) {
            if (fieldDescription.getFieldName().equals(fieldName)){
                return fieldDescription;
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return file.getLibraryName() + "/" + file.getObjectName();
    }

}
