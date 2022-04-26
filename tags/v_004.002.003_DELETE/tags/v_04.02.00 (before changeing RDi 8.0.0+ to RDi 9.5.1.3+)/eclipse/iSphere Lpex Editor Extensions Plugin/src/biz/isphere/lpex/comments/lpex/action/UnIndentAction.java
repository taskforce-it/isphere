/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.lpex.delegates.IIndentionDelegate;
import biz.isphere.lpex.comments.lpex.exceptions.FixedFormatNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.MaxLeftMarginReachedException;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.OperationNotSupportedException;

import com.ibm.lpex.core.LpexView;

public class UnIndentAction extends AbstractLpexIndentionAction {

    public static final String ID = "iSphere.Lpex.UnIndent"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Unindent_Lines, UnIndentAction.ID);
    }

    @Override
    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            IIndentionDelegate delegate = getDelegate(view);
            for (element = firstLine; element <= lastLine; element++) {
                view.setElementText(element, delegate.unindent(getElementText(view, element)));
            }

        } catch (MaxLeftMarginReachedException e) {
            String message = Messages.Left_margin_reached_The_operation_has_been_canceled;
            displayMessage(view, message);
        } catch (FixedFormatNotSupportedException e) {
            String message = Messages.Operation_not_supported_for_fixed_format_statements;
            displayMessage(view, message);
        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        }
    }

    @Override
    protected void doSelection(LpexView view, int element, int startColumn, int endColumn) {

        doLines(view, element, element);
    }

}
