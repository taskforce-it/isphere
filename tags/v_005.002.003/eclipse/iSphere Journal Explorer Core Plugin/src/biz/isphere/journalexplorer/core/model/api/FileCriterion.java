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
import biz.isphere.journalexplorer.core.internals.QualifiedName;

import com.ibm.as400.access.AS400DataType;
import com.ibm.as400.access.AS400Text;

public class FileCriterion {

    public static String FILE_ALLFILE = "*ALLFILE";
    public static String FILE_ALL = "*ALL";

    public static String LIBRARY_LIBL = "*LIBL";
    public static String LIBRARY_CURLIB = "*CURLIB";

    public static String MEMBER_FIRST = "*FIRST";
    public static String MEMBER_ALL = "*ALL";

    private String file;
    private String library;
    private String member;

    private AS400DataType[] type;
    private Object[] value;

    private FileCriterion(String file, String library, String member) {

        this.file = file;
        this.library = library;
        this.member = member;

        this.type = new AS400DataType[] { new AS400Text(10), new AS400Text(10), new AS400Text(10) };
        this.value = new Object[] { this.file, this.library, this.member };
    }

    public List<AS400DataType> getType() {
        return Arrays.asList(type);
    }

    public List<Object> getValue() {
        return Arrays.asList(value);
    }

    public String getData() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(padRight(file));
        buffer.append(padRight(library));
        buffer.append(padRight(member));

        return buffer.toString();
    }

    public String getQualifiedName() {
        return QualifiedName.getMemberName(library, file, member);
    }

    private String padRight(String value) {
        return StringHelper.getFixLength(value, 10);
    }

    public static FileCriterion newFile(String object, String library, String member) {
        if (StringHelper.isNullOrEmpty(member)) {
            return new FileCriterion(object, library, MEMBER_ALL);
        } else {
            return new FileCriterion(object, library, member);
        }
    }
}
