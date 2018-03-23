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

import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;

public abstract class CoreSessionsInfo extends TN5250JInfo {
	
	private String connection;
	private String session;

	public CoreSessionsInfo(ITN5250JPart tn5250jPart) {
		super(tn5250jPart);
		connection = "";
		session = "";
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getTN5250JDescription() {
		return connection + "/" + session;
	}

	public boolean isTN5250JEqual(TN5250JInfo tn5250jInfo) {
		CoreSessionsInfo sessionsInfo = (CoreSessionsInfo) tn5250jInfo;
		if (connection.equals(sessionsInfo.getConnection()) && 
			session.equals(sessionsInfo.getSession())) {
			return true;
		} 
		else {
			return false;
		}
	}
	
}
