/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.progress.UIJob;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.SearchForUpdates;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereUpdates extends PreferencePage implements IWorkbenchPreferencePage, IJobChangeListener {

    private Button buttonSearchForUpdates;
    private Button buttonSearchForBetaVersions;
    private boolean searchForUpdates;
    private boolean searchForBetaVersions;
    private Text textURLForUpdates;
    private String urlForUpdates;
    private Button buttonStartSearchForUpdates;
    private Display display;
    private boolean isSearchingForUpdates;

    public ISphereUpdates() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        container.setLayout(gridLayout);

        final Label labelSearchForUpdates = new Label(container, SWT.NONE);
        labelSearchForUpdates.setText(Messages.Search_for_updates + ":");

        buttonSearchForUpdates = WidgetFactory.createCheckbox(container);
        buttonSearchForUpdates.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent selectionEvent) {
                searchForUpdates = buttonSearchForUpdates.getSelection();
                setButtonEnablement();
                checkError();
            }
        });

        final Label labelSearchForBetaVersions = new Label(container, SWT.NONE);
        labelSearchForBetaVersions.setText(Messages.Search_for_beta_versions + ":");

        buttonSearchForBetaVersions = WidgetFactory.createCheckbox(container);
        buttonSearchForBetaVersions.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent selectionEvent) {
                searchForBetaVersions = buttonSearchForBetaVersions.getSelection();
                setButtonEnablement();
                checkError();
            }
        });

        final Label labelURLForUpdates = new Label(container, SWT.NONE);
        labelURLForUpdates.setText(Messages.URL_for_updates + ":");

        textURLForUpdates = WidgetFactory.createText(container);
        textURLForUpdates.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                urlForUpdates = textURLForUpdates.getText().trim();
                setButtonEnablement();
                checkError();
            }
        });
        textURLForUpdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textURLForUpdates.setTextLimit(256);

        buttonStartSearchForUpdates = WidgetFactory.createPushButton(container);
        buttonStartSearchForUpdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonStartSearchForUpdates.setText(Messages.Search_for_updates);
        buttonStartSearchForUpdates.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                
                SearchForUpdates search;
                try {
                    search = new SearchForUpdates(new URL(urlForUpdates), true, searchForBetaVersions);
                    search.addJobChangeListener(ISphereUpdates.this);
                    search.setUser(false);
                    search.schedule();
                } catch (MalformedURLException e1) {
                    MessageDialog.openError(getShell(), Messages.E_R_R_O_R, "");
                }
            }
        });

        setScreenToValues();

        display = ISphereUpdates.this.getShell().getDisplay();
        
        return container;
    }

    private void checkError() {
        
        if (validateUpdateURL()) {
            setErrorMessage(null);
            setValid(true);
        } else {
            setErrorMessage(Messages.The_value_in_field_URL_for_updates_is_not_valid);
            setValid(false);
        }
        
        setButtonEnablement();
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        setStoreToValues();
        return super.performOk();
    }

    @Override
    public boolean isValid() {
        
        if (!validateUpdateURL()) {
            return false;
        }
        
        return super.isValid();
    }

    protected void setStoreToValues() {
        Preferences.getInstance().setSearchForUpdates(searchForUpdates);
        Preferences.getInstance().setSearchForBetaVersions(searchForBetaVersions);
        Preferences.getInstance().setURLForUpdates(urlForUpdates);
    }

    protected void setScreenToValues() {
        searchForUpdates = Preferences.getInstance().isSearchForUpdates();
        searchForBetaVersions = Preferences.getInstance().isSearchForBetaVersions();
        urlForUpdates = Preferences.getInstance().getURLForUpdates();
        setScreenValues();
    }

    protected void setScreenToDefaultValues() {
        searchForUpdates = Preferences.getInstance().getDefaultSearchForUpdates();
        searchForBetaVersions = Preferences.getInstance().getDefaultSearchForBetaVersions();
        urlForUpdates = Preferences.getInstance().getDefaultURLForUpdates();
        setScreenValues();
    }

    protected void setScreenValues() {
        buttonSearchForUpdates.setSelection(searchForUpdates);
        buttonSearchForBetaVersions.setSelection(searchForBetaVersions);
        textURLForUpdates.setText(urlForUpdates);
        setButtonEnablement();
        setErrorMessage(null);
        setValid(true);
    }

    private void setButtonEnablement() {

        if (validateUpdateURL() && !isSearchingForUpdates) {
            buttonStartSearchForUpdates.setEnabled(true);
        } else {
            buttonStartSearchForUpdates.setEnabled(false);
        }

        if (buttonSearchForUpdates.getSelection()) {
            buttonSearchForBetaVersions.setEnabled(true);
        } else {
            buttonSearchForBetaVersions.setEnabled(false);
        }
    }

    protected boolean validateUpdateURL() {
        
        if (StringHelper.isNullOrEmpty(urlForUpdates)) {
            return false;
        }
        
        try {
            new URL(urlForUpdates);
        } catch (MalformedURLException e) {
            return false;
        }
        
        return true;
    }

    public void init(IWorkbench workbench) {

        isSearchingForUpdates = false;
    }

    /*
     * IJobChangeListener methods
     */

    public void aboutToRun(IJobChangeEvent event) {
    }

    public void awake(IJobChangeEvent event) {
    }

    public void done(IJobChangeEvent event) {
        UIJob job = new UIJob(display, "") {

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                isSearchingForUpdates = false;
                setButtonEnablement();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    public void running(IJobChangeEvent event) {
    }

    public void scheduled(IJobChangeEvent event) {
        isSearchingForUpdates = true;
        setButtonEnablement();
    }

    public void sleeping(IJobChangeEvent event) {
    }

}