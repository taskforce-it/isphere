/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import org.eclipse.swt.widgets.Shell;

public interface ICLPrompter {

    /*
     * Return values of showDialog()
     */
    public static final int OK = 0;
    public static final int CANCEL = 1;
    public static final int ERROR = 2;
    public static final int PTF_REQUIRED = 3;

    /*
     * Prompt modes for setMode()
     */
    public static final int EDIT_MODE = 0;
    public static final int EXECUTE_MODE = 1;
    public static final int NON_EXECUTE_MODE = 2;

    public void setCommandString(String commandString);

    public String getCommandString();

    public void setMode(int mode);

    public void setConnection(String connectionName);

    public void setParent(Shell shell);

    public int showDialog();

    public String testSyntax();
}
