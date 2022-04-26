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

package de.taskforce.tn5250j.rse.sessionspart;

import org.eclipse.swt.widgets.Shell;

import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.sessionspart.CoreSessionsInfo;
import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JPanel;

public class SessionsInfo extends CoreSessionsInfo {
	
	private String rseProfil;
	private String rseConnection;

	public SessionsInfo(ITN5250JPart tn5250jPart) {
		super(tn5250jPart);
		rseProfil = "";
		rseConnection = "";
	}

	public String getRSEProfil() {
		return rseProfil;
	}

	public void setRSEProfil(String rseProfil) {
		this.rseProfil = rseProfil;
		setConnection(this.rseProfil + "-" + this.rseConnection);
	}

	public String getRSEConnection() {
		return rseConnection;
	}

	public void setRSEConnection(String rseConnection) {
		this.rseConnection = rseConnection;
		setConnection(this.rseProfil + "-" + this.rseConnection);
	}

	public String getTN5250JDescription() {
		return rseConnection + "/" + getSession();
	}

	public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
		SessionsInfo sessionsInfo = (SessionsInfo) tn5250jInfo;
		if (rseProfil.equals(sessionsInfo.getRSEProfil()) &&
			rseConnection.equals(sessionsInfo.getRSEConnection()) && 
			getSession().equals(sessionsInfo.getSession())) {
			return true;
		} 
		else {
			return false;
		}
	}

	public TN5250JPanel getTN5250JPanel(Session session, Shell shell) {
		return new SessionsPanel(this, session, shell);
	}
	
}
