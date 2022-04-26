/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector;

import com.ibm.as400.access.AS400;

public interface ISelectQSYSObjectDialog {

    public int open();

    public ISelectedObject getSelectedItem();

    public AS400 getSystem();

    public void setExpandLibraryListsEnabled(boolean enabled);

    public void setCurrentLibraryEnabled(boolean enabled);

    public void setLibraryListEnabled(boolean enabled);

    public void addLibrary(String libraryName);
}
