/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

public interface IEditor {

    public static final String EDIT = "*EDIT";

    // TODO: clean up, to use either BROWSE or DISPLAY
    @Deprecated
    public static final String BROWSE = "*BROWSE"; // same function as *DISPLAY

    public static final String DISPLAY = "*DISPLAY"; // same function as *BROWSE

    public void openEditor(String connectionName, String library, String file, String member, int statement, String mode);

}
