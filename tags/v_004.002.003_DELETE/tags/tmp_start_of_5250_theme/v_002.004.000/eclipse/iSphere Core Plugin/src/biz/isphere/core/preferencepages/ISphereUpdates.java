/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.SearchForUpdates;
import biz.isphere.core.preferences.Preferences;

public class ISphereUpdates extends PreferencePage implements IWorkbenchPreferencePage {
	
	private Button buttonSearchForUpdates;
	private boolean searchForUpdates;
    private Text textURLForUpdates;
    private String urlForUpdates;
    private Button buttonStartSearchForUpdates;

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
		
		final Label labelCachingWarning = new Label(container, SWT.NONE);
		labelCachingWarning.setText(Messages.Search_for_updates + ":");

		buttonSearchForUpdates = new Button(container, SWT.CHECK);
        buttonSearchForUpdates.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent selectionEvent) {
                searchForUpdates = buttonSearchForUpdates.getSelection();
                checkError();
            }
        });

        final Label labelURLForUpdates = new Label(container, SWT.NONE);
        labelURLForUpdates.setText(Messages.URL_for_updates + ":");

        textURLForUpdates = new Text(container, SWT.BORDER);
        textURLForUpdates.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                urlForUpdates = textURLForUpdates.getText().trim();
                checkError();
            }
        });
        textURLForUpdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textURLForUpdates.setTextLimit(256);
        
        buttonStartSearchForUpdates = new Button(container, SWT.NONE);
        buttonStartSearchForUpdates.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        buttonStartSearchForUpdates.setText(Messages.Search_for_updates);
        buttonStartSearchForUpdates.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SearchForUpdates search = new SearchForUpdates(true);
                search.setUser(false);
                search.schedule();
            }
        });
        
		setScreenToValues();
		
		return container;
		
	}

    private void checkError() {
        if (urlForUpdates.equals("")) {
            setErrorMessage(Messages.The_value_in_field_URL_for_updates_is_not_valid);
            setValid(false);
            buttonStartSearchForUpdates.setEnabled(false);
        } 
        else {
            setErrorMessage(null);
            setValid(true);
            buttonStartSearchForUpdates.setEnabled(true);
        }
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
	
    protected void setStoreToValues() {
        Preferences.getInstance().setSearchForUpdates(searchForUpdates);
        Preferences.getInstance().setURLForUpdates(urlForUpdates);
    }

    protected void setScreenToValues() {
        searchForUpdates = Preferences.getInstance().isSearchForUpdates();
        urlForUpdates = Preferences.getInstance().getURLForUpdates();
        setScreenValues();
    }

    protected void setScreenToDefaultValues() {
        searchForUpdates = Preferences.getInstance().getDefaultSearchForUpdates();
        urlForUpdates = Preferences.getInstance().getDefaultURLForUpdates();
        setScreenValues();
    }

    protected void setScreenValues() {
        buttonSearchForUpdates.setSelection(searchForUpdates);
        textURLForUpdates.setText(urlForUpdates);
        setErrorMessage(null);
        setValid(true);
    }

    public void init(IWorkbench workbench) {
    }

}