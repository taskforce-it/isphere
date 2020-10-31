/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.ui.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import biz.isphere.adapter.swt.widgets.XFileDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.lpex.tasktags.Messages;
import biz.isphere.lpex.tasktags.preferences.Preferences;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

    private static final String[] IMPORT_FILE_EXTENSIONS = new String[] { "*.properties", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

    private Table tblFileExtensions;

    private Button btnNew;

    private Button btnEdit;

    private Button btnRemove;

    private Button chkEnableLPEXTaskTags;

    private Composite parent;

    private Button btnExport;

    private Button btnImport;

    /**
     * Create the preference page.
     */
    public PreferencesPage() {
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        return;
    }

    @Override
    protected Control createContents(Composite aParent) {

        parent = aParent;

        setTitle(Messages.PreferencesPage_title);

        Composite mainPanel = new Composite(parent, SWT.NONE);
        GridLayout gl_mainPanel = new GridLayout(2, false);
        gl_mainPanel.marginBottom = 10;
        gl_mainPanel.marginWidth = 0;
        gl_mainPanel.horizontalSpacing = 2;
        gl_mainPanel.verticalSpacing = 4;
        mainPanel.setLayout(gl_mainPanel);

        String headline = " <a href=\"org.eclipse.jdt.ui.preferences.TodoTaskPreferencePage\">" + Messages.PreferencesPage_configureTaskTags + "</a>"; //$NON-NLS-1$ //$NON-NLS-2$
        Link lnkJavaTaskTags = new Link(mainPanel, SWT.NONE);
        lnkJavaTaskTags.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        lnkJavaTaskTags.setText(headline);
        lnkJavaTaskTags.pack();
        lnkJavaTaskTags.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), e.text, null, null);
            }
        });

        Link lnkHelp = new Link(mainPanel, SWT.NONE);
        lnkHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
        lnkHelp.setText("<a>" + Messages.PreferencesPage_help + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        lnkHelp.pack();
        lnkHelp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/biz.isphere.lpex.tasks.help/html/tasktags/tasktags.html"); //$NON-NLS-1$
            }
        });

        Label lnkHeadline = new Label(mainPanel, SWT.WRAP);
        lnkHeadline.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        lnkHeadline.setText(Messages.PreferencesPage_headline);

        Composite tblComposite = new Composite(mainPanel, SWT.NONE);
        tblComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tblComposite.setLayout(new GridLayout(1, false));

        tblFileExtensions = new Table(tblComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData gd_tblFileExtensions = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_tblFileExtensions.heightHint = 0;
        tblFileExtensions.setLayoutData(gd_tblFileExtensions);
        tblFileExtensions.setHeaderVisible(true);
        tblFileExtensions.setLinesVisible(true);
        tblFileExtensions.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent anEvent) {
                updateControlsEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent anEvent) {
                performEdit(anEvent);
            }
        });

        TableColumn tblclmnFileExtension = new TableColumn(tblFileExtensions, SWT.NONE);
        tblclmnFileExtension.setWidth(200);
        tblclmnFileExtension.setText(Messages.PreferencesPage_tableHeadline);

        Composite btnComposite = new Composite(mainPanel, SWT.NONE);
        RowLayout rl_btnComposite = new RowLayout(SWT.VERTICAL);
        rl_btnComposite.wrap = false;
        rl_btnComposite.fill = true;
        rl_btnComposite.pack = false;
        rl_btnComposite.marginBottom = 0;
        rl_btnComposite.marginTop = 0;
        rl_btnComposite.marginRight = 0;
        btnComposite.setLayout(rl_btnComposite);
        btnComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        btnNew = WidgetFactory.createPushButton(btnComposite);
        btnNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performNew(anEvent);
            }
        });
        btnNew.setText(Messages.PreferencesPage_btnNew);

        btnEdit = WidgetFactory.createPushButton(btnComposite);
        btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performEdit(anEvent);
            }
        });
        btnEdit.setText(Messages.PreferencesPage_btnEdit);

        btnRemove = WidgetFactory.createPushButton(btnComposite);
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performRemove(anEvent);
            }
        });
        btnRemove.setText(Messages.PreferencesPage_btnRemove);
        btnRemove.setLayoutData(new RowData(76, SWT.DEFAULT));

        new Label(btnComposite, SWT.HORIZONTAL);

        btnExport = WidgetFactory.createPushButton(btnComposite);
        btnExport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performExport(anEvent);
            }
        });
        btnExport.setText(Messages.PreferencesPage_btnExport);

        btnImport = WidgetFactory.createPushButton(btnComposite);
        btnImport.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent anEvent) {
                performImport(anEvent);
            }
        });
        btnImport.setText(Messages.PreferencesPage_btnImport);

        chkEnableLPEXTaskTags = WidgetFactory.createCheckbox(mainPanel);
        chkEnableLPEXTaskTags.setText(Messages.PreferencesPage_btnEnableTaskTags);
        new Label(mainPanel, SWT.NONE);
        new Label(mainPanel, SWT.NONE);
        new Label(mainPanel, SWT.NONE);
        new Label(mainPanel, SWT.NONE);
        new Label(mainPanel, SWT.NONE);

        initializeValues();

        return mainPanel;
    }

    private void initializeValues() {
        chkEnableLPEXTaskTags.setSelection(getPreferences().isEnabled());
        setFileExtensionsArray(getPreferences().getFileExtensions());

        updateControlsEnablement();
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        chkEnableLPEXTaskTags.setSelection(getPreferences().getDefaultEnabledState());
        setFileExtensionsArray(getPreferences().getDefaultFileExtensions());

        updateControlsEnablement();
    }

    @Override
    public boolean performOk() {
        if (!super.performOk()) {
            return false;
        }

        Preferences tPreferences = getPreferences();

        tPreferences.setEnabled(chkEnableLPEXTaskTags.getSelection());
        tPreferences.setFileExtensions(getFileExtensionsArray());

        updateControlsEnablement();

        return true;
    }

    private String[] getFileExtensionsArray() {
        TableItem[] tItems = tblFileExtensions.getItems();
        String[] tFileExtensions = new String[tItems.length];
        for (int i = 0; i < tItems.length; i++) {
            tFileExtensions[i] = tItems[i].getText();
        }
        return tFileExtensions;
    }

    private void setFileExtensionsArray(String[] aFileExtensions) {
        tblFileExtensions.removeAll();
        Arrays.sort(aFileExtensions);
        for (String tExtension : aFileExtensions) {
            new TableItem(tblFileExtensions, SWT.NONE).setText(tExtension);
        }
    }

    private void updateControlsEnablement() {

        btnNew.setEnabled(true);
        btnImport.setEnabled(true);

        if (tblFileExtensions.getSelectionCount() == 1) {
            btnEdit.setEnabled(true);
        } else {
            btnEdit.setEnabled(false);
        }

        if (tblFileExtensions.getSelectionCount() > 0) {
            btnRemove.setEnabled(true);
        } else {
            btnRemove.setEnabled(false);
        }

        if (tblFileExtensions.getItems().length > 0) {
            btnExport.setEnabled(true);
        } else {
            btnExport.setEnabled(false);
        }

        chkEnableLPEXTaskTags.setEnabled(true);
    }

    private void performNew(SelectionEvent anEvent) {
        TaskTagEditor tEditor = TaskTagEditor.getEditorForNew(getShell(), tblFileExtensions);
        if (tEditor.open() == SWT.OK) {
            refreshTableItems();
            selectTableItem(tEditor.getNewValue());
        }

        updateControlsEnablement();
    }

    private void performEdit(SelectionEvent anEvent) {
        TaskTagEditor tEditor = TaskTagEditor.getEditorForEdit(getShell(), tblFileExtensions);
        if (tEditor.open() == SWT.OK) {
            refreshTableItems();
            selectTableItem(tEditor.getNewValue());
        }
    }

    private void refreshTableItems() {

        TableItem[] items = tblFileExtensions.getItems();
        String[] fileExtensions = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            fileExtensions[i] = items[i].getText();
        }
        setFileExtensionsArray(fileExtensions);
    }

    private void selectTableItem(String value) {

        tblFileExtensions.setSelection(-1);

        if (value != null) {
            for (int i = 0; i < tblFileExtensions.getItemCount(); i++) {
                if (tblFileExtensions.getItem(i).getText().equals(value)) {
                    tblFileExtensions.setSelection(i);
                    return;
                }
            }
        }
    }

    private void performRemove(SelectionEvent anEvent) {
        if (tblFileExtensions.getSelectionCount() <= 0) {
            return;
        }
        tblFileExtensions.remove(tblFileExtensions.getSelectionIndices());
        tblFileExtensions.redraw();

        updateControlsEnablement();
    }

    private void performExport(SelectionEvent anEvent) {
        XFileDialog tFileDialog = new XFileDialog(getShell(), SWT.SAVE);
        tFileDialog.setText(Messages.PreferencesPage_ExportDialog_headline);
        tFileDialog.setFileName("LPEXTaskTags"); //$NON-NLS-1$
        tFileDialog.setFilterPath(getPreferences().getImportExportLocation());
        tFileDialog.setFilterExtensions(IMPORT_FILE_EXTENSIONS);
        tFileDialog.setFilterIndex(0);
        tFileDialog.setOverwrite(true);

        String tExportFile = tFileDialog.open();
        if (tExportFile == null) {
            return;
        }

        if (exportTaskTags(tExportFile, getFileExtensionsArray())) {
            getPreferences().setImportExportLocation(tExportFile);
        }
    }

    private void performImport(SelectionEvent anEvent) {
        XFileDialog fileDialog = new XFileDialog(getShell(), SWT.OPEN);
        fileDialog.setText(Messages.PreferencesPage_ImportDialog_headline);
        fileDialog.setFileName(""); //$NON-NLS-1$
        fileDialog.setFilterPath(getPreferences().getImportExportLocation());
        fileDialog.setFilterExtensions(IMPORT_FILE_EXTENSIONS);
        fileDialog.setFilterIndex(0);

        String location = fileDialog.open();
        if (location != null) {
            importTaskTags(location);
            updateControlsEnablement();
        }
    }

    private boolean exportTaskTags(String aLocation, String[] aFileExtensions) {

        try {
            Properties tExportData = new Properties();
            for (String tItem : aFileExtensions) {
                tExportData.put(tItem, ""); //$NON-NLS-1$
            }

            FileOutputStream tOutStream = new FileOutputStream(aLocation);
            tExportData.store(tOutStream, "LPEX Task-Tags"); //$NON-NLS-1$
            tOutStream.flush();
            tOutStream.close();
            return true;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }

        return false;
    }

    private boolean importTaskTags(String aLocation) {

        try {
            Properties tImportData = new Properties();
            FileInputStream tInputStream = new FileInputStream(aLocation);
            tImportData.load(tInputStream);
            tInputStream.close();

            ArrayList<String> tList = new ArrayList<String>();
            for (Object tItem : tImportData.keySet()) {
                tList.add((String)tItem);
            }
            setFileExtensionsArray(tList.toArray(new String[tList.size()]));
            return true;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
        }

        return false;
    }

    private Preferences getPreferences() {
        return Preferences.getInstance();
    }
}