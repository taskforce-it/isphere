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

import java.io.File;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.systems.core.ui.actions.SystemBaseAction;
import com.ibm.etools.systems.subsystems.SubSystem;

import de.taskforce.tn5250j.rse.DialogActionTypes;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;
import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.session.SessionDetailDialog;

public class NewDesignerSessionAction extends SystemBaseAction {

	private String profil;
	private String connection;
	
	public NewDesignerSessionAction(String profil, String connection, Shell parent) {
		super(Messages.getString("New_designer_session"), parent);
		this.profil = profil;
		this.connection = connection;
		setAvailableOffline(true);
		setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_DESIGNER));
	}

	public void run() {
		if (getFirstSelection() instanceof SubSystem) {
			SubSystem subSystem = (SubSystem)getFirstSelection();
			if (subSystem != null) {
				Session session = new Session(TN5250JRSEPlugin.getRSESessionDirectory(profil + "-" + connection));
				session.setConnection(profil + "-" + connection);
				session.setName("_DESIGNER");
				session.setProgram("DESIGNER");
				session.setLibrary("TN5250J");
				SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(shell, TN5250JRSEPlugin.getRSESessionDirectory(profil + "-" + connection), DialogActionTypes.CREATE, session);
				if (sessionDetailDialog.open() == Dialog.OK) {
					RSESession rseSession = new RSESession(subSystem, session.getName(), session);
					rseSession.create(subSystem);
				}
			}
		}
	}

	public boolean isEnabled() {
		String designer = TN5250JRSEPlugin.getRSESessionDirectory(profil + "-" + connection) + File.separator + "_DESIGNER";
		File fileDesigner = new File(designer);
		if (fileDesigner.exists()) {
			return false;
		}
		else {
			return true;
		}
	}

}
