/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.adapters;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractMemberRenamingRuleAdapter implements IMemberRenamingRuleAdapter {

    private IPreferenceStore preferenceStore;

    public void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    protected void setValue(String property, String value) {
        preferenceStore.setValue(getKey(property), value);
    }

    public String getString(String property) {
        String value = preferenceStore.getString(getKey(property));
        return value;
    }

    public int getInteger(String property) {
        String value = preferenceStore.getString(getKey(property));
        return IntHelper.tryParseInt(value, 0);
    }

    protected void setDefault(String property, String value) {
        preferenceStore.setDefault(getKey(property), value);
    }

    protected String getDefaultString(String property) {
        String value = preferenceStore.getDefaultString(getKey(property));
        return value;
    }

    protected int getDefaultInteger(String property) {
        String value = preferenceStore.getDefaultString(getKey(property));
        return IntHelper.tryParseInt(value, 0);
    }

    protected abstract String getId();

    private String getKey(String property) {
        return getId() + "." + property; //$NON-NLS-1$
    }

    /**
     * Produces a composite with the configuration properties of the adapter.
     * This message should be overwritten by sub-classes.
     */
    public Composite createComposite(Composite parent) {
        return createMainArea(parent);
    }

    protected Composite createMainArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return mainArea;
    }

    protected Text createText(Composite parent, String label) {

        createLabel(parent, label);

        Text text = WidgetFactory.createText(parent);
        GridData gridData = new GridData();
        gridData.widthHint = 60;
        text.setLayoutData(gridData);

        return text;
    }

    protected Text createInteger(Composite parent, String label, int maxLength) {

        createLabel(parent, label);

        Text text = WidgetFactory.createIntegerText(parent);
        GridData gridData = new GridData();
        gridData.widthHint = 60;
        text.setLayoutData(gridData);

        text.setTextLimit(maxLength);

        return text;
    }

    private void createLabel(Composite parent, String text) {

        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText(text);
    }
}
