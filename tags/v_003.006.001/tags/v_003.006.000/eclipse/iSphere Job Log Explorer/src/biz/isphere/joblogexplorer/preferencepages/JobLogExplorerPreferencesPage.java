/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.preferencepages;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.preferences.Preferences;
import biz.isphere.joblogexplorer.preferences.SeverityColor;

public class JobLogExplorerPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

    private Button checkboxEnableColoring;
    private Group groupColors;
    private ColorSelector buttonSeverityBL;
    private ColorSelector buttonSeverity00;
    private ColorSelector buttonSeverity10;
    private ColorSelector buttonSeverity20;
    private ColorSelector buttonSeverity30;
    private ColorSelector buttonSeverity40;
    private Combo comboDateFormat;

    public JobLogExplorerPreferencesPage() {
        super();
        setPreferenceStore(ISphereJobLogExplorerPlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout());
        main.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        createSectionCommon(main);
        createGroupColors(main);
        createGroupParserSettings(main);

        setScreenToValues();

        return main;
    }

    private void createSectionCommon(Composite parent) {

        checkboxEnableColoring = WidgetFactory.createCheckbox(parent);
        checkboxEnableColoring.setText(Messages.Enable_coloring);
        checkboxEnableColoring.setToolTipText(Messages.Enable_coloring_tooltip);
        checkboxEnableColoring.setLayoutData(createLayoutData(1));
        checkboxEnableColoring.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                validateAll();
                setControlsEnablement();
            }
        });
    }

    private void createGroupColors(Composite parent) {

        groupColors = new Group(parent, SWT.NONE);
        groupColors.setText("Messages.Parser_Settings");
        groupColors.setLayout(new GridLayout(2, false));
        groupColors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        buttonSeverityBL = createColorSelector(groupColors, Messages.Severity_BL_colon);
        buttonSeverityBL.getButton().setToolTipText(Messages.Severity_BLANK_tooltip);
        buttonSeverity00 = createColorSelector(groupColors, Messages.Severity_00_colon);
        buttonSeverity00.getButton().setToolTipText(Messages.bind(Messages.Severity_A_to_B_tooltip, new String[] { "00", "09" })); //$NON-NLS-1$ //$NON-NLS-2$
        buttonSeverity10 = createColorSelector(groupColors, Messages.Severity_10_colon);
        buttonSeverity10.getButton().setToolTipText(Messages.bind(Messages.Severity_A_to_B_tooltip, new String[] { "10", "19" })); //$NON-NLS-1$ //$NON-NLS-2$
        buttonSeverity20 = createColorSelector(groupColors, Messages.Severity_20_colon);
        buttonSeverity20.getButton().setToolTipText(Messages.bind(Messages.Severity_A_to_B_tooltip, new String[] { "20", "29" })); //$NON-NLS-1$ //$NON-NLS-2$
        buttonSeverity30 = createColorSelector(groupColors, Messages.Severity_30_colon);
        buttonSeverity30.getButton().setToolTipText(Messages.bind(Messages.Severity_A_to_B_tooltip, new String[] { "30", "39" })); //$NON-NLS-1$ //$NON-NLS-2$
        buttonSeverity40 = createColorSelector(groupColors, Messages.Severity_40_colon);
        buttonSeverity40.getButton().setToolTipText(Messages.bind(Messages.Severity_A_to_B_tooltip, new String[] { "40", "99" })); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void createGroupParserSettings(Composite parent) {

        Group groupParserSettings = new Group(parent, SWT.NONE);
        groupParserSettings.setText(Messages.Parser_settings);
        groupParserSettings.setLayout(new GridLayout(2, false));
        groupParserSettings.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label labelDateFormat = new Label(groupParserSettings, SWT.NONE);
        labelDateFormat.setText(Messages.Date_format_colon);
        labelDateFormat.setToolTipText(Messages.Date_format_tooltip);

        comboDateFormat = WidgetFactory.createReadOnlyCombo(groupParserSettings);
        comboDateFormat.setItems(Preferences.getInstance().getJobLogDateFormats());
        GridData comboDateFormatLayoutData = new GridData();
        comboDateFormatLayoutData.widthHint = 80;
        comboDateFormat.setLayoutData(comboDateFormatLayoutData);
        comboDateFormat.setToolTipText(Messages.Date_format_tooltip);
    }

    private ColorSelector createColorSelector(Group parent, String label) {

        new Label(groupColors, SWT.NONE).setText(label);

        ColorSelector colorSelector = WidgetFactory.createColorSelector(parent);
        // Label labelSystemDefault = new Label(parent, SWT.NONE);
        // labelSystemDefault.setText("(system default)");
        // colorSelector.getButton().setData(labelSystemDefault);
        // colorSelector.addListener(new ColorChangeListener(colorSelector));

        return colorSelector;
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

        preferences.setColoringEnabled(checkboxEnableColoring.getSelection());
        preferences.setColorSeverity(SeverityColor.SEVERITY_BL, buttonSeverityBL.getColorValue());
        preferences.setColorSeverity(SeverityColor.SEVERITY_00, buttonSeverity00.getColorValue());
        preferences.setColorSeverity(SeverityColor.SEVERITY_10, buttonSeverity10.getColorValue());
        preferences.setColorSeverity(SeverityColor.SEVERITY_20, buttonSeverity20.getColorValue());
        preferences.setColorSeverity(SeverityColor.SEVERITY_30, buttonSeverity30.getColorValue());
        preferences.setColorSeverity(SeverityColor.SEVERITY_40, buttonSeverity40.getColorValue());

        preferences.setJobLogDateFormat(comboDateFormat.getText());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = Preferences.getInstance();

        checkboxEnableColoring.setSelection(preferences.isColoringEnabled());
        buttonSeverityBL.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_BL).getRGB());
        buttonSeverity00.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_00).getRGB());
        buttonSeverity10.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_10).getRGB());
        buttonSeverity20.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_20).getRGB());
        buttonSeverity30.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_30).getRGB());
        buttonSeverity40.setColorValue(preferences.getColorSeverity(SeverityColor.SEVERITY_40).getRGB());

        comboDateFormat.setText(preferences.getJobLogDateFormat());

        validateAll();
        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = Preferences.getInstance();

        checkboxEnableColoring.setSelection(preferences.getDefaultColoringEnabled());
        buttonSeverityBL.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_BL));
        buttonSeverity00.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_00));
        buttonSeverity10.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_10));
        buttonSeverity20.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_20));
        buttonSeverity30.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_30));
        buttonSeverity40.setColorValue(preferences.getDefaultColorSeverity(SeverityColor.SEVERITY_40));

        comboDateFormat.setText(preferences.getDefaultJobLogDateFormat());

        validateAll();
        setControlsEnablement();
    }

    private boolean validateAll() {

        return clearError();
    }

    private void setControlsEnablement() {

        if (checkboxEnableColoring.getSelection()) {
            setCompositeEnablement(groupColors, true);
        } else {
            setCompositeEnablement(groupColors, false);
        }
    }

    private void setCompositeEnablement(Composite composite, boolean enabled) {

        composite.setEnabled(enabled);

        for (Control control : composite.getChildren()) {
            control.setEnabled(enabled);
        }
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
        return createLayoutData(1);
    }

    private GridData createLayoutData(int horizontalSpan) {
        return new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, horizontalSpan, 1);
    }

    private GridData createColorButtonLayoutData() {

        GridData gridData = new GridData(60, SWT.DEFAULT);

        return gridData;
    }

    // private class ColorChangeListener implements IPropertyChangeListener {
    //
    // private ColorSelector selector;
    //
    // public ColorChangeListener(ColorSelector selector) {
    // this.selector = selector;
    //
    // performPropertyChange();
    // }
    //
    // @Override
    // public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    // {
    //
    // performPropertyChange();
    // }
    //
    // private void performPropertyChange() {
    //
    // RGB currentRGB = selector.getColorValue();
    // RGB backgroundRGB = ColorHelper.getDefaultBackgroundColor().getRGB();
    // Label label = (Label)selector.getButton().getData();
    // if (backgroundRGB == null || !backgroundRGB.equals(currentRGB)) {
    // label.setVisible(false);
    // } else {
    // label.setVisible(true);
    // }
    // }
    //
    // }
}
