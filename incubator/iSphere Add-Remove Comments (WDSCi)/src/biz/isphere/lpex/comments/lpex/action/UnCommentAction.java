/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.lpex.delegates.CLCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.ICommentDelegate;

import com.ibm.lpex.core.LpexView;

public class UnCommentAction extends AbstractLpexAction {

    public static final String ID = "iSphere.Lpex.UnComment"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Uncomment_Lines, UnCommentAction.ID);
    }

    @Override
    protected ICommentDelegate getDelegate(LpexView view) {
        return new CLCommentsDelegate(view);
    }

    protected void doLines(LpexView view, int firstLine, int lastLine) {

        ICommentDelegate delegate = getDelegate(view);
        for (int i = firstLine; i <= lastLine; i++) {
            view.setElementText(i, delegate.uncomment(view.elementText(i)));
        }
    }

    protected void doSelection(LpexView view, int line, int startColumn, int endColumn) {

        ICommentDelegate delegate = getDelegate(view);
        view.setElementText(line, delegate.uncomment(view.elementText(line), startColumn, endColumn));
    }

}
