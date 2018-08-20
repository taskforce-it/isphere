/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import biz.isphere.lpex.comments.lpex.delegates.CLCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.DDSCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.ICommentDelegate;
import biz.isphere.lpex.comments.lpex.delegates.PNLGRPCommentsDelegate;
import biz.isphere.lpex.comments.lpex.delegates.RPGCommentsDelegate;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.internal.Position;

import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexCommentsAction extends AbstractLpexAction {

    public void doAction(LpexView view) {

        try {
            saveCursorPosition(view);

            Position start;
            Position end;
            if (anythingSelected(view)) {
                start = new Position(getBlockTopElement(view), getBlockTopPosition(view));
                end = new Position(getBlockBottomElement(view), getBLockBottomPosition(view));
            } else {
                start = new Position(getCurrentElement(view), getCurrentPosition(view));
                end = start;
            }

            // Range of lines
            if (start.getLine() < end.getLine()) {
                doLines(view, start.getLine(), end.getLine());
            } else if (start.getLine() == end.getLine()) {
                // Single line
                if (start.getColumn() == end.getColumn()) {
                    doLines(view, start.getLine(), end.getLine());
                } else if (start.getColumn() < end.getColumn()) {
                    // Selection
                    doSelection(view, start.getLine(), start.getColumn(), end.getColumn());
                }
            }

        } finally {
            restoreCursorPosition(view);
        }
    }

    protected ICommentDelegate getDelegate(LpexView view) throws MemberTypeNotSupportedException {

        String type = getMemberType();
        if ("CLP".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new CLCommentsDelegate(view);
        } else if ("CLLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new CLCommentsDelegate(view);
        } else if ("RPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("RPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("SQLRPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("SQLRPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGCommentsDelegate(view);
        } else if ("PRTF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("DSPF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("LF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("PF".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new DDSCommentsDelegate(view);
        } else if ("PNLGRP".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new PNLGRPCommentsDelegate(view);
        }

        throw new MemberTypeNotSupportedException();
    }
}
