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
import biz.isphere.lpex.comments.lpex.exceptions.FixedFormatNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.OperationNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class CommentAction extends AbstractLpexCommentsAction {

    public static final String ID = "iSphere.Lpex.Comment"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Comment_Lines, CommentAction.ID);
    }

    @Override
    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            ICommentDelegate delegate = getDelegate(view);
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    delegate.setValidationMode(true);
                } else {
                    delegate.setValidationMode(false);
                }
                for (element = firstLine; element <= lastLine; element++) {
                    if (isTextLine(view, element)) {
                        view.setElementText(element, delegate.comment(getElementText(view, element)));
                    }
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
    protected void doSelection(LpexView view, int element, int startColumn, int endColumn) {

        try {

            String text = getElementText(view, element);
            if (endColumn > text.length()) {
                endColumn = text.length();
            }

            if (startColumn > endColumn) {
                displayMessage(view, Messages.Selection_is_out_of_range_The_operation_has_been_canceled);
                return;
            }

            ICommentDelegate delegate = getDelegate(view);
            view.setElementText(element, delegate.comment(text, startColumn, endColumn));

        } catch (FixedFormatNotSupportedException e) {
            String message = Messages.Operation_not_supported_for_fixed_format_statements;
            displayMessage(view, message);
        } catch (OperationNotSupportedException e) {
            String message = Messages.bind(Messages.Operation_not_supported_for_member_type_A, getMemberType());
            displayMessage(view, message);
        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        } catch (CommentExistsException e) {
            String message = Messages.Selection_has_already_been_commented_The_operation_has_been_canceled;
            displayMessage(view, message);
        } catch (TextLimitExceededException e) {
            String message = Messages.bind(Messages.Text_limit_would_have_been_exceeded_on_line_A_The_operation_has_been_canceled,
                Integer.toString(element));
            displayMessage(view, message);
        }
    }
}
