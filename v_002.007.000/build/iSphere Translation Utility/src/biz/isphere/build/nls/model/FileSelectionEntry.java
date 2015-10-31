/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.model;

import biz.isphere.build.nls.utils.FileUtil;

/**
 * Class for file selection entries as defined in "nls.properties".
 * 
 * @author Thomas Raddatz
 */
public class FileSelectionEntry {

    private String fPath;
    private String fPattern;
    private boolean fIsSubdirectories;

    public FileSelectionEntry(String fileSelectionEntry) {
        String[] parts = fileSelectionEntry.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Illegal file selection entry '" + fileSelectionEntry
                + "'. The file selection entry must follow this syntax: path;patter;isSubdirectories");
        }

        fPath = FileUtil.fixRelativePath(parts[0].trim());
        fPattern = parts[1].trim();
        fIsSubdirectories = Boolean.parseBoolean(parts[2].trim());
    }

    public String getPath() {
        return fPath;
    }

    public String getPattern() {
        return fPattern;
    }

    public boolean isSubdirectories() {
        return fIsSubdirectories;
    }

    @Override
    public String toString() {
        return fPath + " (subDirs=" + fIsSubdirectories + ")";
    }

}
