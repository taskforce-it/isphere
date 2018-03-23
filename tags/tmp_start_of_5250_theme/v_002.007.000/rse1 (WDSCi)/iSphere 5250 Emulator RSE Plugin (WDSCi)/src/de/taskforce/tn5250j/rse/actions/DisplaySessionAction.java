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

import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;

import de.taskforce.tn5250j.rse.DialogActionTypes;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;
import de.taskforce.tn5250j.core.session.SessionDetailDialog;

public class DisplaySessionAction extends SystemBaseAction {

	public DisplaySessionAction(Shell parent) {
		super(Messages.getString("Display_session"), parent);
		setAvailableOffline(true);
		setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DISPLAY));
	}

	public void run() {
		if (getFirstSelection() instanceof RSESession) {
			RSESession rseSession = (RSESession)getFirstSelection();
			if (rseSession != null) {
				new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(rseSession.getRSEProfil() + "-" + rseSession.getRSEConnection()), DialogActionTypes.DISPLAY, rseSession.getSession()).open();
			}
		}
	}

}