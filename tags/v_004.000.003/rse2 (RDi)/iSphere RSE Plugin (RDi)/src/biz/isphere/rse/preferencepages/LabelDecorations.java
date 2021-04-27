/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;
import biz.isphere.rse.preferences.Preferences;

public class LabelDecorations extends PreferencePage implements IWorkbenchPreferencePage {

    private Button checkboxIsExtendedObjectDecoration;
    private Button checkboxIsExtendedSourceMemberDecoration;
    private Button checkboxIsExtendedDataMemberDecoration;

    public LabelDecorations() {
        super();
        setPreferenceStore(ISphereRSEPlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionLabelDecorations(container);

        setScreenToValues();

        return container;
    }

    private void createSectionLabelDecorations(Composite parent) {

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Label_Decorations_RSE_host_objects);

        String headline = Messages.bind(Messages.Label_Decorations_RSE_host_objects_Description, new String[] {
            "<a href=\"org.eclipse.ui.preferencePages.Decorators\">", "</a>" });

        Link lnkJavaTaskTags = new Link(group, SWT.MULTI | SWT.WRAP);
        lnkJavaTaskTags.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 3, 1));
        lnkJavaTaskTags.setText(headline);
        lnkJavaTaskTags.pack();
        lnkJavaTaskTags.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), e.text, null, null);
            }
        });

        Composite extendedDecorationsPanel = new Composite(parent, SWT.NONE);
        extendedDecorationsPanel.setLayout(new GridLayout(1, false));

        checkboxIsExtendedObjectDecoration = WidgetFactory.createCheckbox(extendedDecorationsPanel);
        checkboxIsExtendedObjectDecoration.setText(Messages.Add_library_name_to_objects);

        checkboxIsExtendedSourceMemberDecoration = WidgetFactory.createCheckbox(extendedDecorationsPanel);
        checkboxIsExtendedSourceMemberDecoration.setText(Messages.Add_library_and_file_name_to_source_members);

        checkboxIsExtendedDataMemberDecoration = WidgetFactory.createCheckbox(extendedDecorationsPanel);
        checkboxIsExtendedDataMemberDecoration.setText(Messages.Add_library_and_file_name_to_data_members);
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
        applyChangesToRSETree();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = Preferences.getInstance();

        preferences.setObjectDecorationExtension(checkboxIsExtendedObjectDecoration.getSelection());
        preferences.setSourceMemberDecorationExtension(checkboxIsExtendedSourceMemberDecoration.getSelection());
        preferences.setDataMemberDecorationExtension(checkboxIsExtendedDataMemberDecoration.getSelection());
    }

    protected void setScreenToValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxIsExtendedObjectDecoration.setSelection(preferences.isObjectDecorationExtension());
        checkboxIsExtendedSourceMemberDecoration.setSelection(preferences.isSourceMemberDecorationExtension());
        checkboxIsExtendedDataMemberDecoration.setSelection(preferences.isDataMemberDecorationExtension());

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxIsExtendedObjectDecoration.setSelection(preferences.getDefaultObjectDecorationExtension());
        checkboxIsExtendedSourceMemberDecoration.setSelection(preferences.getDefaultSourceMemberDecorationExtension());
        checkboxIsExtendedDataMemberDecoration.setSelection(preferences.getDefaultDataMemberDecorationExtension());

        checkAllValues();
        setControlsEnablement();
    }

    private boolean checkAllValues() {

        return clearError();
    }

    private void setControlsEnablement() {

    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

    private void applyChangesToRSETree() {

        IDecoratorManager manager = getDecoratorManager();
        manager.update("biz.isphere.rse.decorators.RSEHostObjectDecorator");

    }

    /**
     * Get the DecoratorManager being used for this page.
     * 
     * @return the decorator manager
     */
    private IDecoratorManager getDecoratorManager() {
        // return WorkbenchPlugin.getDefault().getDecoratorManager();
        return PlatformUI.getWorkbench().getDecoratorManager();
    }
}