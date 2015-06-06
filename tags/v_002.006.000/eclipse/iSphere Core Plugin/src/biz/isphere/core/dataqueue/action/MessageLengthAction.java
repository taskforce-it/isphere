/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;

public class MessageLengthAction extends Action {

    public static final int MAX_LENGTH = 64512;

    private IDialogView view;
    private int length;

    public MessageLengthAction(IDialogView view, int length) {
        super("", SWT.PUSH);

        this.view = view;
        this.length = length;

        if (length == MAX_LENGTH) {
            setText(Messages.Maximum_message_length_menu_item_no_max);
            setToolTipText(Messages.Maximum_message_length_menu_item_no_max_tooltip);
        } else {
            setText(Messages.bind(Messages.Maximum_message_length_menu_item_A_bytes, length));
            setToolTipText(Messages.bind(Messages.Maximum_message_length_menu_item_A_bytes_tooltip, length));
        }

        setEnabled(false);
    }

    public int getLength() {
        return length;
    }

    @Override
    public void run() {
        view.refreshData();
    }

    public static int lengthToInt(String text) {

        if (Messages.Maximum_message_length_menu_item_no_max.equals(text)) {
            return MessageLengthAction.MAX_LENGTH;
        } else {
            return IntHelper.tryParseInt(text);
        }
    }

    public static String lengthToString(int length) {

        if (length == MessageLengthAction.MAX_LENGTH) {
            return Messages.Maximum_message_length_menu_item_no_max;
        } else {
            return Integer.toString(length);
        }
    }
}
