/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model;

import biz.isphere.core.json.JsonSerializable;

public class JobTraceSessionJson extends JobTraceSessionSQL implements JsonSerializable {

    private transient String fileName;

    public JobTraceSessionJson(String fileName) {
        super(null, null, null);
        this.fileName = fileName;
    }

    public String getQualifiedName() {
        return fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public int hashCode() {
        final int prime = 89;
        int result = super.hashCode();
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        JobTraceSessionJson other = (JobTraceSessionJson)obj;
        if (fileName == null) {
            if (other.fileName != null) return false;
        } else if (!fileName.equals(other.fileName)) return false;
        return true;
    }
}
