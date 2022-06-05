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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.session.SessionDetailDialog;
import de.taskforce.tn5250j.rse.DialogActionTypes;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;
import de.taskforce.tn5250j.rse.subsystems.TN5250JSubSystem;

public class NewSessionAction implements IObjectActionDelegate {

	private ArrayList<TN5250JSubSystem> selectedSubSystems;
	
	public NewSessionAction() {
		selectedSubSystems = new ArrayList<TN5250JSubSystem>();
	}

	public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
	}

	public void run(IAction action) {
		for (int idx = 0; idx < selectedSubSystems.size(); idx++) {
			Session session = new Session(TN5250JRSEPlugin.getRSESessionDirectory(selectedSubSystems.get(idx).getSystemProfileName() + "-" + selectedSubSystems.get(idx).getHostAliasName()));
			session.setConnection(selectedSubSystems.get(idx).getSystemProfileName() + "-" + selectedSubSystems.get(idx).getHostAliasName());
			SessionDetailDialog sessionDetailDialog = new SessionDetailDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), TN5250JRSEPlugin.getRSESessionDirectory(selectedSubSystems.get(idx).getSystemProfileName() + "-" + selectedSubSystems.get(idx).getHostAliasName()), DialogActionTypes.CREATE, session);
			if (sessionDetailDialog.open() == Dialog.OK) {
				RSESession rseSession = new RSESession(selectedSubSystems.get(idx), session.getName(), session);
				rseSession.create(selectedSubSystems.get(idx));
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		selectedSubSystems.clear();
		Iterator<?> theSet = ((IStructuredSelection)selection).iterator();
		while (theSet.hasNext()) {
			Object object = theSet.next();
			if (object instanceof TN5250JSubSystem) {
				selectedSubSystems.add((TN5250JSubSystem)object);
			}
		}
	}

}
