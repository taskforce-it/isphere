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

public class AddSession {

	public static void run(String sessionDirectory, String connection, String name, TN5250JInfo tn5250jInfo) {

		ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();
		
		Session session = Session.load(sessionDirectory, connection, name);
		if (session != null) {
			Composite compositeControl = new Composite(tn5250jPart.getTabFolderSessions(), SWT.NONE);
			GridLayout gridLayoutControl = new GridLayout();
			gridLayoutControl.numColumns = 1;
			compositeControl.setLayout(gridLayoutControl);

			ArrayList<Composite> arrayListCompositeSession = new ArrayList<Composite>();
			ArrayList<TN5250JPanel> arrayListTabItemTN5250J = new ArrayList<TN5250JPanel>();
			
			CreateSession createSession = new CreateSession();
			final TN5250JPanel tn5250j = createSession.run(compositeControl, arrayListCompositeSession, arrayListTabItemTN5250J, session, tn5250jInfo);
			
			CTabItem tabItemSession = new CTabItem(tn5250jPart.getTabFolderSessions(), SWT.NONE);
			tabItemSession.setText(tn5250jInfo.getTN5250JDescription());
			tabItemSession.setData("Connection", connection);
			tabItemSession.setData("Name", name);
			tabItemSession.setData("TN5250JInfo", tn5250jInfo);
			tabItemSession.setData("CompositeSession", arrayListCompositeSession);
			tabItemSession.setData("TabItemTN5250J", arrayListTabItemTN5250J);
			tabItemSession.setData("Session", session);
			tabItemSession.setData("LastFocus", new Integer(0));
			tabItemSession.setControl(compositeControl);

			tn5250jPart.getTabFolderSessions().setSelection(tabItemSession);
			
			SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), 0, tn5250jPart);
			
			new ConnectSession(tn5250j).start();
			
		}
		
	}
	
}
