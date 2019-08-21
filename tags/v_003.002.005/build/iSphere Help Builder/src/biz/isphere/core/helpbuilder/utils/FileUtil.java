/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.utils;

import java.io.File;
import java.io.IOException;

import biz.isphere.core.helpbuilder.configuration.Configuration;

public final class FileUtil {

    public static String resolvePath(String pathname) throws IOException {
        return new File(pathname).getCanonicalPath().replaceAll(Configuration.REGEX_BACK_SLASH, Configuration.FORWARD_SLASH);
    }

    public static String resolvePath(String parent, String child) throws IOException {
        return new File(parent, child).getCanonicalPath().replaceAll(Configuration.REGEX_BACK_SLASH, Configuration.FORWARD_SLASH);
    }

    public static String resolvePath(File parent, String child) throws IOException {
        return new File(parent, child).getCanonicalPath().replaceAll(Configuration.REGEX_BACK_SLASH, Configuration.FORWARD_SLASH);
    }

}
