/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.action;

import org.eclipse.jface.dialogs.Dialog;

import biz.isphere.strpreprc.gui.StrPrePrcHeaderDialog;
import biz.isphere.strpreprc.model.StrPrePrcHeader;

import com.ibm.lpex.core.LpexView;

public class EditHeaderAction extends AbstractHeaderAction {

    @Override
    public void doAction(LpexView view) {

        try {

            StrPrePrcHeader header = new StrPrePrcHeader();
            header.loadFromLpexView(view);

            StrPrePrcHeaderDialog dialog = new StrPrePrcHeaderDialog(getShell());
            dialog.setInput(header);
            if (dialog.open() == Dialog.OK) {
                header.updateLpexView(view);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getLPEXMenuAction() {
        return "\"Edit header\" " + EditHeaderAction.class.getName();
    }
}
