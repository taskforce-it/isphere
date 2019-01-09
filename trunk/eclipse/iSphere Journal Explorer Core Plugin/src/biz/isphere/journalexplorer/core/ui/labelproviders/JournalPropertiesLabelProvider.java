/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.labelproviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryAppearanceAttributes;

public class JournalPropertiesLabelProvider implements ITableLabelProvider, ITableColorProvider {

    private final int PROPERTY_COLUMN = 0;
    private final int VALUE_COLUMN = 1;

    private Map<String, Color> colors;

    public JournalPropertiesLabelProvider() {
        initialize();
    }

    private void initialize() {

        JournalEntryAppearanceAttributes[] attributes = Preferences.getInstance().getSortedJournalEntryAppearancesAttributes();

        colors = new HashMap<String, Color>();

        for (JournalEntryAppearanceAttributes attribute : attributes) {
            colors.put(attribute.getColumnName(), attribute.getColor());
        }
    }

    public void addListener(ILabelProviderListener listener) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object object, String property) {
        return false;
    }

    public void removeListener(ILabelProviderListener listener) {
    }

    public Image getColumnImage(Object object, int columnIndex) {
        if (object instanceof JournalProperty && columnIndex == VALUE_COLUMN) {
            JournalProperty journalProperty = (JournalProperty)object;
            if (journalProperty.isErrorParsing()) {
                return ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_WARNING_OV);
            } else if (((JournalProperty)object).isNullValue()) {
                return ISphereJournalExplorerCorePlugin.getDefault().getImage(ISphereJournalExplorerCorePlugin.IMAGE_NULL_OV);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getColumnText(Object object, int columnIndex) {

        if (object instanceof JournalProperties) {
            JournalProperties journalProperties = (JournalProperties)object;

            switch (columnIndex) {
            case PROPERTY_COLUMN:
                return journalProperties.getJournalEntry().getKey();

            case VALUE_COLUMN:
                String qualifiedName = journalProperties.getJournalEntry().getQualifiedObjectName();
                return Messages.bind(Messages.ColLabel_JournalEntry_Table_A, qualifiedName);
            }
        } else if (object instanceof JournalProperty) {
            JournalProperty journalProperty = (JournalProperty)object;

            switch (columnIndex) {
            case PROPERTY_COLUMN:

                return journalProperty.label;
            case VALUE_COLUMN:
                return journalProperty.value.toString();
            }
        }
        return null;
    }

    public Color getBackground(Object object, int columnIndex) {

        if (object instanceof JournalProperties) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

        } else if (object instanceof JournalProperty) {

            JournalProperty journalProperty = (JournalProperty)object;
            if (journalProperty.highlighted) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            } else {
                return null; // colors.get(journalProperty.name);
            }
        }
        return null;
    }

    public Color getForeground(Object object, int columnIndex) {
        return null;
    }

}