/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.utils;

public final class FileUtil {

    public static String trimDirectory(String directory) {

        if (directory.endsWith("/") || directory.endsWith("\\")) {
            if (directory.length() >= 2) {
                return directory.substring(0, directory.length() - 1);
            } else {
                return "";
            }
        }

        return directory;
    }

}
