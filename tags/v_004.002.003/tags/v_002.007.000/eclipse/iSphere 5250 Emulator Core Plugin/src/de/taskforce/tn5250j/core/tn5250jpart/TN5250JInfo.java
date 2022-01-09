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

import org.eclipse.swt.widgets.Shell;

import de.taskforce.tn5250j.core.session.Session;

public abstract class TN5250JInfo {
	
	private ITN5250JPart tn5250jPart;
	
	public TN5250JInfo(ITN5250JPart tn5250jPart) {
		this.tn5250jPart = tn5250jPart;
	}

	public String getTN5250JDescription() {
		return "";
	}

	public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
		return true;
	}

	public TN5250JPanel getTN5250JPanel(Session session, Shell shell) {
		return null;
	}

	public ITN5250JPart getTN5250JPart() {
		return tn5250jPart;
	}
	
}
