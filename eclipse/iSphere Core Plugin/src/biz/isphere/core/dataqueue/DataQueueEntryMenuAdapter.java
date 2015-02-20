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
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.retrieve.message.RDQM0200MessageEntry;
import biz.isphere.core.internal.IDialogSettingsManager;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class DataQueueEntryMenuAdapter extends MenuAdapter {

    private static final String LAST_USED_SAVE_PATH = "lastUsedSavePath"; //$NON-NLS-1$

    private static final String OFFSET_DELIMITER = ":   "; //$NON-NLS-1$
    private static final String HEX_PACKAGE_DELIMITER = " "; //$NON-NLS-1$
    private static final String TEXT_DELIMITER = "   - "; //$NON-NLS-1$
    private static final String NEW_LINE = "\n"; //$NON-NLS-1$
    private static final String DECIMAL_NUMBER_FORMAT = "00000"; //$NON-NLS-1$

    private IDialogSettingsManager dialogSettingsManager = null;
    private DecimalFormat formatter;

    private Menu menu;
    private TableViewer tableViewer;
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

        if (getSelectionCount() == 1) {
            createMenuItemCopyMessageAsText();
            createMenuItemCopyMessageAsHex();
            createMenuItemCopyMessageAsHexFormatted();
            createMenuItemSaveMessage();
        }
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

            int hexOffset = 0;
            int textOffset = 0;
            int hexLength = 32;
            int textLength = hexLength / 2;

            String hexString = ByteHelper.getHexString(messageEntry.getMessageBytes());
            String textString = messageEntry.getMessageText();
            StringBuilder formattedHexString = new StringBuilder();

            while (hexOffset < hexString.length()) {

                textOffset = hexOffset / 2;

                String tmpHexString;
                String tmpTextString;
                if ((hexOffset + hexLength) <= hexString.length()) {
                    tmpHexString = hexString.substring(hexOffset, hexOffset + hexLength);
                    tmpTextString = textString.substring(textOffset, textOffset + textLength);
                } else {
                    tmpHexString = hexString.substring(hexOffset);
                    tmpTextString = textString.substring(hexOffset / 2);
                }

                if (tmpHexString.length() < hexLength) {
                    tmpHexString = StringHelper.getFixLength(tmpHexString, hexLength);
                    tmpTextString = StringHelper.getFixLength(tmpTextString, textLength);
                }

                appendLine(formattedHexString, hexOffset, tmpTextString, tmpHexString);
                hexOffset = hexOffset + hexLength;
            }

            ClipboardHelper.setText(formattedHexString.toString());
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

    private void appendLine(StringBuilder formattedHexString, int offset, String textString, String hexString) {

        formattedHexString.append(getFormatter().format(offset));
        formattedHexString.append(OFFSET_DELIMITER);

        String packageDelimiter = null;
        int packageWidth = 8;
        int count = hexString.length() / packageWidth;
        int tempOffset = 0;
        while (count > 0) {

            if (packageDelimiter != null) {
                formattedHexString.append(packageDelimiter);
            }

            formattedHexString.append(hexString.substring(tempOffset, tempOffset + packageWidth));
            packageDelimiter = HEX_PACKAGE_DELIMITER;

            tempOffset = tempOffset + packageWidth;
            count--;
        }

        formattedHexString.append(TEXT_DELIMITER);
        formattedHexString.append(textString);
        formattedHexString.append(NEW_LINE);
    }

    private NumberFormat getFormatter() {
        if (formatter == null) {
            formatter = new DecimalFormat(DECIMAL_NUMBER_FORMAT);
        }
        return formatter;
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

    private IDialogSettingsManager getDialogSettingsManager() {

        if (dialogSettingsManager == null) {
            dialogSettingsManager = new IDialogSettingsManager(getClass());
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
