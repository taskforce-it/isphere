/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector;

public interface ISelectRemoteQSYSObjectDialog {

    public int open();

    public String getConnectionName();

    public String getLibraryName();

    public String getObjectName();

    public String getObjectType();

    public ISelectedObject getObject();

    public void setLibraryName(String libraryName);

    public void setObjectName(String objectName);
}
