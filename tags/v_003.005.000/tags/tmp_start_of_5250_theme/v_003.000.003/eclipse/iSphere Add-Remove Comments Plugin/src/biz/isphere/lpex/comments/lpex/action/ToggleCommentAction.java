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
import biz.isphere.lpex.comments.lpex.exceptions.CommentExistsException;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class ToggleCommentAction extends AbstractLpexAction {

    public static final String ID = "iSphere.Lpex.ToggleComment"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Toggle_Comment_Lines, ToggleCommentAction.ID);
    }

    @Override
    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            ICommentDelegate delegate = getDelegate(view);
            boolean isAllCommented = true;
            for (element = firstLine; element <= lastLine; element++) {
                if (!delegate.isLineComment(getElementText(view, element))) {
                    isAllCommented = false;
                    break;
                }
            }

            for (element = firstLine; element <= lastLine; element++) {
                if (isAllCommented) {
                    view.setElementText(element, delegate.uncomment(getElementText(view, element)));
                } else {
                    view.setElementText(element, delegate.comment(getElementText(view, element)));
                }
            }

        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        } catch (CommentExistsException e) {
            String message = Messages.bind(Messages.Line_A_has_already_been_commented_The_operation_has_been_canceled, Integer.toString(element));
            displayMessage(view, message);
        } catch (TextLimitExceededException e) {
            String message = Messages.bind(Messages.Text_limit_would_have_been_exceeded_on_line_A_The_operation_has_been_canceled,
                Integer.toString(element));
            displayMessage(view, message);
        } catch (Throwable e) {
            displayMessage(view, e.getLocalizedMessage());
        }
    }

    @Override
    protected void doSelection(LpexView view, int line, int startColumn, int endColumn) {
        String message = Messages.bind(Messages.Operation_not_supported_for_member_type_A, getMemberType());
        displayMessage(view, message);
    }

}
