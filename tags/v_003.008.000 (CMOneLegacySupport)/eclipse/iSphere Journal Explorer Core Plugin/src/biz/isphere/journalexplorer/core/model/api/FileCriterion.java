/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.api;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.core.internals.QualifiedName;

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

    public FileCriterion(String file, String library, String member) {

        this.file = file;
        this.library = library;
        this.member = member;
    }

    public String getFile() {
        return file;
    }

    public String getLibrary() {
        return library;
    }

    public String getMember() {
        return member;
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
}
