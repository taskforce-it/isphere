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

import org.eclipse.swt.custom.CTabItem;

public class RemoveSession {

	public static void run(CTabItem closedTab, ITN5250JPart tn5250jPart) {
		ArrayList arrayListTabItemTN5250J = (ArrayList)closedTab.getData("TabItemTN5250J");
		for (int idx = 0; idx < arrayListTabItemTN5250J.size(); idx++) {
			TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(idx);
			tn5250j.removeScreenListener();
			tn5250j.getSession5250().disconnect();
			tn5250jPart.removeTN5250JPanel(tn5250j);
			closedTab.dispose();
		}
		if (tn5250jPart.isMultiSession() && tn5250jPart.getTabFolderSessions().getItemCount() == 0) {
			tn5250jPart.setAddSession(false);
			tn5250jPart.setRemoveSession(false);
		}
		
	}
	
}
