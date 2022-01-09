/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class JobLogExplorerEditorJobInput implements IEditorInput {

    private String connectionName;
    private String jobName;
    private String userName;
    private String jobNumber;
    
    private String qualifiedJobName;

    public JobLogExplorerEditorJobInput(String connectionName, String jobName, String userName, String jobNumber) {

        this.connectionName = connectionName;
        this.jobName = jobName;
        this.userName = userName;
        this.jobNumber = jobNumber;
        
        StringBuilder buffer = new StringBuilder();

        buffer.append(connectionName);
        buffer.append(":"); //$NON-NLS-1$
        buffer.append(jobNumber);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(userName);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(jobName);

        qualifiedJobName = buffer.toString();
   }

    public String getConnectionName() {
        return connectionName;
    }

    public String getJobName() {
        return jobName;
    }

    public String getUserName() {
        return userName;
    }

    public String getJobNumber() {
        return jobNumber;
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

        if (qualifiedJobName == null) {
            return ""; //$NON-NLS-1$
        }

        return qualifiedJobName;
    }

    public String getToolTipText() {
        return qualifiedJobName;
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

        if (getName() == null) {
            return -1;
        }

        return getName().hashCode();

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        JobLogExplorerEditorJobInput other = (JobLogExplorerEditorJobInput)obj;

        String thisName = getName();
        String otherName = other.getName();
        if (thisName == null && otherName == null) return true;
        if (otherName == null) return false;
        return otherName.equals(thisName);
    }
}
