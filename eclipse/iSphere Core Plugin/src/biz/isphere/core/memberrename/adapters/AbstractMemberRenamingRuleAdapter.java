/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.memberrename.adapters;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractMemberRenamingRuleAdapter implements IMemberRenamingRuleAdapter {

    private IPreferenceStore preferenceStore;
    private List<IAdapterModificationListener> adapterModificationListeners;

    public AbstractMemberRenamingRuleAdapter() {
        this.adapterModificationListeners = new LinkedList<IAdapterModificationListener>();
    }

    public void addAdapterModificationListener(IAdapterModificationListener listener) {
        adapterModificationListeners.add(listener);
    }

    public void removeAdapterModificationListener(IAdapterModificationListener listener) {
        adapterModificationListeners.remove(listener);
    }

    protected void fireAdapterModificationListeners(TypedEvent event) {

        for (IAdapterModificationListener listener : adapterModificationListeners) {
            listener.changed(event);
        }
    }

    public void initializeDefaultPreferences(IPreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }

    protected void setValue(String property, String value) {
        preferenceStore.setValue(getKey(property), value.trim());
    }

    protected void setValue(String property, Integer value) {
        preferenceStore.setValue(getKey(property), value.toString());
    }

    protected void setValue(String property, Boolean value) {
        preferenceStore.setValue(getKey(property), value.toString());
    }

    public String getString(String property) {
        String value = preferenceStore.getString(getKey(property));
        return value.trim();
    }

    public int getInteger(String property) {
        String value = preferenceStore.getString(getKey(property));
        return IntHelper.tryParseInt(value, 0);
    }

    public boolean getBoolean(String property) {
        boolean value = preferenceStore.getBoolean(getKey(property));
        return value;
    }

    protected void setDefault(String property, String value) {
        preferenceStore.setDefault(getKey(property), value.trim());
    }

    protected void setDefault(String property, Integer value) {
        preferenceStore.setDefault(getKey(property), value.toString());
    }

    protected void setDefault(String property, Boolean value) {
        preferenceStore.setDefault(getKey(property), value.toString());
    }

    protected String getDefaultString(String property) {
        String value = preferenceStore.getDefaultString(getKey(property));
        return value.trim();
    }

    protected int getDefaultInteger(String property) {
        String value = preferenceStore.getDefaultString(getKey(property));
        return IntHelper.tryParseInt(value, 0);
    }

    protected boolean getDefaultBoolean(String property) {
        boolean value = preferenceStore.getDefaultBoolean(getKey(property));
        return value;
    }

    protected abstract String getId();

    protected String getHelpURL() {
        return null;
    }

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

        if (!StringHelper.isNullOrEmpty(getHelpURL())) {

            Link lnkHelp = new Link(mainArea, SWT.NONE);
            lnkHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
            lnkHelp.setText("<a>" + Messages.Help + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
            lnkHelp.pack();
            lnkHelp.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(getHelpURL());
                }
            });

        }

        return mainArea;
    }

    protected Text createNameText(Composite parent, String label, String tooltip) {

        createLabel(parent, label, tooltip);

        Text text = WidgetFactory.createNameText(parent);
        text.setToolTipText(tooltip);
        GridData gridData = new GridData();
        gridData.widthHint = 60;
        text.setLayoutData(gridData);

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fireAdapterModificationListeners(event);
            }
        });

        return text;
    }

    protected Text createText(Composite parent, String label, String tooltip) {

        createLabel(parent, label, tooltip);

        Text text = WidgetFactory.createText(parent);
        text.setToolTipText(tooltip);
        GridData gridData = new GridData();
        gridData.widthHint = 60;
        text.setLayoutData(gridData);

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fireAdapterModificationListeners(event);
            }
        });

        return text;
    }

    protected Text createInteger(Composite parent, String label, String tooltip, int maxDigits) {

        createLabel(parent, label, tooltip);

        Text text = WidgetFactory.createIntegerText(parent);
        text.setToolTipText(tooltip);
        GridData gridData = new GridData();
        gridData.widthHint = 60;
        text.setLayoutData(gridData);

        text.setTextLimit(maxDigits);

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                fireAdapterModificationListeners(event);
            }
        });

        return text;
    }

    protected Button createCheckbox(Composite parent, String label, String tooltip) {

        Button checkbox = WidgetFactory.createCheckbox(parent, label);
        checkbox.setToolTipText(tooltip);
        GridData gridData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        checkbox.setLayoutData(gridData);

        checkbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                fireAdapterModificationListeners(event);
            }
        });

        return checkbox;
    }

    private void createLabel(Composite parent, String text, String tooltip) {

        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData());
        label.setText(text);
        label.setToolTipText(tooltip);
    }
}
