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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

public class SetMinorSession {

	private Display display;
	private CTabFolder tabFolderSessions;
	private String direction;
	private ITN5250JPart tn5250jPart;

	public SetMinorSession(Display display, CTabFolder tabFolderSessions, String direction, ITN5250JPart tn5250jPart) {
		this.display = display;
		this.tabFolderSessions = tabFolderSessions;
		this.direction = direction;
		this.tn5250jPart = tn5250jPart;
	}

	public void start() {
		display.asyncExec(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				CTabItem tabItemSession = tabFolderSessions.getSelection();
				ArrayList<TN5250JPanel> arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData("TabItemTN5250J");
				int currentItem = -1;
				for (int idx = 0; idx < arrayListTabItemTN5250J.size(); idx++) {
					TN5250JPanel tn5250j = arrayListTabItemTN5250J.get(idx);
					if (tn5250j.getSessionGUI().hasFocus()) {
						currentItem = idx;
						break;
					}
				}
				if (currentItem != -1) {
					if (direction.equals("*NEXT")) {
						if (currentItem >= 0) {
							int itemCount = arrayListTabItemTN5250J.size() - 1;
							int nextItem;
							if (currentItem < itemCount) {
								nextItem = currentItem + 1;
							}
							else {
								nextItem = 0;
							}
							SetSessionFocus.run(tabFolderSessions.getSelectionIndex(), nextItem, tn5250jPart);
						}
					}
					else if (direction.equals("*PREVIOUS")) {
						if (currentItem >= 0) {
							int itemCount = arrayListTabItemTN5250J.size() - 1;
							int nextItem;
							if (currentItem > 0) {
								nextItem = currentItem - 1;
							}
							else {
								nextItem = itemCount;
							}
							SetSessionFocus.run(tabFolderSessions.getSelectionIndex(), nextItem, tn5250jPart);
						}
					}
				}
			}
		});
	}
	
}
