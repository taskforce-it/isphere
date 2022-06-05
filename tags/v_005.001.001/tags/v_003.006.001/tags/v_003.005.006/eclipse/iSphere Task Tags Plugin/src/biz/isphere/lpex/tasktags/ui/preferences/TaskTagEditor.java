/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.ui.preferences;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import biz.isphere.adapter.swt.widgets.XDecoratedText;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.lpex.tasktags.Messages;

/**
 * Simple editor to create or change a LPEX task tag.
 * 
 * @author Thomas Raddatz
 */
public class TaskTagEditor extends Dialog {

    private static final int FIRST_AND_ONLY_ITEM = 0;

    private int result;

    private Shell shell;

    private Table tblFileExtensions;

    private HashSet<String> fileExtensionsSet;

    private String editedFileExtension;

    private Text txtFileExtension;

    private XDecoratedText decTxtFileExtension;

    private Button btnOk;

    private Button btnCancel;

    private int mode;

    private static final int MODE_NEW = 1;

    private static final int MODE_EDIT = 2;

    public static TaskTagEditor getEditorForNew(Shell aParent, Table aTable) {
        TaskTagEditor tEditor = new TaskTagEditor(aParent, SWT.NONE);
        tEditor.mode = MODE_NEW;
        tEditor.setText(Messages.TaskTagEditor_headline_new);
        tEditor.setModel(aTable);
        return tEditor;
    }

    public static TaskTagEditor getEditorForEdit(Shell aParent, Table aTable) {
        TaskTagEditor tEditor = new TaskTagEditor(aParent, SWT.NONE);
        tEditor.mode = MODE_EDIT;
        tEditor.setText(Messages.TaskTagEditor_headline_edit);
        tEditor.setModel(aTable);
        return tEditor;
    }

    private TaskTagEditor(Shell aParent, int aStyle) {
        super(aParent, aStyle);
    }

    private void setModel(Table aTable) {
        tblFileExtensions = aTable;
        fileExtensionsSet = new HashSet<String>();
        if (aTable == null) {
            return;
        }

        for (TableItem tItem : tblFileExtensions.getItems()) {
            fileExtensionsSet.add(tItem.getText(0).toUpperCase());
        }

        if (mode == MODE_NEW) {
            editedFileExtension = null;
        } else {
            editedFileExtension = getEditedItem().getText();
        }
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public int open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    public String getNewValue() {
        return editedFileExtension;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM);
        shell.setMinimumSize(new Point(150, 25));
        shell.setSize(238, 140);
        shell.setText(getText());
        shell.setLayout(new FormLayout());

        int x = getParent().getLocation().x + getParent().getSize().x / 2 - shell.getSize().x / 2;
        int y = getParent().getLocation().y + getParent().getSize().y / 2 - shell.getSize().y;
        shell.setLocation(x, y);

        /*
         * Input field(s)
         */

        decTxtFileExtension = new XDecoratedText(shell, SWT.BORDER);
        decTxtFileExtension.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent anEvent) {
                performCheckFileExtensions((Text)anEvent.getSource());
            }
        });
        FormData fd_txtFileExtension = new FormData();
        fd_txtFileExtension.top = new FormAttachment(0, 29);
        decTxtFileExtension.setLayoutData(fd_txtFileExtension);
        txtFileExtension = decTxtFileExtension.getControl();

        Label lblFileExtension = new Label(shell, SWT.NONE);
        fd_txtFileExtension.left = new FormAttachment(lblFileExtension, 25);
        FormData fd_lblFileExtension = new FormData();
        fd_lblFileExtension.top = new FormAttachment(0, 32);
        fd_lblFileExtension.left = new FormAttachment(0, 10);
        lblFileExtension.setLayoutData(fd_lblFileExtension);
        lblFileExtension.setText(Messages.TaskTagEditor_label_fileExtension);

        /*
         * Buttons
         */

        btnOk = new Button(shell, SWT.NONE);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performOK(anEvent);
            }
        });
        FormData fd_btnOk = new FormData();
        fd_btnOk.bottom = new FormAttachment(100, -10);
        btnOk.setLayoutData(fd_btnOk);
        btnOk.setText(Messages.TaskTagEditor_btnOK);

        btnCancel = new Button(shell, SWT.NONE);
        fd_btnOk.left = new FormAttachment(btnCancel, -83, SWT.LEFT);
        fd_btnOk.right = new FormAttachment(btnCancel, -6);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performCancel(anEvent);
            }
        });
        fd_txtFileExtension.right = new FormAttachment(btnCancel, 0, SWT.RIGHT);
        btnCancel.setText(Messages.TaskTagEditor_btnCancel);
        FormData fd_btnCancel = new FormData();
        fd_btnCancel.left = new FormAttachment(100, -93);
        fd_btnCancel.bottom = new FormAttachment(100, -10);
        fd_btnCancel.right = new FormAttachment(100, -10);
        btnCancel.setLayoutData(fd_btnCancel);

        shell.setDefaultButton(btnOk);

        initializeValues();
    }

    private void performCheckFileExtensions(Text aTextControl) {
        boolean isError = false;
        String tFileExtension = aTextControl.getText();
        if (StringHelper.isNullOrEmpty(tFileExtension)) {
            decTxtFileExtension.showDecoration(Messages.TaskTag_empty);
            isError = true;
        } else if (fileExtensionExists(tFileExtension)) {
            decTxtFileExtension.showDecoration(Messages.TaskTag_exists);
            isError = true;
        } else {
            decTxtFileExtension.hideDecoration();
            isError = false;
        }
        updateControlsEnablement(isError);
    }

    private boolean fileExtensionExists(String aFileExtension) {
        return fileExtensionsSet.contains(aFileExtension.toUpperCase()) && !aFileExtension.equalsIgnoreCase(editedFileExtension);
    }

    private void performOK(SelectionEvent anEvent) {

        editedFileExtension = txtFileExtension.getText();

        if (mode == MODE_NEW) {
            new TableItem(tblFileExtensions, SWT.NONE).setText(editedFileExtension);
        } else {
            getEditedItem().setText(editedFileExtension);
        }

        tblFileExtensions.redraw();
        result = SWT.OK;
        shell.dispose();
    }

    private TableItem getEditedItem() {
        return tblFileExtensions.getSelection()[FIRST_AND_ONLY_ITEM];
    }

    private void performCancel(SelectionEvent anEvent) {
        editedFileExtension = null;
        result = SWT.CANCEL;
        shell.dispose();
    }

    private void initializeValues() {
        if (mode == MODE_NEW) {
            txtFileExtension.setText(""); //$NON-NLS-1$
        } else {
            txtFileExtension.setText(editedFileExtension);
        }
    }

    private void updateControlsEnablement(boolean anIsError) {
        if (anIsError) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }
    }
}
