package org.bac.gati.tools.journalexplorer.ui.labelProviders;

import org.eclipse.jface.viewers.LabelProvider;

import biz.isphere.journalexplorer.rse.shared.model.ConnectionDelegate;

public class IBMiConnectionLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {

        if (ConnectionDelegate.instanceOf(element)) {
            return ConnectionDelegate.getConnectionName(element);
        }

        return super.getText(element);
    }
}
