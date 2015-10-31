/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereCompare extends PreferencePage implements IWorkbenchPreferencePage {

    private Text textMessageFileCompareLineWith;
    private boolean messageFileCompareEnabled;

    public ISphereCompare() {
        super();
        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (IBMiHostContributionsHandler.hasContribution()) {
            messageFileCompareEnabled = true;
        } else {
            messageFileCompareEnabled = false;
        }

    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionMessageFileCompare(container);

        setScreenToValues();

        return container;
    }

    private void createSectionMessageFileCompare(Composite parent) {

        /*
         * Does not work, because we cannot create an AS400 object, when loading
         * a search result.
         */
        if (!messageFileCompareEnabled) {
            return;
        }

        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        group.setText(Messages.Message_descriptions_compare);

        Label labelMessageFileSearchResultsAutoSaveFileName = new Label(group, SWT.NONE);
        labelMessageFileSearchResultsAutoSaveFileName.setLayoutData(createLabelLayoutData());
        labelMessageFileSearchResultsAutoSaveFileName.setText(Messages.Line_width_colon);

        textMessageFileCompareLineWith = WidgetFactory.createIntegerText(group);
        textMessageFileCompareLineWith
            .setToolTipText(Messages.Tooltip_Line_with_of_word_wrap_of_first_and_second_level_text_when_comparing_message_descriptions);
        textMessageFileCompareLineWith.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        textMessageFileCompareLineWith.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                if (validateMessageFileCompareLineWidth()) {
                    checkAllValues();
                }
            }
        });
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

        Preferences preferences = Preferences.getInstance();

        if (messageFileCompareEnabled) {
            int defaultLineWidth = Preferences.getInstance().getDefaultMessageFileCompareMinLineWidth();
            preferences.setMessageFileCompareLineWidth(IntHelper.tryParseInt(textMessageFileCompareLineWith.getText(), defaultLineWidth));
        }
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        if (messageFileCompareEnabled) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getMessageFileCompareLineWidth()));
        }

        checkAllValues();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        if (messageFileCompareEnabled) {
            textMessageFileCompareLineWith.setText(Integer.toString(preferences.getDefaultMessageFileCompareMinLineWidth()));
        }

        checkAllValues();
        setControlsEnablement();
    }

    private boolean validateMessageFileCompareLineWidth() {
        
        if (!messageFileCompareEnabled) {
            return true;
        }

        int minLineWidth = 15;

        int lineWidth = IntHelper.tryParseInt(textMessageFileCompareLineWith.getText(), minLineWidth);
        if (lineWidth < minLineWidth) {
            setError(Messages.bind(Messages.Minimum_line_width_is_A_characters, minLineWidth));
            return false;
        }

        return true;
    }

    private boolean checkAllValues() {

        if (!validateMessageFileCompareLineWidth()) {
            return false;
        }

        return clearError();
    }

    private void setControlsEnablement() {

    }

    private boolean setError(String message) {
        setErrorMessage(message);
        setValid(false);
        return false;
    }

    private boolean clearError() {
        setErrorMessage(null);
        setValid(true);
        return true;
    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
    }
}