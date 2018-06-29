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

import org.eclipse.swt.custom.CTabItem;

public class DisplaySession {

	public static void run(String sessionDirectory, String connection, String name, TN5250JInfo tn5250jInfo) {

		ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();
		
		int tabItemNumber = -1;
		CTabItem[] tabItems = tn5250jPart.getTabFolderSessions().getItems();
		for (int idx = 0; idx < tabItems.length; idx++) {
			String tabItemConnection = (String)tabItems[idx].getData("Connection");
			String tabItemName = (String)tabItems[idx].getData("Name");
			TN5250JInfo tabItemTN5250JInfo = (TN5250JInfo)tabItems[idx].getData("TN5250JInfo");
			if (connection.equals(tabItemConnection) && name.equals(tabItemName) && tn5250jInfo.isTN5250JEqual(tabItemTN5250JInfo)) {
				tabItemNumber = idx;
				break;
			}
		}
		if (tabItemNumber == -1) {
			
			AddSession.run(sessionDirectory, connection, name, tn5250jInfo);
			
		}
		else {
			tn5250jPart.getTabFolderSessions().setSelection(tabItems[tabItemNumber]);
			
			SetSessionFocus.run(tn5250jPart.getTabFolderSessions().getSelectionIndex(), -1, tn5250jPart);
			
		}
		
	}
	
}
