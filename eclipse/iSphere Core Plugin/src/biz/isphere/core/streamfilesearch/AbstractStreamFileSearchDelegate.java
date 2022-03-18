/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractStreamFileSearchDelegate {

    private Shell shell;
    private IProgressMonitor monitor;

    public AbstractStreamFileSearchDelegate(Shell shell) {
        this(shell, null);
    }

    public AbstractStreamFileSearchDelegate(Shell shell, IProgressMonitor monitor) {
        this.shell = shell;
        this.monitor = monitor;
    }

    private void addSearchElement(HashMap<String, SearchElement> searchElements, SearchElement aSearchElement) {
        String tKey = aSearchElement.getDirectory() + "/" + aSearchElement.getStreamFile(); //$NON-NLS-1$ //$NON-NLS-2$
        if (!searchElements.containsKey(tKey)) {
            searchElements.put(tKey, aSearchElement);
        }
    }

    public HashMap<String, SearchElement> createHashMap(ArrayList<SearchElement> filteredElements) {

        HashMap<String, SearchElement> map = new HashMap<String, SearchElement>();

        for (SearchElement searchElement : filteredElements) {
            addSearchElement(map, searchElement);
        }

        return map;
    }

}
