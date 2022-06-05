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

import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemImpl;
import com.ibm.etools.systems.subsystems.impl.*;

import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.model.RSESession;

public class TN5250JSubSystem extends DefaultSubSystemImpl {
	
	private RSESession[] rseSessions = null;

	public TN5250JSubSystem() {
		super();
	}

	public AbstractSystemManager getSystemManager() {
		return TN5250JSystemManager.getTN5250JSystemManager();
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
		
		String directory = TN5250JRSEPlugin.getRSESessionDirectory(getSystemProfileName() + "-" + getSystemConnectionName()) ;
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
