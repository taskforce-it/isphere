package biz.isphere.journalexplorer.core.ui.labelProviders;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import biz.isphere.journalexplorer.core.JournalExplorerPlugin;
import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;

public class JournalEntryViewLabelProvider implements ITableLabelProvider, ITableColorProvider {

    private final int PROPERTY_COLUMN = 0;
    private final int VALUE_COLUMN = 1;

    public void addListener(ILabelProviderListener arg0) {
    }

    public void dispose() {
    }

    public boolean isLabelProperty(Object arg0, String arg1) {
        return false;
    }

    public void removeListener(ILabelProviderListener arg0) {
    }

    public Image getColumnImage(Object object, int columnIndex) {
        if (object instanceof JOESDProperty && columnIndex == VALUE_COLUMN) {
            if (((JOESDProperty)object).isErrorParsing()) {
                return JournalExplorerPlugin.getImage(JournalExplorerPlugin.IMAGE_WARNING_OV);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String getColumnText(Object object, int columnIndex) {

        if (object instanceof JournalProperties) {
            switch (columnIndex) {
            case PROPERTY_COLUMN:
                return ((JournalProperties)object).getJournal().getKey();

                // TODO encapsular logica
            case VALUE_COLUMN:
                return "Table " + ((JournalProperties)object).getJournal().getQualifiedObjectName();
            }
        } else if (object instanceof JournalProperty) {
            switch (columnIndex) {
            case PROPERTY_COLUMN:

                return ((JournalProperty)object).name;
            case VALUE_COLUMN:
                return ((JournalProperty)object).value.toString();
            }
        }
        return null;
    }

    public Color getBackground(Object object, int columnIndex) {

        if (object instanceof JournalProperties) {
            return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);

        } else if (object instanceof JournalProperty) {

            if (((JournalProperty)object).highlighted) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            } else {
                return null;
            }
        }
        return null;
    }

    public Color getForeground(Object arg0, int arg1) {
        return null;
    }

}