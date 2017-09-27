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
import biz.isphere.lpex.comments.lpex.exceptions.MemberTypeNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class IndentAction extends AbstractLpexIndentionAction {

    public static final String ID = "iSphere.Lpex.Indent"; //$NON-NLS-1$

    public static String getLPEXMenuAction() {
        return getLPEXMenuAction(Messages.Menu_Indent_Lines, IndentAction.ID);
    }

    @Override
    protected void doLines(LpexView view, int firstLine, int lastLine) {

        int element = 0;

        try {

            IIndentionDelegate delegate = getDelegate(view);
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    delegate.setValidationMode(true);
                } else {
                    delegate.setValidationMode(false);
                }
                for (element = firstLine; element <= lastLine; element++) {
                    if (isTextLine(view, element)) {
                        view.setElementText(element, delegate.indent(getElementText(view, element)));
                    }
                }
            }

        } catch (MemberTypeNotSupportedException e) {
            String message = Messages.bind(Messages.Member_type_A_not_supported, getMemberType());
            displayMessage(view, message);
        } catch (FixedFormatNotSupportedException e) {
            String message = Messages.Operation_not_supported_for_fixed_format_statements;
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

        doLines(view, element, element);
    }
}
