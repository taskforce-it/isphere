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

package de.taskforce.tn5250j.rse.subsystems;

import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemFactoryImpl;
import com.ibm.etools.systems.subsystems.*;
import com.ibm.etools.systems.model.*;

import de.taskforce.tn5250j.rse.actions.InfoAction;
import de.taskforce.tn5250j.rse.actions.NewDesignerSessionAction;
import de.taskforce.tn5250j.rse.actions.NewSessionAction;
import de.taskforce.tn5250j.rse.actions.TransferTN5250JLibraryAction;

public class TN5250JSubSystemFactory extends DefaultSubSystemFactoryImpl {

	public TN5250JSubSystemFactory() {
		super();
	}

	protected SubSystem createSubSystemInternal(SystemConnection conn) {
		return new TN5250JSubSystem();
	}

	public boolean supportsFilters() {
		return false;
	}

	protected Vector getAdditionalSubSystemActions(SubSystem subSystem, Shell shell) {

		Vector<IAction> actions = new Vector<IAction>();
		
		IAction newSessionAction = new NewSessionAction(shell);
		actions.add(newSessionAction);

		IAction newDesignerSessionAction = new NewDesignerSessionAction(subSystem.getSystemProfileName(), subSystem.getSystemConnectionName(), shell);
		actions.add(newDesignerSessionAction);
		
		IAction transferTN5250JLibraryAction = new TransferTN5250JLibraryAction(ISeriesConnection.getConnection(subSystem.getSystemConnection()), shell);
		actions.add(transferTN5250JLibraryAction);

		IAction infoAction = new InfoAction(shell);
		actions.add(infoAction);
		
		return actions;

	}

}
