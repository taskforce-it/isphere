/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.rse.compareeditor.handler.CompareSourceMembersHandler;
import biz.isphere.rse.internal.RSEMember;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

import com.ibm.etools.iseries.core.api.ISeriesMember;

public class OpenCompareAsync extends AbstractAsyncHandler {

    private String library;
    private String sourceFile;
    private String member;

    public OpenCompareAsync(Shell shell, SessionsInfo sessionsInfo, String library, String sourceFile, String member) {
        super(shell, sessionsInfo);

        this.library = library;
        this.sourceFile = sourceFile;
        this.member = member;
    }

    public void runInternally() {

        try {

            ISeriesMember _member = getConnection().getISeriesMember(getShell(), library, sourceFile, member);
            if (_member != null) {

                RSEMember[] selectedMembers = new RSEMember[1];
                selectedMembers[0] = new RSEMember(_member);
                if (selectedMembers.length > 0) {
                    CompareSourceMembersHandler handler = new CompareSourceMembersHandler();
                    handler.handleSourceCompare(selectedMembers);
                }
            }
        } catch (Throwable e) {
        }
    }
}
