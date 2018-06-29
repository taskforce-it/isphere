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

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;

public class SetMajorSession {
	
	private Display display;
	private CTabFolder tabFolderSessions;
	private String direction;
	private ITN5250JPart tn5250jPart;

	public SetMajorSession(Display display, CTabFolder tabFolderSessions, String direction, ITN5250JPart tn5250jPart) {
		this.display = display;
		this.tabFolderSessions = tabFolderSessions;
		this.direction = direction;
		this.tn5250jPart = tn5250jPart;
	}

	public void start() {
		display.asyncExec(new Runnable() {
			public void run() {
				if (direction.equals("*NEXT")) {
					int currentItem = tabFolderSessions.getSelectionIndex();
					if (currentItem >= 0) {
						int itemCount = tabFolderSessions.getItemCount() - 1;
						int nextItem;
						if (currentItem < itemCount) {
							nextItem = currentItem + 1;
						}
						else {
							nextItem = 0;
						}
						tabFolderSessions.setSelection(nextItem);
						SetSessionFocus.run(nextItem, -1, tn5250jPart);
					}
				}
				else if (direction.equals("*PREVIOUS")) {
					int currentItem = tabFolderSessions.getSelectionIndex();
					if (currentItem >= 0) {
						int itemCount = tabFolderSessions.getItemCount() - 1;
						int nextItem;
						if (currentItem > 0) {
							nextItem = currentItem - 1;
						}
						else {
							nextItem = itemCount;
						}
						tabFolderSessions.setSelection(nextItem);
						SetSessionFocus.run(nextItem, -1, tn5250jPart);
					}
				}
			}
		});
	}
	
}
