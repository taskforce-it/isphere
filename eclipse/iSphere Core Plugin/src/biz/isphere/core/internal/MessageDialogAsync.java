/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.UIHelper;
import biz.isphere.core.Messages;

/**
 * This class is used for displaying message dialogs from a batch job. The
 * message dialogs can be blocking or non-blocking.
 */
public class MessageDialogAsync {

    /**
     * Displays a non-blocking information message.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param title - title of the message dialog
     * @param message - information message that is displayed
     */
    public static void displayNonBlockingInformation(Shell shell, String title, String message) {
        MessageDialog dialog = createInformationMessageDialog(shell, title, message);
        displayNonBlockingDialog(shell, dialog);
    }

    /**
     * Displays a non-blocking error message.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param message - information message that is displayed
     */
    public static void displayNonBlockingError(Shell shell, String message) {
        displayNonBlockingError(shell, Messages.E_R_R_O_R, message);
    }

    /**
     * Displays a non-blocking error message.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param title - title of the message dialog
     * @param message - information message that is displayed
     */
    public static void displayNonBlockingError(Shell shell, String title, String message) {
        MessageDialog dialog = createErrorMessageDialog(shell, title, message);
        displayNonBlockingDialog(shell, dialog);
    }

    /**
     * Displays a non-blocking dialog.
     * 
     * @param dialog - dialog that is displayed
     */
    public static void displayNonBlockingDialog(Shell shell, Dialog dialog) {
        DialogUIJob job = new DialogUIJob(shell.getDisplay(), dialog);
        job.schedule();
    }

    /**
     * Displays a blocking error message.
     * 
     * @param message - error message that is displayed
     */
    public static void displayBlockingError(final String message) {
        displayBlockingError(Messages.E_R_R_O_R, message);
    }

    /**
     * Displays a blocking error message.
     * 
     * @param title - title of the message dialog
     * @param message - error message that is displayed
     */
    public static void displayBlockingError(final String title, final String message) {
        MessageDialog dialog = createErrorMessageDialog(getShell(), title, message);
        displayBlockingDialog(dialog);
    }

    /**
     * Displays a blocking dialog.
     * 
     * @param dialog - dialog that is displayed
     */
    public static void displayBlockingDialog(Dialog dialog) {
        Runnable runnable = new DialogUIRunnable(dialog);
        Display.getDefault().syncExec(runnable);
    }

    /**
     * Produces an information message dialog.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param title - title of the message dialog
     * @param message - information message that is displayed
     * @return information message dialog
     */
    private static MessageDialog createInformationMessageDialog(Shell shell, String title, String message) {
        if (shell == null) {
            shell = UIHelper.getActiveShell();
        }
        return createDialog(shell, title, message, MessageDialog.INFORMATION);
    }

    /**
     * Produces an error message dialog.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param title - title of the message dialog
     * @param message - error message that is displayed
     * @return error message dialog
     */
    private static MessageDialog createErrorMessageDialog(Shell shell, String title, String message) {
        if (shell == null) {
            shell = UIHelper.getActiveShell();
        }
        return createDialog(shell, title, message, MessageDialog.ERROR);
    }

    /**
     * Produces a message dialog of a given kind.
     * 
     * @param shell - Shell used for displaying the message dialog
     * @param title - title of the message dialog
     * @param message - information message that is displayed
     * @param kind - kind of the message dialog
     * @return message dialog of the specified kind
     */
    private static MessageDialog createDialog(Shell shell, String title, String message, int kind) {
        return new MessageDialog(shell, title, null, message, kind, getButtonLabels(kind), 0);
    }

    /**
     * Produces the button label of a given kind.
     * 
     * @param kind - kind of the button labels
     * @return array of button labels
     */
    static String[] getButtonLabels(int kind) {
        String[] dialogButtonLabels;
        switch (kind) {
        case MessageDialog.ERROR:
        case MessageDialog.INFORMATION:
        case MessageDialog.WARNING: {
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL };
            break;
        }
        case MessageDialog.QUESTION: {
            dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
            break;
        }
        default: {
            throw new IllegalArgumentException("Illegal value for kind in MessageDialog.open()"); //$NON-NLS-1$
        }
        }
        return dialogButtonLabels;
    }

    private static Shell getShell() {
        return UIHelper.getActiveShell();
    }
}
