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

public class RemoveMultiSession {

	@SuppressWarnings("unchecked")
	public static void run(ITN5250JPart tn5250jPart) {

		CTabItem tabItemSession = tn5250jPart.getTabFolderSessions().getSelection();

		int sessionToDelete = ((Integer)tabItemSession.getData("LastFocus")).intValue();
		ArrayList<TN5250JPanel> arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData("TabItemTN5250J");
		ArrayList<Composite> arrayListCompositeSession = (ArrayList)tabItemSession.getData("CompositeSession");
		
		int numberOfSessions = arrayListCompositeSession.size();
		
		if (numberOfSessions > 1) {

			Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
			GridLayout gridLayoutControl = new GridLayout();
			if (numberOfSessions == 2) {
				gridLayoutControl.numColumns = 1;
			}
			else {
				gridLayoutControl.numColumns = 2;
			}
			compositeControl.setLayout(gridLayoutControl);
			
			for (int idx = 0; idx < numberOfSessions; idx++) {
				if (idx != sessionToDelete) {
					Composite compositeSession = arrayListCompositeSession.get(idx);
					compositeSession.setParent(compositeControl);
				}
			}
			
			tabItemSession.setControl(compositeControl);
			
			TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(sessionToDelete);
			tn5250j.removeScreenListener();
			tn5250j.getSession5250().disconnect();
			arrayListTabItemTN5250J.remove(sessionToDelete);

			tn5250jPart.removeTN5250JPanel(tn5250j);
			
			Composite compositeSession = (Composite)arrayListCompositeSession.get(sessionToDelete);
			compositeSession.dispose();
			arrayListCompositeSession.remove(sessionToDelete);
			
			SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), 0, tn5250jPart);

		}
		
	}
	
}
