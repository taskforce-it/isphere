/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.tn5250j.core.Messages;
import biz.isphere.tn5250j.core.TN5250JCorePlugin;
import biz.isphere.tn5250j.core.preferences.Preferences;

/**
 * 5250 preferences page: Minimal session size
 */
public class PreferencePage3 extends PreferencePage implements IWorkbenchPreferencePage {

    private Preferences preferences;
    private Button buttonMSActive;
    private Text textMSHorizontalSize;
    private Text textMSVerticalSize;

    public PreferencePage3() {
        super();
        setPreferenceStore(TN5250JCorePlugin.getDefault().getPreferenceStore());
        preferences = Preferences.getInstance();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayoutx = new GridLayout();
        gridLayoutx.numColumns = 2;
        container.setLayout(gridLayoutx);

        // Minimal size - Active

        final Label labelMSActive = new Label(container, SWT.NONE);
        labelMSActive.setText(Messages.Active_colon);

        buttonMSActive = new Button(container, SWT.CHECK);

        // Minimal size - Horizontal size

        final Label labelMSHorizontalSize = new Label(container, SWT.NONE);
        labelMSHorizontalSize.setText(Messages.Horizontal_size_colon);

        textMSHorizontalSize = new Text(container, SWT.BORDER);
        textMSHorizontalSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(textMSHorizontalSize.getText());
                    setErrorMessage(null);
                    setValid(true);
                } catch (NumberFormatException e1) {
                    setErrorMessage(Messages.The_value_in_field_Horizontal_size_is_not_valid);
                    setValid(false);
                }
            }
        });
        textMSHorizontalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMSHorizontalSize.setTextLimit(4);

        // Minimal size - Vertical size

        final Label labelMSVerticalSize = new Label(container, SWT.NONE);
        labelMSVerticalSize.setText(Messages.Vertical_size_colon);

        textMSVerticalSize = new Text(container, SWT.BORDER);
        textMSVerticalSize.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    Integer.parseInt(textMSVerticalSize.getText());
                    setErrorMessage(null);
                    setValid(true);
                } catch (NumberFormatException e1) {
                    setErrorMessage(Messages.The_value_in_field_Vertical_size_is_not_valid);
                    setValid(false);
                }
            }
        });
        textMSVerticalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        textMSVerticalSize.setTextLimit(4);

        // Miscellaneous

        setScreenToValues();

        return container;
    }

    @Override
    protected void performApply() {
        setStoreToValues();
        setScreenToValues();
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
        preferences.setIsMinimalSessionEnabled(buttonMSActive.getSelection());
        preferences.setMinimalSessionHorizontalSize(IntHelper.tryParseInt(textMSHorizontalSize.getText(),
            preferences.getDefaultMinimalSessionHorizontalSize()));
        preferences.setMinimalSessionVerticalSize(IntHelper.tryParseInt(textMSVerticalSize.getText(),
            preferences.getDefaultMinimalSessionVerticalSize()));
    }

    protected void setScreenToDefaultValues() {
        buttonMSActive.setSelection(preferences.getDefaultIsMinimalSessionSizeEnabled());
        textMSHorizontalSize.setText(Integer.toString(preferences.getDefaultMinimalSessionHorizontalSize()));
        textMSVerticalSize.setText(Integer.toString(preferences.getDefaultMinimalSessionVerticalSize()));
    }

    protected void setScreenToValues() {
        buttonMSActive.setSelection(preferences.isMinimalSessionSizeEnabled());
        textMSHorizontalSize.setText(Integer.toString(preferences.getMinimalSessionHorizontalSize()));
        textMSVerticalSize.setText(Integer.toString(preferences.getMinimalSessionVerticalSize()));
    }

    public void init(IWorkbench workbench) {
    }

}