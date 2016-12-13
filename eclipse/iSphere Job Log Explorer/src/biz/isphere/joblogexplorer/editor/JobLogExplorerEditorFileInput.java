/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class JobLogExplorerEditorFileInput implements IEditorInput {

    private String pathName;
    private String originalFileName;

    private File file;

    public JobLogExplorerEditorFileInput(String pathName, String originalFileName) {

        this.pathName = pathName;
        this.originalFileName = originalFileName;

        if (pathName == null) {
            this.file = null;
        } else {
            this.file = new File(pathName);
        }
    }

    public String getPath() {
        return pathName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public boolean exists() {

        // if (file != null && file.exists() && file.isFile()) {
        // return true;
        // }

        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {

        if (file == null) {
            return ""; //$NON-NLS-1$
        }

        return file.getName();
    }

    public String getToolTipText() {
        return pathName;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public int hashCode() {

        if (getPath() == null) {
            return -1;
        }

        return getPath().hashCode();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobLogExplorerEditorFileInput other = (JobLogExplorerEditorFileInput)obj;

        String thisPath = getPath();
        String otherPath = other.getPath();
        if (thisPath == null && otherPath == null) return true;
        if (otherPath == null) return false;
        return otherPath.equals(thisPath);
    }
}
