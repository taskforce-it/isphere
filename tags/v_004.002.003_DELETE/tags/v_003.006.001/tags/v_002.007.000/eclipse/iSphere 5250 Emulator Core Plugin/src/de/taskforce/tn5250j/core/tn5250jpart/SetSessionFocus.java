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

import java.awt.EventQueue;
import java.util.ArrayList;

import org.eclipse.swt.custom.CTabItem;

import de.taskforce.tn5250j.core.session.Session;

public class SetSessionFocus {

	public static void run(int majorSession, int minorSession, ITN5250JPart tn5250jPart) {
		if (majorSession >= 0) {
			CTabItem tabItem = tn5250jPart.getTabFolderSessions().getItem(majorSession);
			int newMinorSession = minorSession;
			if (newMinorSession == -1) {
				newMinorSession = ((Integer)tabItem.getData("LastFocus")).intValue();
			}
			ArrayList arrayListTabItemTN5250J = (ArrayList)tabItem.getData("TabItemTN5250J");
			final TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(newMinorSession);
			if (tn5250j != null) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						tn5250j.getSessionGUI().grabFocus();
					}
				});
				if (tn5250jPart.isMultiSession()) {
					Session session = (Session)tabItem.getData("Session");
					if (!session.getDevice().equals("")) {
						tn5250jPart.setAddSession(false);
						tn5250jPart.setRemoveSession(false);
					}
					else {
						if (arrayListTabItemTN5250J.size() == 4) {
							tn5250jPart.setAddSession(false);
						}
						else {
							tn5250jPart.setAddSession(true);
						}
						if (arrayListTabItemTN5250J.size() == 1) {
							tn5250jPart.setRemoveSession(false);
						}
						else {
							tn5250jPart.setRemoveSession(true);
						}
					}
				}
			}
		}
	}
	
}
