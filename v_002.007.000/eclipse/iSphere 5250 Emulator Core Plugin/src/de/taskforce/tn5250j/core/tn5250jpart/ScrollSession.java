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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ScrollSession {

	private Display display;
	private CTabFolder tabFolderSessions;
	private java.awt.event.KeyEvent keyEvent;

	public ScrollSession(Display display, CTabFolder tabFolderSessions, java.awt.event.KeyEvent keyEvent) {
		this.display = display;
		this.tabFolderSessions = tabFolderSessions;
		this.keyEvent = keyEvent;
	}

	public void start() {
		display.asyncExec(new Runnable() {
			public void run() {
				CTabItem tabItemSession = tabFolderSessions.getSelection();
				ArrayList arrayListTabItemTN5250J = (ArrayList)tabItemSession.getData("TabItemTN5250J");
				ArrayList arrayListCompositeSession = (ArrayList)tabItemSession.getData("CompositeSession");
				for (int idx = 0; idx < arrayListTabItemTN5250J.size(); idx++) {
					TN5250JPanel tn5250j = (TN5250JPanel)arrayListTabItemTN5250J.get(idx);
					if (tn5250j.getSessionGUI().hasFocus()) {
						Composite compositeX = (Composite)arrayListCompositeSession.get(idx);
						ScrolledComposite sc = (ScrolledComposite)compositeX.getData("ScrolledComposite");
						if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
							Point point = sc.getOrigin();
							point.y = point.y - 50;
							sc.setOrigin(point);
						}
						if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
							Point point = sc.getOrigin();
							point.y = point.y + 50;
							sc.setOrigin(point);
						}
						if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
							Point point = sc.getOrigin();
							point.x = point.x - 50;
							sc.setOrigin(point);
						}
						if (keyEvent.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
							Point point = sc.getOrigin();
							point.x = point.x + 50;
							sc.setOrigin(point);
						}
						break;
					}
				}
			}
		});
	}
	
}
