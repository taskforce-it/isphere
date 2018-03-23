/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.compareeditor.CompareAction;
import biz.isphere.rse.compareeditor.RSECompareDialog;
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

                RSEMember rseLeftMember = new RSEMember(_member);
                RSECompareDialog dialog = new RSECompareDialog(getShell(), true, rseLeftMember);

                if (dialog.open() == Dialog.OK) {

                    boolean editable = dialog.isEditable();
                    boolean considerDate = dialog.isConsiderDate();
                    boolean threeWay = dialog.isThreeWay();

                    RSEMember rseAncestorMember = null;
                    if (threeWay) {
                        ISeriesMember ancestorMember = dialog.getAncestorConnection().getISeriesMember(getShell(), dialog.getAncestorLibrary(),
                            dialog.getAncestorFile(), dialog.getAncestorMember());
                        if (ancestorMember != null) {
                            rseAncestorMember = new RSEMember(ancestorMember);
                        }
                    }

                    RSEMember rseRightMember = null;
                    ISeriesMember rightMember = dialog.getRightConnection().getISeriesMember(getShell(), dialog.getRightLibrary(),
                        dialog.getRightFile(), dialog.getRightMember());
                    if (rightMember != null) {
                        rseRightMember = new RSEMember(rightMember);
                    }

                    CompareAction action = new CompareAction(editable, considerDate, threeWay, rseAncestorMember, rseLeftMember, rseRightMember, null);
                    action.run();
                }
            }
        } catch (Throwable e) {
        }
    }
}
