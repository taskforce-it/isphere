/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model.listeners;

public interface MessageModifyEventsTypes {

    public static final int ALL = -1;

    public static final int TYPE = 1;
    public static final int SEVERITY = 2;
    public static final int ID = 3;
    public static final int SELECTED = 4;
    public static final int FROM_PROGRAM = 5;
    public static final int FROM_LIBRARY = 6;
    public static final int FROM_STMT = 7;
    public static final int TO_PROGRAM = 8;
    public static final int TO_LIBRARY = 9;
    public static final int TO_STMT = 10;

}
