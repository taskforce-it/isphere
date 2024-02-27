/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;

/**
 * This class is used for displaying message dialogs from a batch job. The
 * message dialogs can be blocking or non-blocking.
 */
public class MessageDialogAsync {

    /**
     * Displays a non-blocking information message. This method can be called
     * from non UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param message - information message that is displayed
     */
    public static void displayNonBlockingInformation(Shell shell, String message) {
        displayNonBlockingDialog(shell, MessageDialog.INFORMATION, Messages.Informational, message);
    }

    /**
     * Displays a non-blocking information message. This method can be called
     * from non UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param messages - information messages that are displayed
     */
    public static void displayNonBlockingInformation(Shell shell, String[] messages) {
        displayNonBlockingDialog(shell, MessageDialog.INFORMATION, Messages.Informational, messages);
    }

    /**
     * Displays a non-blocking information message. This method can be called
     * from non UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param title - title of the message dialog
     * @param messages - information message that is displayed
     */
    public static void displayNonBlockingInformation(Shell shell, String title, String[] messages) {
        displayNonBlockingDialog(shell, MessageDialog.INFORMATION, title, messages);
    }

    /**
     * Displays a non-blocking information message. This method can be called
     * from non UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param title - title of the message dialog
     * @param message - information messages that are displayed
     */
    public static void displayNonBlockingInformation(Shell shell, String title, String message) {
        displayNonBlockingDialog(shell, MessageDialog.INFORMATION, title, message);
    }

    /**
     * Displays a non-blocking error message. This method can be called from non
     * UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param message - information message that is displayed
     */
    public static void displayNonBlockingError(Shell shell, String message) {
        displayNonBlockingError(shell, Messages.E_R_R_O_R, message);
    }

    /**
     * Displays a non-blocking error message. This method can be called from non
     * UI jobs.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param title - title of the message dialog
     * @param messages - information message that is displayed
     */
    public static void displayNonBlockingError(Shell shell, String title, String... messages) {
        displayNonBlockingDialog(shell, MessageDialog.INFORMATION, title, messages);
    }

    /**
     * Displays a non-blocking dialog.
     * 
     * @param shell - Shell used for displaying the message dialog. If shell is
     *        null, the default display is used.
     * @param kind - kind of the message dialog
     * @param title - title of the message dialog
     * @param messages - information message that is displayed
     */
    public static void displayNonBlockingDialog(Shell shell, int kind, String title, String... messages) {
        DialogUIJob job = new DialogUIJob(shell, title, createFinalMessage(messages), kind);
        job.schedule();
    }

    /**
     * Displays a blocking error message. This method can be called from non UI
     * jobs.
     * 
     * @param message - error message that is displayed
     */
    public static int displayBlockingError(final String message) {
        return displayBlockingError(Messages.E_R_R_O_R, message);
    }

    /**
     * Displays a blocking error message. This method can be called from non UI
     * jobs.
     * 
     * @param title - title of the message dialog
     * @param messages - error message that is displayed
     */
    public static int displayBlockingError(final String title, final String... messages) {
        return displayBlockingDialog(MessageDialog.ERROR, title, messages);
    }

    /**
     * Displays a blocking Confirmation message. This method can be called from
     * non UI jobs.
     * 
     * @param messages - error message that is displayed
     */
    public static int displayBlockingConfirmation(final String... messages) {
        return displayBlockingDialog(MessageDialog.CONFIRM, Messages.Confirmation, messages);
    }

    /**
     * Displays a blocking Confirmation message. This method can be called from
     * non UI jobs.
     * 
     * @param title - title of the message dialog
     * @param messages - error message that is displayed
     */
    public static int displayBlockingConfirmation(final String title, final String... messages) {
        return displayBlockingDialog(MessageDialog.CONFIRM, title, messages);
    }

    /**
     * Displays a blocking Question message. This method can be called from non
     * UI jobs.
     * 
     * @param messages - error message that is displayed
     */
    public static int displayBlockingQuestion(final String... messages) {
        return displayBlockingDialog(MessageDialog.QUESTION, Messages.Confirmation, messages);
    }

    /**
     * Displays a blocking Question message. This method can be called from non
     * UI jobs.
     * 
     * @param title - title of the message dialog
     * @param messages - error message that is displayed
     */
    public static int displayBlockingQuestion(final String title, final String... messages) {
        return displayBlockingDialog(MessageDialog.QUESTION, title, messages);
    }

    /**
     * Displays a blocking dialog. This method can be called from non UI jobs.
     * 
     * @param kind - kind of the message dialog
     * @param title - title of the message dialog
     * @param messages - error message that is displayed
     */
    public static int displayBlockingDialog(int kind, String title, String... messages) {
        DialogUIRunnable runnable = new DialogUIRunnable(title, createFinalMessage(messages), kind);
        Display.getDefault().syncExec(runnable);
        return runnable.getResult();
    }

    /**
     * Displays a blocking dialog. This method can be called from non UI jobs.
     * 
     * @param kind - kind of the message dialog
     * @param buttonLabels - buttons to display
     * @param title - title of the message dialog
     * @param messages - error message that is displayed
     */
    public static int displayBlockingDialog(int kind, String[] buttonLabels, String title, String... messages) {
        DialogUIRunnable runnable = new DialogUIRunnable(title, createFinalMessage(messages), kind, buttonLabels);
        Display.getDefault().syncExec(runnable);
        return runnable.getResult();
    }

    /**
     * Produces the final message that is displayed.
     * 
     * @param messages - one or more messages that are concatenated.
     * @return final message
     */
    private static String createFinalMessage(String[] messages) {

        StringBuilder buffer = new StringBuilder();
        for (String message : messages) {
            if (buffer.length() > 0) {
                buffer.append("\n");
            }
            buffer.append(message);
        }

        return buffer.toString();
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
        case MessageDialog.CONFIRM: {
            dialogButtonLabels = new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL };
            break;
        }
        default: {
            throw new IllegalArgumentException("Illegal value for kind: " + kind); //$NON-NLS-1$
        }
        }
        return dialogButtonLabels;
    }
}
