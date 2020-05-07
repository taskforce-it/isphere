/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.filter.RSEFilterPool;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class FilterDialog extends XDialog {

    public static final String FILTER_POOL = "FILTER_POOL"; //$NON-NLS-1$
    public static final String FILTER_UPDATE_TYPE = "FILTER_UPDATE_TYPE"; //$NON-NLS-1$

    private Combo cbFilter;
    private Combo cbFilterPool;
    private Group gpUpdateType;
    private Button rbAdd;
    private Button rbReplace;

    private String filter = null;
    private String filterPool = null;
    private FilterUpdateType filterUpdateType = null;

    private String filterType;
    private RSEFilterPool[] filterPools;
    private String[] filterPoolNames;

    public FilterDialog(Shell parentShell, String filterType) {
        super(parentShell);

        setFilterType(filterType);
        setFilterPools(new RSEFilterPool[0]);
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public void setFilterPools(RSEFilterPool[] filterPools) {
        this.filterPools = filterPools;
        this.filterPoolNames = new String[filterPools.length];
        for (int i = 0; i < filterPools.length; i++) {
            filterPoolNames[i] = filterPools[i].getName();
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Composite compositeFilter = new Composite(container, SWT.NONE);
        compositeFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeFilter.setLayout(new GridLayout(2, false));

        Label labelFilterPool = new Label(compositeFilter, SWT.NONE);
        labelFilterPool.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        labelFilterPool.setText(Messages.FilterPool_colon);

        cbFilterPool = WidgetFactory.createReadOnlyCombo(compositeFilter);
        cbFilterPool.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        cbFilterPool.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                updateFilterNames();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        Label labelFilter = new Label(compositeFilter, SWT.NONE);
        labelFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        labelFilter.setText(Messages.Filter_colon);

        cbFilter = WidgetFactory.createCombo(compositeFilter);
        cbFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        cbFilter.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                setUpdateTypeEnablement();
            }
        });

        new Composite(compositeFilter, SWT.NONE).setVisible(false);

        gpUpdateType = new Group(compositeFilter, SWT.NONE);
        gpUpdateType.setLayout(new GridLayout(1, false));
        gpUpdateType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        rbAdd = WidgetFactory.createRadioButton(gpUpdateType);
        rbAdd.setLayoutData(new GridData());
        rbAdd.setText(Messages.Add_filter_strings);
        rbAdd.addSelectionListener(new FilterUpdateTypeSelectionListener());

        rbReplace = WidgetFactory.createRadioButton(gpUpdateType);
        rbReplace.setLayoutData(new GridData());
        rbReplace.setText(Messages.Replace_filter_strings);
        rbReplace.addSelectionListener(new FilterUpdateTypeSelectionListener());

        createStatusLine(container);

        loadScreenValues();

        return container;
    }

    private void setUpdateTypeEnablement() {

        boolean enabled = false;

        int i = cbFilterPool.getSelectionIndex();
        if (i >= 0 && i < filterPools.length) {

            RSEFilterPool filterPool = filterPools[i];
            String filterName = cbFilter.getText();

            for (String filter : filterPool.getFilterNames(null)) {
                if (filter.equalsIgnoreCase(filterName)) {
                    enabled = true;
                }
            }
        }

        gpUpdateType.setEnabled(enabled);
        rbAdd.setEnabled(enabled);
        rbReplace.setEnabled(enabled);
    }

    @Override
    public void setFocus() {
        cbFilter.setFocus();
    }

    private void updateFilterNames() {

        int index = cbFilterPool.getSelectionIndex();
        if (index < 0 || index >= filterPools.length) {
            cbFilter.setItems(new String[0]);
            cbFilter.setText(""); //$NON-NLS-1$
            return;
        }

        RSEFilterPool filterPool = filterPools[index];
        String[] filters = filterPool.getFilterNames(filterType);

        cbFilter.setItems(filters);
    }

    private int findDefaultFilterPool(RSEFilterPool[] filterPools) {

        for (int i = 0; i < filterPools.length; i++) {
            if (filterPools[i].isDefault()) {
                return i;
            }
        }

        return -1;
    }

    private int findFilterPool(String name) {

        for (int i = 0; i < filterPoolNames.length; i++) {
            if (name.equalsIgnoreCase(filterPoolNames[i])) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, Messages.OK, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Specify_a_filter);
    }

    @Override
    protected void okPressed() {

        cbFilter.setText(cbFilter.getText().trim());

        if (getFilterUpdateType() == FilterUpdateType.REPLACE) {
            if (!confirmReplace(cbFilter.getText())) {
                return;
            }
        }

        if (!Validator.validateFile(cbFilter.getText())) {
            setErrorMessage(Messages.The_value_in_field_Filter_is_not_valid);
            cbFilter.setFocus();
            return;
        }

        filterPool = cbFilterPool.getText();
        filter = cbFilter.getText();

        storeScreenValues();

        super.okPressed();

    }

    private boolean confirmReplace(String filterName) {

        if (MessageDialog.openQuestion(getShell(), Messages.Question,
            Messages.bind(Messages.Are_you_sure_to_replace_all_filter_strings_of_filter_A, filterName))) {
            return true;
        }

        return false;
    }

    public String getFilterPool() {
        return filterPool;
    }

    public String getFilter() {
        return filter;
    }

    public FilterUpdateType getFilterUpdateType() {

        return filterUpdateType;
    }

    private void loadScreenValues() {

        cbFilterPool.setItems(filterPoolNames);

        String filterPool = getDialogBoundsSettings().get(FILTER_POOL);
        int index;
        if (StringHelper.isNullOrEmpty(filterPool)) {
            index = findDefaultFilterPool(filterPools);
        } else {
            index = findFilterPool(filterPool);
        }

        cbFilterPool.select(index);

        String filterUpdateTypeString = getDialogBoundsSettings().get(FILTER_UPDATE_TYPE);
        if (FilterUpdateType.REPLACE.name().equalsIgnoreCase(filterUpdateTypeString)) {
            rbReplace.setSelection(true);
            filterUpdateType = FilterUpdateType.REPLACE;
        } else if (FilterUpdateType.ADD.name().equalsIgnoreCase(filterUpdateTypeString)) {
            rbAdd.setSelection(true);
            filterUpdateType = FilterUpdateType.ADD;
        } else {
            rbAdd.setSelection(true);
            filterUpdateType = FilterUpdateType.ADD;
        }

        updateFilterNames();
    }

    private void storeScreenValues() {

        getDialogBoundsSettings().put(FILTER_POOL, filterPool);

        if (rbReplace.getSelection()) {
            getDialogBoundsSettings().put(FILTER_UPDATE_TYPE, FilterUpdateType.REPLACE.name());
        } else if (rbAdd.getSelection()) {
            getDialogBoundsSettings().put(FILTER_UPDATE_TYPE, FilterUpdateType.ADD.name());
        }
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(250), SWT.DEFAULT, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class FilterUpdateTypeSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent event) {
            if (rbAdd.getSelection()) {
                filterUpdateType = FilterUpdateType.ADD;
            } else if (rbReplace.getSelection()) {
                filterUpdateType = FilterUpdateType.REPLACE;
            } else {
                filterUpdateType = null;
            }
        }

        public void widgetDefaultSelected(SelectionEvent event) {
            widgetSelected(event);
        }
    }
}
