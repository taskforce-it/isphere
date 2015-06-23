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

package de.taskforce.tn5250j.rse.model;

import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.model.ISystemResourceChangeEvents;
import com.ibm.etools.systems.model.SystemRegistry;
import com.ibm.etools.systems.model.impl.SystemResourceChangeEvent;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractResource;

import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.core.session.Session;

public class RSESession extends AbstractResource {
	
	private String rseProfil;
	private String rseConnection;
	private String name;
	private Session session;

	public RSESession(String rseProfil, String rseConnection, String name, Session session) {
		super();
		this.rseProfil = rseProfil;
		this.rseConnection = rseConnection;
		init(name, session);
	}
	
	public RSESession(SubSystem subSystem, String name, Session session) {
		super(subSystem);
		rseProfil = subSystem.getSystemProfileName();
		rseConnection = subSystem.getSystemConnectionName();
		init(name, session);
	}
	
	private void init(String name, Session session) {
		this.name = name;
		this.session = session;
	}
	
	public String getRSEProfil() {
		return rseProfil;
	}

	public void setRSEProfil(String rseProfil) {
		this.rseProfil = rseProfil;
	}
	
	public String getRSEConnection() {
		return rseConnection;
	}

	public void setRSEConnection(String rseConnection) {
		this.rseConnection = rseConnection;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public static RSESession load(String rseProfil, String rseConnection, String name){
		Session session = Session.load(TN5250JRSEPlugin.getRSESessionDirectory(rseProfil + "-" + rseConnection), rseProfil + "-" + rseConnection, name);
		if (session != null) {
    		return new RSESession(rseProfil, rseConnection, name, session);
		}
		return null;
	}
	
	public static RSESession load(SubSystem subSystem, String name){
		Session session = Session.load(TN5250JRSEPlugin.getRSESessionDirectory(subSystem.getSystemProfileName() + "-" + subSystem.getSystemConnectionName()), subSystem.getSystemProfileName() + "-" + subSystem.getSystemConnectionName(), name);
		if (session != null) {
    		return new RSESession(subSystem, name, session);
		}
		return null;
	}
	
	public boolean create(Object object) {
		if (session.create()) {
			SystemRegistry systemRegistry = SystemPlugin.getDefault().getSystemRegistry();
			systemRegistry.fireEvent(new SystemResourceChangeEvent(object, ISystemResourceChangeEvents.EVENT_REFRESH, object));
		    return true;
		}				
		return false;
	}
	
	public boolean update(Object object) {
		if (session.update()) {
			SystemRegistry systemRegistry = SystemPlugin.getDefault().getSystemRegistry();
			systemRegistry.fireEvent(new SystemResourceChangeEvent(object, ISystemResourceChangeEvents.EVENT_REFRESH, object));
		    return true;
		}
		return false;
	}
	
	public boolean delete(Object object) {
		if (session.delete()) {
			SystemRegistry systemRegistry = SystemPlugin.getDefault().getSystemRegistry();
			systemRegistry.fireEvent(new SystemResourceChangeEvent(object, ISystemResourceChangeEvents.EVENT_REFRESH, object));
		    return true;
		}
		return false;
	}

}
