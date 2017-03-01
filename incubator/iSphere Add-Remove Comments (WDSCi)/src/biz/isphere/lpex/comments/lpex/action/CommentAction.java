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
import biz.isphere.lpex.comments.lpex.exceptions.CommentExistsException;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class CommentAction extends AbstractLpexAction {

    public static final String ID = "iSphere.Lpex.Comment"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Comment_Lines, CommentAction.ID);
    }

    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            ICommentDelegate delegate = getDelegate(view);
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    delegate.validate(true);
                } else {
                    delegate.validate(false);
                }
                for (element = firstLine; element <= lastLine; element++) {
                    if (isTextLine(view, element)) {
                        view.setElementText(element, delegate.comment(view.elementText(element)));
                    }
                }
            }

        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind("Membery type {0} not supported.", getMemberType());
            displayMessage(view, message);
        } catch (CommentExistsException e) {
            String message = Messages.bind(Messages.Line_A_has_already_been_commented_The_operation_has_been_canceled, Integer.toString(element));
            displayMessage(view, message);
        } catch (TextLimitExceededException e) {
            String message = Messages.bind(Messages.Text_limit_would_have_been_exceeded_on_line_A_The_operation_has_been_canceled, Integer
                .toString(element));
            displayMessage(view, message);
        } catch (Throwable e) {
            displayMessage(view, e.getLocalizedMessage());
        }
    }

    protected void doSelection(final LpexView view, final int element, final int startColumn, final int endColumn) {

        try {
            
            ICommentDelegate delegate = getDelegate(view);
            view.setElementText(element, delegate.comment(view.elementText(element), startColumn, endColumn));

        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind("Membery type {0} not supported.", getMemberType());
            displayMessage(view, message);
        } catch (CommentExistsException e) {
            String message = Messages.Selection_has_already_been_commented_The_operation_has_been_canceled;
            displayMessage(view, message);
        } catch (TextLimitExceededException e) {
            String message = Messages.bind(Messages.Text_limit_would_have_been_exceeded_on_line_A_The_operation_has_been_canceled, Integer
                .toString(element));
            displayMessage(view, message);
        }
    }
}
