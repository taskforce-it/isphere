/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

import java.util.ArrayList;
import java.util.List;

public enum Columns {

    SELECTED ("selected", Index.SELECTED, 30), //$NON-NLS-1$
    DATE ("date", Index.DATE, 60), //$NON-NLS-1$
    TIME ("time", Index.TIME, 100), //$NON-NLS-1$
    ID ("id", Index.ID, 80), //$NON-NLS-1$
    TYPE ("type", Index.TYPE, 100), //$NON-NLS-1$
    SEVERITY ("severity", Index.SEVERITY, 60), //$NON-NLS-1$
    TEXT ("text", Index.TEXT, 200), //$NON-NLS-1$
    FROM_LIBRARY ("fromLibrary", Index.FROM_LIBRARY, 100), //$NON-NLS-1$
    FROM_PROGRAM ("fromProgram", Index.FROM_PROGRAM, 100), //$NON-NLS-1$
    FROM_STATEMENT ("fromStatement", Index.FROM_STATEMENT, 50), //$NON-NLS-1$
    TO_LIBRARY ("toLibrary", Index.TO_LIBRARY, 100), //$NON-NLS-1$
    TO_PROGRAM ("toProgram", Index.TO_PROGRAM, 100), //$NON-NLS-1$
    TO_STATEMENT ("toStatement", Index.TO_STATEMENT, 50), //$NON-NLS-1$
    FROM_MODULE ("fromModule", Index.FROM_MODULE, 100), //$NON-NLS-1$
    TO_MODULE ("toModule", Index.TO_MODULE, 100), //$NON-NLS-1$
    FROM_PROCEDURE ("fromProcedure", Index.FROM_PROCEDURE, 200), //$NON-NLS-1$
    TO_PROCEDURE ("toProcedure", Index.TO_PROCEDURE, 200); //$NON-NLS-1$

    public final String name;
    public final int index;
    public final int width;

    private Columns(String name, int columnNumber, int width) {
        this.name = name;
        this.index = columnNumber;
        this.width = width;
    }

    public static String[] names() {

        List<String> names = new ArrayList<String>();
        for (Columns column : Columns.values()) {
            names.add(column.name);
        }

        return names.toArray(new String[names.size()]);
    }

    public interface Index {
        public static int SELECTED = 0;
        public static int DATE = 1;
        public static int TIME = 2;
        public static int ID = 3;
        public static int TYPE = 4;
        public static int SEVERITY = 5;
        public static int TEXT = 6;
        public static int FROM_LIBRARY = 7;
        public static int FROM_PROGRAM = 8;
        public static int FROM_STATEMENT = 9;
        public static int TO_LIBRARY = 10;
        public static int TO_PROGRAM = 11;
        public static int TO_STATEMENT = 12;
        public static int FROM_MODULE = 13;
        public static int TO_MODULE = 14;
        public static int FROM_PROCEDURE = 15;
        public static int TO_PROCEDURE = 16;
    }

}
