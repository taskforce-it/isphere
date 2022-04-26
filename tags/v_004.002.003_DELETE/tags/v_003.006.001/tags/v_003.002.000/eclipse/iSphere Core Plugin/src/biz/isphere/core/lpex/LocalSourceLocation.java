/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.lpex;

import org.eclipse.core.runtime.IPath;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

public class LocalSourceLocation {

    private static final int FILE_EXTENSION = 0;
    private static final int MEMBER = 1;
    private static final int FILE = 2;
    private static final int LIBRARY = 3;
    private static final int HOST = 4;
    private static final int DONE = 5;

    private String iProjectName;
    private String libraryName;
    private String fileName;
    private String memberName;

    public LocalSourceLocation(String lpexSourceName) {

        parseSourceName(lpexSourceName);
    }

    /*
     * Source name:
     * C:\workspaces\rdp_080\runtime-EclipseApplication-en\iSphere\QRPGLESRC
     * \DEMO1.RPGLE
     */
    private void parseSourceName(String lpexSourceName) {

        int step = FILE_EXTENSION;
        int offset = lpexSourceName.length() - 1;
        int end = lpexSourceName.length();

        while (offset >= 0 && step != DONE) {

            String str = lpexSourceName.substring(offset, offset + 1);

            switch (step) {
            case FILE_EXTENSION:
                if (".".equals(str)) {
                    String fileExtension = lpexSourceName.substring(offset + 1, end);
                    end = offset;
                    step = MEMBER;
                }
                break;

            case MEMBER:
                if (str.charAt(0) == IPath.SEPARATOR) {
                    memberName = lpexSourceName.substring(offset + 1, end);
                    end = offset;
                    step = FILE;
                }
                break;

            case FILE:
                if (str.charAt(0) == IPath.SEPARATOR) {
                    fileName = lpexSourceName.substring(offset + 1, end);
                    end = offset;
                    step = LIBRARY;
                }
                break;

            case LIBRARY:
                if (str.charAt(0) == IPath.SEPARATOR) {
                    iProjectName = lpexSourceName.substring(offset + 1, end);
                    end = offset;
                    step = HOST;
                }
                break;

            default:
                break;
            }

            offset--;
        }

        libraryName = getLibraryName(iProjectName);
    }

    private String getLibraryName(String projectName) {
        return IBMiHostContributionsHandler.getLibraryName(projectName);
    }

    public String getProjectName() {
        return iProjectName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMemberName() {
        return memberName;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("iProject"); //$NON-NLS-1$
        buffer.append(":"); //$NON-NLS-1$
        buffer.append(iProjectName);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(fileName);
        buffer.append("("); //$NON-NLS-1$
        buffer.append(memberName);
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
