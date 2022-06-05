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

import java.awt.event.KeyEvent;

import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;


public abstract class TN5250JGUI extends SessionPanel {

	private static final long serialVersionUID = 1L;
	private TN5250JInfo tn5250jInfo;
	private Session5250 session5250;
	
	public TN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
		super(session5250);
		this.tn5250jInfo = tn5250jInfo;
		this.session5250 = session5250;
	}

	public void processKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.isControlDown() && keyEvent.isAltDown() && 
			(keyEvent.getKeyCode() == KeyEvent.VK_UP ||
			keyEvent.getKeyCode() == KeyEvent.VK_DOWN ||
			keyEvent.getKeyCode() == KeyEvent.VK_LEFT ||
			keyEvent.getKeyCode() == KeyEvent.VK_RIGHT)) {
			new ScrollSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(), keyEvent).start();
		}
		else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_UP) {
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
			}
		}
		else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				new SetMajorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
			}
		}
		
		else if (keyEvent.isAltDown() && keyEvent.getKeyCode() == KeyEvent.VK_UP) {
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(), "*NEXT", tn5250jInfo.getTN5250JPart()).start();
			}
		}
		else if (keyEvent.isAltDown() && keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
			if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				new SetMinorSession(tn5250jInfo.getTN5250JPart().getTabFolderSessions().getDisplay(), tn5250jInfo.getTN5250JPart().getTabFolderSessions(), "*PREVIOUS", tn5250jInfo.getTN5250JPart()).start();
			}
		}
		else {
			super.processKeyEvent(keyEvent);
		}
	}

	public TN5250JInfo getTN5250JInfo() {
		return tn5250jInfo;
	}

	public Session5250 getSession5250() {
		return session5250;
	}

}
