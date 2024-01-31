/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.utils;

public final class LogUtil {

    public static void print(String text) {
        System.out.println("Help Builder: " + text);
    }

    public static void warn(String text) {
        System.out.println("Help Builder Warning: " + text);
    }

    public static void debug(String text) {
        // System.out.println("Help Builder Debug: " + text);
    }

    public static void error(String text) {
        System.out.println("*** Help Builder Error: " + text);
    }

}
