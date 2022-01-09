/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.core.spooledfiles.SpooledFile;

public class WorkWithSpooledFilesContentProvider implements IStructuredContentProvider {

    private SpooledFile[] spooledFiles;

    public Object[] getElements(Object inputElement) {
        return spooledFiles;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.spooledFiles = (SpooledFile[])newInput;
    }

}
