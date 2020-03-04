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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.rse.ui.actions.SystemBaseAction;

import de.taskforce.tn5250j.rse.DialogActionTypes;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;
import de.taskforce.tn5250j.core.session.SessionDetailDialog;

public class DeleteSessionAction extends SystemBaseAction {

	public DeleteSessionAction(Shell parent) {
		super(Messages.getString("Delete_session"), parent);
		setAvailableOffline(true);
		setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DELETE));
	}

	public void run() {
		if (getFirstSelection() instanceof RSESession) {
			RSESession rseSession = (RSESession)getFirstSelection();
			if (rseSession != null) {
				SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(rseSession.getRSEProfil() + "-" + rseSession.getRSEConnection()), DialogActionTypes.DELETE, rseSession.getSession());
				if (sessionDetailDialog.open() == Dialog.OK) {
					rseSession.delete(rseSession.getSubSystem());
				}
			}
		}
	}

}