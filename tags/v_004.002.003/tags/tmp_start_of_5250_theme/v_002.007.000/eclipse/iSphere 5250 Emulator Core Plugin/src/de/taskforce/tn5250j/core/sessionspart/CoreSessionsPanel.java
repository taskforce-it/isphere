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

package de.taskforce.tn5250j.core.sessionspart;

import org.eclipse.swt.widgets.Shell;

import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JPanel;

public abstract class CoreSessionsPanel extends TN5250JPanel {
	
	private static final long serialVersionUID = 1L;
	
	public CoreSessionsPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
		super(tn5250jInfo, session, shell);
	}

	public String getHost() {
		return "";
	}
	
}
