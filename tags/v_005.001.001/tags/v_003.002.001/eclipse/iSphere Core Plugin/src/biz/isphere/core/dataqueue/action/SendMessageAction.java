/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.viewer.SendMessageDialog;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.internal.api.senddataqentry.QSNDDTAQ;

/**
 * Action, that is used to send a message to a data queue.
 * 
 * @author Thomas Raddatz
 */
public class SendMessageAction extends Action {

    private Shell shell;
    private RemoteObject dataQueue;
    private boolean isKeyed;
    private int keyLen;
    private int maxDataLen;

    public SendMessageAction() {
        super(Messages.Send_message, Action.AS_PUSH_BUTTON); //$NON-NLS-1$

        this.shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        setToolTipText(Messages.Send_message_tooltip);
        setImageDescriptor(ISpherePlugin.getDefault().getImageRegistry().getDescriptor(ISpherePlugin.IMAGE_SEND_MESSAGE));

        setActionEnablement();
    }

    public void setDataQueue(RemoteObject dataQueue, boolean isKeyed, int keyLen, int maxDataLen) {
        this.dataQueue = dataQueue;
        this.isKeyed = isKeyed;
        this.keyLen = keyLen;
        this.maxDataLen = maxDataLen;
    }

    private void setActionEnablement() {

        if (dataQueue == null) {
            setEnabled(false);
        } else {
            setEnabled(true);
        }

    }

    @Override
    public void run() {

        SendMessageDialog dialog = new SendMessageDialog(shell, dataQueue.getName(), dataQueue.getLibrary(), isKeyed, keyLen, maxDataLen);
        if (dialog.open() == Dialog.OK) {
            sendData(dialog.getKey(), dialog.getData());
        }
    }

    private void sendData(Object key, Object data) {

        try {

            QSNDDTAQ qsnddtaq = new QSNDDTAQ(dataQueue.getSystem());
            qsnddtaq.setDataQueue(dataQueue.getName(), dataQueue.getLibrary());
            if (!qsnddtaq.execute(key, data)) {
                MessageDialog.openError(shell, Messages.E_R_R_O_R, qsnddtaq.getErrorMessage());
            }
            ;

        } catch (Throwable e) {
            MessageDialog.openError(shell, Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
        }
    }
}
