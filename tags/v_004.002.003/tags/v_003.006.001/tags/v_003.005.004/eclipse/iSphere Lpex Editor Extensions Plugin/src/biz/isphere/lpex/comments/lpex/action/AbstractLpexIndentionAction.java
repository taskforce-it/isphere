/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import biz.isphere.lpex.comments.lpex.delegates.IIndentionDelegate;
import biz.isphere.lpex.comments.lpex.delegates.RPGIndentionDelegate;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.internal.Position;

import com.ibm.lpex.core.LpexView;

public abstract class AbstractLpexIndentionAction extends AbstractLpexAction {

    public void doAction(LpexView view) {

        boolean isBlockSelection = false;

        try {

            Position start;
            Position end;
            if (anythingSelected(view)) {
                start = new Position(getBlockTopElement(view), getBlockTopPosition(view));
                end = new Position(getBlockBottomElement(view), getBLockBottomPosition(view));
                isBlockSelection = true;
            } else {
                start = new Position(getCurrentElement(view), getCurrentPosition(view));
                end = start;
                isBlockSelection = false;
            }

            doLines(view, start.getLine(), end.getLine());

        } finally {
            if (!isBlockSelection) {
                goStartOfText(view, getCurrentElement(view));
            }
        }
    }

    protected IIndentionDelegate getDelegate(LpexView view) throws MemberTypeNotSupportedException {

        String type = getMemberType();
        if ("RPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGIndentionDelegate(view);
        } else if ("RPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGIndentionDelegate(view);
        } else if ("SQLRPG".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGIndentionDelegate(view);
        } else if ("SQLRPGLE".equalsIgnoreCase(type)) { //$NON-NLS-1$
            return new RPGIndentionDelegate(view);
        }

        throw new MemberTypeNotSupportedException();
    }
}
