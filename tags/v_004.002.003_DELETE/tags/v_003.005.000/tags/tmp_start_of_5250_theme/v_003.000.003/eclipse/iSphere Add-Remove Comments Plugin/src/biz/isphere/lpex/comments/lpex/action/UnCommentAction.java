/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.action;

import biz.isphere.lpex.comments.Messages;
import biz.isphere.lpex.comments.lpex.delegates.ICommentDelegate;
import biz.isphere.lpex.comments.lpex.exceptions.CommentNotFoundException;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.OperationNotSupportedException;

import com.ibm.lpex.core.LpexView;

public class UnCommentAction extends AbstractLpexAction {

    public static final String ID = "iSphere.Lpex.UnComment"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Uncomment_Lines, UnCommentAction.ID);
    }

    @Override
    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            ICommentDelegate delegate = getDelegate(view);
            for (element = firstLine; element <= lastLine; element++) {
                view.setElementText(element, delegate.uncomment(getElementText(view, element)));
            }

        } catch (CommentNotFoundException e) {
            String message = Messages.bind(Messages.Line_A_is_not_a_comment_The_operation_has_been_canceled, Integer.toString(element));
            displayMessage(view, message);
        } catch (OperationNotSupportedException e) {
            String message = Messages.bind(Messages.Operation_not_supported_for_member_type_A, getMemberType());
            displayMessage(view, message);
        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        }
    }

    @Override
    protected void doSelection(LpexView view, int line, int startColumn, int endColumn) {

        try {

            ICommentDelegate delegate = getDelegate(view);
            view.setElementText(line, delegate.uncomment(view.elementText(line), startColumn, endColumn));

        } catch (OperationNotSupportedException e) {
            String message = Messages.bind(Messages.Operation_not_supported_for_member_type_A, getMemberType());
            displayMessage(view, message);
        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        }
    }

}
