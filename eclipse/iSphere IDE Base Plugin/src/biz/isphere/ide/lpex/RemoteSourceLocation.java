/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.ide.lpex;

public class RemoteSourceLocation {

    private static final int HOST = 1;
    private static final int LIBRARY = 2;
    private static final int FILE = 3;
    private static final int MEMBER = 4;
    private static final int DONE = 5;

    private String hostName;
    private String libraryName;
    private String fileName;
    private String memberName;

    public RemoteSourceLocation(String lpexSourceName) {

        parseSourceName(lpexSourceName);
    }

    /*
     * Source name: GHENTW.GFD.DE:ISPHEREDVP/QRPGLESRC(DEMO2)
     */
    private void parseSourceName(String lpexSourceName) {

        int step = HOST;
        int offset = 0;
        StringBuilder buffer = new StringBuilder();

        /*
         * WDSCi does not include the host name in the result of
         * LpexView.query("sourceName")
         */
        if (lpexSourceName.indexOf(":") < 0) {
            step = LIBRARY;
        }

        while (offset < lpexSourceName.length() && step != DONE) {

            String str = lpexSourceName.substring(offset, offset + 1);

            switch (step) {
            case HOST:
                if (":".equals(str)) {
                    hostName = buffer.toString();
                    buffer.delete(0, buffer.length());
                    step = LIBRARY;
                } else {
                    buffer.append(str);
                }
                break;

            case LIBRARY:
                if ("/".equals(str)) {
                    libraryName = buffer.toString();
                    buffer.delete(0, buffer.length());
                    step = FILE;
                } else {
                    buffer.append(str);
                }
                break;

            case FILE:
                if ("(".equals(str)) {
                    fileName = buffer.toString();
                    buffer.delete(0, buffer.length());
                    step = MEMBER;
                } else {
                    buffer.append(str);
                }
                break;

            case MEMBER:
                if (")".equals(str)) {
                    memberName = buffer.toString();
                    buffer.delete(0, buffer.length());
                    step = DONE;
                } else {
                    buffer.append(str);
                }
                break;

            default:
                break;
            }

            offset++;
        }
    }

    public String getHostName() {
        return hostName;
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

        buffer.append(hostName);
        buffer.append(":"); //$NON-NLS-1$
        buffer.append(libraryName);
        buffer.append("/"); //$NON-NLS-1$
        buffer.append(fileName);
        buffer.append("("); //$NON-NLS-1$
        buffer.append(memberName);
        buffer.append(")"); //$NON-NLS-1$

        return buffer.toString();
    }
}
