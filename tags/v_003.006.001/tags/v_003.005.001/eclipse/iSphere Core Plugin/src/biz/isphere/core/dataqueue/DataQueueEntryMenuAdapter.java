/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.ByteHelper;
import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.DialogSettingsManager;
import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.HexFormatter;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;
import biz.isphere.core.dataqueue.viewer.DataQueueEntryViewer;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class DataQueueEntryMenuAdapter extends MenuAdapter {

    private static final String LAST_USED_SAVE_PATH = "lastUsedSavePath"; //$NON-NLS-1$

    private DialogSettingsManager dialogSettingsManager = null;

    private Menu menu;
    private TableViewer tableViewer;
    private MenuItem menuItemDisplayMessage;
    private MenuItem menuItemCopyMessageAsText;
    private MenuItem menuItemCopyMessageAsHex;
    private MenuItem menuItemCopyMessageAsHexFormatted;
    private MenuItem menuItemSaveMessage;

    public DataQueueEntryMenuAdapter(Menu menu, TableViewer table) {

        this.menu = menu;
        this.tableViewer = table;
    }

    @Override
    public void menuShown(MenuEvent event) {
        destroyMenuItems();
        createMenuItems();
    }

    private void destroyMenuItems() {

        if (!((menuItemDisplayMessage == null) || (menuItemDisplayMessage.isDisposed()))) {
            menuItemDisplayMessage.dispose();
        }

        if (!((menuItemCopyMessageAsText == null) || (menuItemCopyMessageAsText.isDisposed()))) {
            menuItemCopyMessageAsText.dispose();
        }

        if (!((menuItemCopyMessageAsHex == null) || (menuItemCopyMessageAsHex.isDisposed()))) {
            menuItemCopyMessageAsHex.dispose();
        }

        if (!((menuItemCopyMessageAsHexFormatted == null) || (menuItemCopyMessageAsHexFormatted.isDisposed()))) {
            menuItemCopyMessageAsHexFormatted.dispose();
        }

        if (!((menuItemSaveMessage == null) || (menuItemSaveMessage.isDisposed()))) {
            menuItemSaveMessage.dispose();
        }
    }

    private void createMenuItems() {

        if (getSelectionCount() >= 1) {
            createMenuItemDisplayMessage();
        }

        if (getSelectionCount() == 1) {
            createMenuItemCopyMessageAsText();
            createMenuItemCopyMessageAsHex();
            createMenuItemCopyMessageAsHexFormatted();
            createMenuItemSaveMessage();
        }
    }

    public void createMenuItemDisplayMessage() {

        menuItemDisplayMessage = new MenuItem(menu, SWT.NONE);
        menuItemDisplayMessage.setText(Messages.Display);
        menuItemDisplayMessage.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                performDisplayMessage(getItems(), getSelectedItem());
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }

    public void createMenuItemCopyMessageAsText() {

        menuItemCopyMessageAsText = new MenuItem(menu, SWT.NONE);
        menuItemCopyMessageAsText.setText(Messages.Copy_message_as_text);
        menuItemCopyMessageAsText.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                performCopyAsText(getSelectedItem());
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }

    public void createMenuItemCopyMessageAsHex() {

        menuItemCopyMessageAsHex = new MenuItem(menu, SWT.NONE);
        menuItemCopyMessageAsHex.setText(Messages.Copy_message_as_hex);
        menuItemCopyMessageAsHex.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                performCopyAsHex(getSelectedItem());
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }

    public void createMenuItemCopyMessageAsHexFormatted() {

        menuItemCopyMessageAsHexFormatted = new MenuItem(menu, SWT.NONE);
        menuItemCopyMessageAsHexFormatted.setText(Messages.Copy_message_as_hex_formatted);
        menuItemCopyMessageAsHexFormatted.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                performCopyAsHexFormatted(getSelectedItem());
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }

    public void createMenuItemSaveMessage() {

        menuItemSaveMessage = new MenuItem(menu, SWT.NONE);
        menuItemSaveMessage.setText(Messages.Save_message);
        menuItemSaveMessage.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent paramSelectionEvent) {
                performSave(getSelectedItem());
            }

            public void widgetDefaultSelected(SelectionEvent paramSelectionEvent) {

            }
        });
    }

    private void performDisplayMessage(RDQM0200MessageEntry[] messages, RDQM0200MessageEntry messageEntry) {

        if (messageEntry == null) {
            return;
        }

        DataQueueEntryViewer viewer = new DataQueueEntryViewer(getShell());
        viewer.setMessages(messages);
        viewer.setSelectedItem(messageEntry);
        viewer.open();
    }

    private void performCopyAsText(RDQM0200MessageEntry messageEntry) {

        if (messageEntry == null) {
            return;
        }

        try {
            ClipboardHelper.setText(messageEntry.getMessageText());
        } catch (Throwable e) {
            handleError(e);
        }
    }

    private void performCopyAsHex(RDQM0200MessageEntry messageEntry) {

        if (messageEntry == null) {
            return;
        }

        try {
            ClipboardHelper.setText(ByteHelper.getHexString(messageEntry.getMessageBytes()));
        } catch (Throwable e) {
            handleError(e);
        }
    }

    private void performCopyAsHexFormatted(RDQM0200MessageEntry messageEntry) {

        if (messageEntry == null) {
            return;
        }

        try {

            HexFormatter formatter = new HexFormatter();

            byte[] messageBytes = messageEntry.getMessageBytes();
            String messageText = messageEntry.getMessageText();

            String formattedHexString = formatter.createFormattedHexText(messageBytes, messageText);

            ClipboardHelper.setText(formattedHexString);

        } catch (Throwable e) {
            handleError(e);
        }
    }

    private void performSave(RDQM0200MessageEntry messageEntry) {

        if (messageEntry == null) {
            return;
        }

        String fileName = getFileName();
        if (fileName == null) {
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(messageEntry.getMessageBytes());
        } catch (Throwable e) {
            handleError(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    handleError(e);
                }
            }
        }
    }

    private StructuredSelection getSelection() {

        ISelection selection = tableViewer.getSelection();
        if (selection instanceof StructuredSelection) {
            return ((StructuredSelection)selection);
        }

        ISpherePlugin.logError("DataQueueEntryMenuAdapter.getSelection(): Expected to get a 'StructuredSelection'.", null); //$NON-NLS-1$

        return new StructuredSelection();
    }

    private RDQM0200MessageEntry getSelectedItem() {
        return (RDQM0200MessageEntry)getSelection().getFirstElement();
    }

    private int getSelectionCount() {
        return getSelection().size();
    }

    private RDQM0200MessageEntry[] getItems() {

        if (getSelectionCount() == 1) {
            Object object = tableViewer.getInput();
            if (object instanceof RDQM0200) {
                RDQM0200 rdqm0200 = (RDQM0200)tableViewer.getInput();
                return rdqm0200.getMessages();
            }
        } else {
            List<RDQM0200MessageEntry> tmpMessages = new ArrayList<RDQM0200MessageEntry>();
            for (Object item : getSelection().toArray()) {
                if (item instanceof RDQM0200MessageEntry) {
                    tmpMessages.add((RDQM0200MessageEntry)item);
                }
            }
            return tmpMessages.toArray(new RDQM0200MessageEntry[tmpMessages.size()]);
        }

        return new RDQM0200MessageEntry[0];
    }

    private String getFileName() {

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(getShell(), SWT.SAVE);

        String[] filterNames = new String[] { "Data queue (*.dtaq)", FileHelper.getAllFilesText() }; //$NON-NLS-1$
        String[] filterExtensions = new String[] { "*.dtaq;", FileHelper.getAllFilesFilter() }; //$NON-NLS-1$
        String filterPath = getLastSavePath();

        dialog.setFilterNames(filterNames);
        dialog.setFilterExtensions(filterExtensions);
        dialog.setFilterPath(filterPath);
        dialog.setOverwrite(true);

        String fileName = dialog.open();
        if (fileName != null) {
            storeLastSavePath(dialog.getFilterPath());
        }

        return fileName;
    }

    private DialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new DialogSettingsManager(ISpherePlugin.getDefault().getDialogSettings(), getClass());
        }

        return dialogSettingsManager;
    }

    private String getLastSavePath() {
        return getDialogSettingsManager().loadValue(LAST_USED_SAVE_PATH, FileHelper.getDefaultRootDirectory());
    }

    private void storeLastSavePath(String path) {
        getDialogSettingsManager().storeValue(LAST_USED_SAVE_PATH, path);
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    private void handleError(Throwable e) {
        ISpherePlugin.logError("Unexpected error in class: " + getClass().getName(), e); //$NON-NLS-1$
    }
}
