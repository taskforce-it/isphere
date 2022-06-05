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

package de.taskforce.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.taskforce.tn5250j.core.session.Session;

public class AddMultiSession {

	@SuppressWarnings("unchecked")
	public static void run(ITN5250JPart tn5250jPart) {
		
		CTabItem tabItemSession = tn5250jPart.getTabFolderSessions().getSelection();

		ArrayList<Composite> arrayListCompositeSession = (ArrayList)tabItemSession.getData("CompositeSession");
		ArrayList<TN5250JPanel> arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData("TabItemTN5250J");
		Session session = (Session)tabItemSession.getData("Session"); 
		TN5250JInfo tn5250jInfo = (TN5250JInfo)tabItemSession.getData("TN5250JInfo");
		
		int numberOfSessions = arrayListCompositeSession.size();
		
		if (numberOfSessions < 4) {

			Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
			GridLayout gridLayoutControl = new GridLayout();
			gridLayoutControl.numColumns = 2;
			compositeControl.setLayout(gridLayoutControl);
			
			for (int idx = 0; idx < numberOfSessions; idx++) {
				Composite compositeSession = arrayListCompositeSession.get(idx);
				compositeSession.setParent(compositeControl);
			}

			CreateSession createSession = new CreateSession();
			final TN5250JPanel tn5250j = createSession.run(compositeControl, arrayListCompositeSession, arrayListTabItemTN5250J, session, tn5250jInfo);
			
			tabItemSession.setControl(compositeControl);
			
			SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), numberOfSessions, tn5250jPart);
			
			new ConnectSession(tn5250j).start();
			
		}
		
	}
	
}
