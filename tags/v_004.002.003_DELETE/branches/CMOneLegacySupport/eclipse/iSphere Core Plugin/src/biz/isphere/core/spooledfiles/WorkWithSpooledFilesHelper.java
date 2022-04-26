/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.clcommands.ICLPrompter;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.exception.CanceledByUserException;
import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.spooledfiles.view.events.ITableItemChangeListener;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent;
import biz.isphere.core.spooledfiles.view.events.TableItemChangedEvent.EventType;

import com.ibm.as400.ui.util.CommandPrompter;

public class WorkWithSpooledFilesHelper {

    private Shell shell;
    private String connectionName;

    private List<ITableItemChangeListener> changedListeners;

    public WorkWithSpooledFilesHelper(Shell shell, String connectionName) {
        this.shell = shell;
        this.connectionName = connectionName;
        this.changedListeners = new LinkedList<ITableItemChangeListener>();
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    public void setConnection(String connectionName) {
        this.connectionName = connectionName;
    }

    public void performChangeSpooledFile(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {

            try {

                ICLPrompter command = IBMiHostContributionsHandler.getCLPrompter(connectionName);
                command.setCommandString(spooledFile.getCommandChangeAttribute());
                command.setParent(Display.getCurrent().getActiveShell());
                if (command.showDialog() == CommandPrompter.OK) {

                    String message = spooledFile.changeAttribute(command.getCommandString());
                    if (handleErrorMessage(message)) {
                        break;
                    } else {
                        notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.CHANGED));
                    }

                }
            } catch (Exception e) {
                if (handleException(e)) {
                    break;
                }
            }

        }
    }

    public void performDeleteSpooledFile(SpooledFile[] spooledFiles) {

        if (!isConfirmed(spooledFiles)) {
            return;
        }

        List<SpooledFile> deletedSpooledFiles = new ArrayList<SpooledFile>();

        for (SpooledFile spooledFile : spooledFiles) {
            spooledFile.delete();
            deletedSpooledFiles.add(spooledFile);
        }

        if (deletedSpooledFiles.size() > 0) {
            SpooledFile[] deletedSpooledFilesArray = deletedSpooledFiles.toArray(new SpooledFile[deletedSpooledFiles.size()]);
            notifyChangedListener(new TableItemChangedEvent(deletedSpooledFilesArray, EventType.DELETED));
        }
    }

    public void performHoldSpooledFile(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.hold())) {
                break;
            }
            notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.HOLD));
        }
    }

    public void performReleaseSpooledFile(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.release())) {
                break;
            }
            notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.RELEASED));
        }
    }

    public void performShowMessages(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.replyMessage())) {
                break;
            }
            notifyChangedListener(new TableItemChangedEvent(spooledFile, EventType.MESSAGE));
        }
    }

    public void performOpenAsText(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_TEXT))) {
                break;
            }
        }
    }

    public void performOpenAsHtml(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_HTML))) {
                break;
            }
        }
    }

    public void performOpenAsPdf(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.open(IPreferences.OUTPUT_FORMAT_PDF))) {
                break;
            }
        }
    }

    public void performSaveAsText(SpooledFile[] spooledFiles) {

        try {
            for (SpooledFile spooledFile : spooledFiles) {
                if (handleErrorMessage(spooledFile.saveWithCancelOption(getShell(), IPreferences.OUTPUT_FORMAT_TEXT))) {
                    break;
                }
            }
        } catch (CanceledByUserException e) {
            // nothing to do here
        }
    }

    public void performSaveAsHtml(SpooledFile[] spooledFiles) {

        try {
            for (SpooledFile spooledFile : spooledFiles) {
                if (handleErrorMessage(spooledFile.saveWithCancelOption(getShell(), IPreferences.OUTPUT_FORMAT_HTML))) {
                    break;
                }
            }
        } catch (CanceledByUserException e) {
            // nothing to do here
        }
    }

    public void performSaveAsPdf(SpooledFile[] spooledFiles) {

        try {
            for (SpooledFile spooledFile : spooledFiles) {
                if (handleErrorMessage(spooledFile.saveWithCancelOption(getShell(), IPreferences.OUTPUT_FORMAT_PDF))) {
                    break;
                }
            }
        } catch (CanceledByUserException e) {
            // nothing to do here
        }
    }

    public void performDisplaySpooledFileProperties(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            if (handleErrorMessage(spooledFile.displayProperties(getShell()))) {
                break;
            }
        }
    }

    public void performOpen(SpooledFile[] spooledFiles) {

        for (SpooledFile spooledFile : spooledFiles) {
            String openFormat = Preferences.getInstance().getSpooledFileConversionDefaultFormat();
            handleErrorMessage(spooledFile.open(openFormat));
        }
    }

    private boolean isConfirmed(SpooledFile[] spooledFiles) {

        ConfirmDeletionSpooledFiles dialog = new ConfirmDeletionSpooledFiles(getShell(), spooledFiles);
        if (dialog.open() == Dialog.OK) {
            return true;
        }

        return false;
    }

    public void addChangedListener(ITableItemChangeListener modifyListener) {
        changedListeners.add(modifyListener);
    }

    public void removeChangedListener(ITableItemChangeListener modifyListener) {
        changedListeners.remove(modifyListener);
    }

    private void notifyChangedListener(TableItemChangedEvent event) {
        for (ITableItemChangeListener listener : changedListeners) {
            listener.itemChanged(event);
        }
    }

    private boolean handleErrorMessage(String message) {

        if (message != null) {
            MessageDialog.openError(getShell(), Messages.Error, message);
            return true;
        }

        return false;
    }

    private boolean handleException(Exception e) {
        return handleErrorMessage(ExceptionHelper.getLocalizedMessage(e));
    }

    private Shell getShell() {
        return shell;
    }
}
