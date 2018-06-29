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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.taskforce.tn5250j.core.info.InfoDetailDialog;
import de.taskforce.tn5250j.rse.subsystems.TN5250JSubSystem;

public class InfoAction implements IObjectActionDelegate {

	private ArrayList<TN5250JSubSystem> selectedSubSystems;
	
	public InfoAction() {
		selectedSubSystems = new ArrayList<TN5250JSubSystem>();
	}

	public void setActivePart(IAction action, IWorkbenchPart workbenchPart) {
	}

	public void run(IAction action) {
		for (int idx = 0; idx < selectedSubSystems.size(); idx++) {
			new InfoDetailDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()).open();
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
