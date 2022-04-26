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

package de.taskforce.tn5250j.rse.subsystems;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.SubSystem;

import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;

public class TN5250JSubSystem extends SubSystem {
	
	private RSESession[] rseSessions = null;

	public TN5250JSubSystem(IHost host, IConnectorService connectorService) {
		super(host, connectorService);
	}
	
	public void initializeSubSystem(IProgressMonitor monitor) {
	}

	public void uninitializeSubSystem(IProgressMonitor monitor) {
	}

	public Object getObjectWithAbsoluteName(String key) {
		if (key.startsWith("Session_")) {
			String sessionName = key.substring(8);
			RSESession[] rseSessions = getRSESessions();
			for (int idx = 0; idx < rseSessions.length; idx++)
				if (rseSessions[idx].getName().equals(sessionName))
					return rseSessions[idx];
		}
		return null;
	}

	public boolean hasChildren() {
		if (getRSESessions().length == 0) {
			return false;
		}
		else {
			return true;
		}
	}

	public Object[] getChildren() {
		return getRSESessions();
	}

	public RSESession[] getRSESessions() {

		ArrayList<RSESession> arrayListRSESessions = new ArrayList<RSESession>();
		
		String directory = TN5250JRSEPlugin.getRSESessionDirectory(getSystemProfileName() + "-" + getHostAliasName()) ;
		File directoryTN5250J = new File(directory);
		if (!directoryTN5250J.exists()) {
			directoryTN5250J.mkdir();
		}
		
		String stringSessions[] = new File(directory).list();
		for (int idx = 0; idx < stringSessions.length; idx++) {
			RSESession rseSession = RSESession.load(this, stringSessions[idx]);
			if (rseSession != null) {
				arrayListRSESessions.add(rseSession);
			}
		} 

		rseSessions = new RSESession[arrayListRSESessions.size()];
		arrayListRSESessions.toArray(rseSessions);
			
		return rseSessions;
	}

}
