/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.sf;

public interface SFFileListener {

    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";
    public static final String IGNORED = "IGNORED";

    public void executingFileCommand(String command, String filename, String info);

}
