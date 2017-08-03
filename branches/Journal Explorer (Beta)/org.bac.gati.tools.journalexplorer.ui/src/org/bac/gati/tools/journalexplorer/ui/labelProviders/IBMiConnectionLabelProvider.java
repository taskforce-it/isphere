package org.bac.gati.tools.journalexplorer.ui.labelProviders;

import org.bac.gati.tools.journalexplorer.rse.shared.model.ConnectionDelegate;
import org.eclipse.jface.viewers.LabelProvider;

public class IBMiConnectionLabelProvider extends LabelProvider {

    @Override
    public String getText(Object element) {

        if (ConnectionDelegate.instanceOf(element)) {
            return ConnectionDelegate.getConnectionName(element);
        }

        return super.getText(element);
    }
}
