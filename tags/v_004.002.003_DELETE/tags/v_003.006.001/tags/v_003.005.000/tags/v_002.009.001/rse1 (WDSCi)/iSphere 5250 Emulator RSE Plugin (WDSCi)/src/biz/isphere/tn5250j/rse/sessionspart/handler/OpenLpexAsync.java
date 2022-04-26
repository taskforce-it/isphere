/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

import com.ibm.etools.iseries.core.api.ISeriesMember;

public class OpenLpexAsync extends AbstractAsyncHandler {

    private String library;
    private String sourceFile;
    private String member;
    private String mode;
    private String currentLibrary;
    private String libraryList;

    public OpenLpexAsync(Shell shell, SessionsInfo sessionsInfo, String library, String sourceFile, String member, String mode,
        String currentLibrary, String libraryList) {
        super(shell, sessionsInfo);

        this.library = library;
        this.sourceFile = sourceFile;
        this.member = member;
        this.mode = mode;
        this.currentLibrary = currentLibrary;
        this.libraryList = libraryList;
    }

    @Override
    protected String getCurrentLibrary() {
        return currentLibrary;
    }

    public void runInternally() {

        try {

            ISeriesMember iseriesMember = getConnection().getISeriesMember(getShell(), library, sourceFile, member);
            if (iseriesMember != null) {
                if (mode.equals("*OPEN")) {
                    iseriesMember.open();
                } else {
                    iseriesMember.browse();
                }
            }
        } catch (Throwable e) {
        }
    }
}
