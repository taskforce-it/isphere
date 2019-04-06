/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.utils;

import java.io.File;

public final class FileUtil {

    public static String fixAbsolutePath(String path) {
        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    public static String fixRelativeFile(String path) {
        return ensureRelativePathPrefix(path);
    }

    public static String fixRelativePath(String path) {
        String fixedPath = ensureRelativePathPrefix(path);
        if (!fixedPath.endsWith(File.separator)) {
            fixedPath = fixedPath + File.separator;
        }
        return fixedPath;
    }

    private static String ensureRelativePathPrefix(String fixedPath) {
        if (!fixedPath.startsWith(".")) {
            fixedPath = "." + File.separator + fixedPath;
        } else if (fixedPath.startsWith(File.separator)) {
            fixedPath = "." + fixedPath;
        }
        return fixedPath;
    }

}
