/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.connectioncombo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ConnectionCombo extends Composite {

    private Combo connectionCombo;
    private String[] connections;

    public ConnectionCombo(Composite parent, int style) {
        super(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        super.setLayout(gridLayout);

        connectionCombo = WidgetFactory.createReadOnlyCombo(this, style | SWT.READ_ONLY | SWT.DROP_DOWN);
        connectionCombo.setLayoutData(new GridData(GridData.FILL_BOTH));

        connections = IBMiHostContributionsHandler.getConnectionNames();
        connectionCombo.setItems(connections);
    }

    public void addModifyListener(ModifyListener listener) {
        connectionCombo.addModifyListener(listener);
    }

    public void removeModifyListener(ModifyListener listener) {
        connectionCombo.removeModifyListener(listener);
    }

    @Override
    public void setLayout(Layout layout) {
        throw new IllegalAccessError();
    }

    @Override
    public void setLayoutData(Object layoutData) {
        if (!(layoutData instanceof GridData)) {
            throw new IllegalArgumentException("Parameter 'layoutData' is not of type 'GridData'."); //$NON-NLS-1$
        }
        super.setLayoutData(layoutData);
    }

    public String getText() {
        return connectionCombo.getText().trim();
    }

    public void setText(String text) {

        if (text == null) {
            connectionCombo.select(-1);
            connectionCombo.setText(""); //$NON-NLS-1$
            return;
        }

        for (int i = 0; i < connections.length; i++) {
            if (text.trim().equalsIgnoreCase(connections[i].trim())) {
                connectionCombo.select(i);
                return;
            }
        }

        setText(null);
    }

    public int getSelectionIndex() {
        return connectionCombo.getSelectionIndex();
    }

    public void select(int index) {
        connectionCombo.select(index);
    }

    @Override
    public boolean setFocus() {
        return connectionCombo.setFocus();
    }
}
