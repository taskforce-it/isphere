/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor.tableviewer;

public interface JobLogExplorerTableColumns {

    public static int COLUMN_SELECTED = 0;
    public static int COLUMN_DATE = 1;
    public static int COLUMN_TIME = 2;
    public static int COLUMN_ID = 3;
    public static int COLUMN_TYPE = 4;
    public static int COLUMN_SEVERITY = 5;
    public static int COLUMN_TEXT = 6;
    public static int COLUMN_FROM_LIBRARY = 7;
    public static int COLUMN_FROM_PROGRAM = 8;
    public static int COLUMN_FROM_STATEMENT = 9;
    public static int COLUMN_TO_LIBRARY = 10;
    public static int COLUMN_TO_PROGRAM = 11;
    public static int COLUMN_TO_STATEMENT = 12;
    public static int COLUMN_FROM_MODULE = 13;
    public static int COLUMN_TO_MODULE = 14;
    public static int COLUMN_FROM_PROCEDURE = 15;
    public static int COLUMN_TO_PROCEDURE = 16;

    public static int WIDTH_SELECTED = 30;
    public static int WIDTH_DATE = 60;
    public static int WIDTH_TIME = 100;
    public static int WIDTH_ID = 80;
    public static int WIDTH_TYPE = 100;
    public static int WIDTH_SEVERITY = 60;
    public static int WIDTH_TEXT = 200;
    public static int WIDTH_FROM_LIBRARY = 100;
    public static int WIDTH_FROM_PROGRAM = WIDTH_FROM_LIBRARY;
    public static int WIDTH_FROM_STATEMENT = 50;
    public static int WIDTH_TO_LIBRARY = WIDTH_FROM_LIBRARY;
    public static int WIDTH_TO_PROGRAM = WIDTH_FROM_PROGRAM;
    public static int WIDTH_TO_STATEMENT = WIDTH_FROM_STATEMENT;
    public static int WIDTH_FROM_MODULE = WIDTH_FROM_LIBRARY;
    public static int WIDTH_TO_MODULE = WIDTH_FROM_MODULE;
    public static int WIDTH_FROM_PROCEDURE = 200;
    public static int WIDTH_TO_PROCEDURE = WIDTH_FROM_PROCEDURE;

}
