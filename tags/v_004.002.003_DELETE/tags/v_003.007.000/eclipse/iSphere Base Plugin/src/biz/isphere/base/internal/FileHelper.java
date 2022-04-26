/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.io.File;

import org.eclipse.swt.SWT;

public final class FileHelper {

    private static String platform = SWT.getPlatform();

    public static String getFileExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static String getFileName(String fileName) {
        return getFileName(new File(fileName));
    }

    public static String getFileName(File file) {
        return file.getName();
    }

    public static String getBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    public static String getBaseName(File file) {
        return getBaseName(file.getName());
    }

    public static boolean ensureDirectory(String path) {
        File directoryPath = new File(path).getAbsoluteFile();
        if (!path.endsWith(File.separator)) {
            // Assuming, that we got a file
            directoryPath = new File(path).getAbsoluteFile().getParentFile();
        }
        return ensureDirectory(directoryPath);
    }

    private static boolean ensureDirectory(File directoryPath) {

        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
            if (!directoryPath.exists()) {
                return false;
            }
        }

        return true;
    }

    public static String getDefaultRootDirectory() {

        if (platform.equals("win32") || platform.equals("wpf")) {
            return "c:\\"; //$NON-NLS-1$
        } else {
            return "/"; //$NON-NLS-1$
        }

    }

    public static String getAllFilesFilter() {

        if (isWin32Notation()) {
            return "*.*"; //$NON-NLS-1$
        } else {
            return "*"; //$NON-NLS-1$
        }

    }

    public static String getAllFilesText() {
        return "All Files (*.*)"; //$NON-NLS-1$
    }

    private static boolean isWin32Notation() {
        return platform.equals("win32") || platform.equals("wpf");
    }
}
