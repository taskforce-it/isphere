package org.bac.gati.tools.journalexplorer.ui.labelProviders;

import org.eclipse.jface.viewers.LabelProvider;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class IBMiConnectionLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		
		if (element instanceof IBMiConnection) {
			IBMiConnection connection = (IBMiConnection) element;
			return connection.getHostName();
		}
		
		return super.getText(element);
	}
}
