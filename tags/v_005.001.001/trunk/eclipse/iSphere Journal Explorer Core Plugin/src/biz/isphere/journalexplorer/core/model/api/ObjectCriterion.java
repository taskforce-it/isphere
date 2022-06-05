/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import java.util.Arrays;
import java.util.List;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.internal.ISeries;
import biz.isphere.journalexplorer.core.internals.JournalExplorerHelper;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Text;

public class ObjectCriterion {

    public static String OBJECT_ALL = "*ALL";

    public static String OBJECT_TYPE_FILE = ISeries.FILE;
    public static String OBJECT_TYPE_DTAARA = ISeries.DTAARA;
    public static String OBJECT_TYPE_DTAQ = ISeries.DTAQ;
    public static String OBJECT_TYPE_LIB = ISeries.LIB;

    public static String LIBRARY_LIBL = "*LIBL";
    public static String LIBRARY_CURLIB = "*CURLIB";

    public static String MEMBER_FIRST = "*FIRST";
    public static String MEMBER_ALL = "*ALL";
    public static String MEMBER_NONE = "*NONE";

    private String object;
    private String library;
    private String objectType;
    private String member;

    private AS400DataType[] type;
    private Object[] value;

    private ObjectCriterion(String object, String library, String objectType, String member) {

        this.object = object;
        this.library = library;
        this.objectType = objectType;

        if (OBJECT_TYPE_FILE.equals(objectType)) {
            this.member = member;
        } else {
            this.member = "";
        }

        // if (OBJECT_TYPE_FILE.equals(objectType)) {
        this.type = new AS400DataType[] { new AS400Text(10), new AS400Text(10), new AS400Text(10), new AS400Text(10) };
        this.value = new Object[] { this.object, this.library, this.objectType, this.member };
        // } else {
        // this.type = new AS400DataType[] { new AS400Text(10), new
        // AS400Text(10), new AS400Text(10) };
        // this.value = new Object[] { this.object, this.library,
        // this.objectType };
        // }
    }

    public List<AS400DataType> getType() {
        return Arrays.asList(type);
    }

    public List<Object> getValue() {
        return Arrays.asList(value);
    }

    public String getData() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(padRight(object));
        buffer.append(padRight(library));
        buffer.append(padRight(objectType));

        if (OBJECT_TYPE_FILE.equals(objectType)) {
            buffer.append(padRight(member));
        } else {

        }

        return buffer.toString();
    }

    public String getQualifiedName() {
        if (JournalExplorerHelper.isFile(objectType)) {
            return QualifiedName.getMemberName(library, object, member);
        } else {
            return QualifiedName.getName(library, object);
        }
    }

    private String padRight(String value) {
        return StringHelper.getFixLength(value, 10);
    }

    public static ObjectCriterion newObject(String object, String library, String objectType, String member) {

        if (OBJECT_TYPE_FILE.equals(objectType)) {
            return ObjectCriterion.newFile(object, library, member);
        } else if (OBJECT_TYPE_DTAARA.equals(objectType)) {
            return ObjectCriterion.newDtaAra(object, library);
        } else if (OBJECT_TYPE_DTAQ.equals(objectType)) {
            return ObjectCriterion.newDtaQ(object, library);
        } else if (OBJECT_TYPE_LIB.equals(objectType)) {
            return ObjectCriterion.newLib(library);
        } else {
            throw new IllegalArgumentException("Ivalid object type: " + objectType);
        }
    }

    public static ObjectCriterion newFile(String object, String library, String member) {
        if (StringHelper.isNullOrEmpty(member)) {
            return new ObjectCriterion(object, library, OBJECT_TYPE_FILE, MEMBER_ALL);
        } else {
            return new ObjectCriterion(object, library, OBJECT_TYPE_FILE, member);
        }
    }

    public static ObjectCriterion newDtaAra(String object, String library) {
        return new ObjectCriterion(object, library, OBJECT_TYPE_DTAARA, null);
    }

    public static ObjectCriterion newDtaQ(String object, String library) {
        return new ObjectCriterion(object, library, OBJECT_TYPE_DTAQ, null);
    }

    public static ObjectCriterion newLib(String library) {
        return new ObjectCriterion(library, "QSYS", OBJECT_TYPE_LIB, null);
    }
}
