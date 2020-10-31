/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.internal;

public final class FileHelper {

    public static boolean hasFileExtension(String fileName) {

        String extension = getFileExtension(fileName);
        if (extension == null) {
            return false;
        }

        return true;
    }

    public static String getBareFileName(String fileName) {
        
        String extension = getFileExtension(fileName);
        if (extension == null) {
            return fileName;
        }
        
        return fileName.substring(0, fileName.length() - extension.length() - 1);
    }

    public static String replaceFileExtension(String fileName, String extension) {
        return getBareFileName(fileName) + "." + extension;
    }

    public static String getFileExtension(String fileName) {
        String extension = null;
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        if (extension != null && extension.length() == 0) {
            return null;
        }
        return extension;
    }

}
