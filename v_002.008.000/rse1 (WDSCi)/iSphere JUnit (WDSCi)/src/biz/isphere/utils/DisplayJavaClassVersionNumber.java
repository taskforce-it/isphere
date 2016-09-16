/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class DisplayJavaClassVersionNumber {

    public static void main(String[] args) {
        DisplayJavaClassVersionNumber main = new DisplayJavaClassVersionNumber();
        main.run(args);
    }

    private void run(String[] args) {
        try {
            String filename = args[0];
            DataInputStream in = new DataInputStream(new FileInputStream(filename));
            int magic = in.readInt();
            if (magic != 0xcafebabe) {
                log(filename + " is not a valid class!");
            }
            int minor = in.readUnsignedShort();
            int major = in.readUnsignedShort();
            log(filename + ": " + major + " . " + minor);
            log(filename + ": Java " + getVersion(major, minor));
            in.close();
        } catch (IOException e) {
            log("Exception: " + e.getMessage(), e);
        }
    }

    private String getVersion(int major, int minor) {
        switch (major) {
        case 45:
            if (minor == 3) {
                return "1.1";
            } else {
                return "*UNKNOWN";
            }
        case 46:
            return "1.2";
        case 47:
            return "1.3";
        case 48:
            return "1.4";
        case 49:
            return "1.5";
        case 50:
            return "1.6";
        case 51:
            return "1.7";
        case 52:
            return "1.8";
        default:
            return "*UNKNOWN";
        }
    }

    private void log(String string) {
        log(string, null);
    }

    private void log(String string, IOException e) {
        System.out.println(string);
        if (e != null) {
            e.printStackTrace();
        }
    }
}