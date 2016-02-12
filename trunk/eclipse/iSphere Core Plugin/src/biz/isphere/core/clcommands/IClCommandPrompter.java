/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.clcommands;

import org.eclipse.swt.widgets.Shell;

public interface IClCommandPrompter {

    public void setCommandString(String commandString);

    public void setMode(int mode);

    public void setConnection(String connectionName);

    public void setParent(Shell shell);

}
