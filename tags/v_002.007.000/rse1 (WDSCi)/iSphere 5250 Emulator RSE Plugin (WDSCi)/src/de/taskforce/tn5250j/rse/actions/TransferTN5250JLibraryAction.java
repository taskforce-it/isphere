// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.rse.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;

import de.taskforce.tn5250j.core.misc.TransferTN5250JLibrary;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;

public class TransferTN5250JLibraryAction extends SystemBaseAction {
	
	private ISeriesConnection iseriesConnection;

	public TransferTN5250JLibraryAction(ISeriesConnection iseriesConnection, Shell shell) {
		super(Messages.getString("Transfer_TN5250J_library"), shell);
		setAvailableOffline(true);
		setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_TN5250J));
		this.iseriesConnection = iseriesConnection;
		this.shell = shell;
	}

	public void run() {
		TransferTN5250JLibrary statusDialog = new TransferTN5250JLibrary(getShell().getDisplay(), SWT.APPLICATION_MODAL | SWT.SHELL_TRIM);
		if (statusDialog.connect(iseriesConnection.getSystemConnection().getHostName())) {
			statusDialog.open();
		}
	}

}
