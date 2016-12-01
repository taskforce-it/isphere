/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.editor;

public class DroppedLocalFile {

    private String pathName;

    public DroppedLocalFile(String dropString) {

        int c = 1;
        int s = 0;
        int e = dropString.indexOf(":"); //$NON-NLS-1$
        while (e > s && c <= 2) {
            dropString.substring(s, e);
            switch (c) {
            case 1:
                // profile and connection names
                break;

            case 2:
                // subsystem
                break;

            default:
                break;
            }
            c++;
            s = e + 1;
            e = dropString.indexOf(":", s); //$NON-NLS-1$
        }

        if (s < dropString.length()) {
            pathName = dropString.substring(s);
        }
    }

    public String getPathName() {
        return pathName;
    }

    @Override
    public String toString() {
        return pathName;
    }
}
