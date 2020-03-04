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

import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.SubSystemConfiguration;

public class TN5250JSubSystemConfiguration extends SubSystemConfiguration {

	public TN5250JSubSystemConfiguration() {
		super();
	}

	public ISubSystem createSubSystemInternal(IHost host) {
	   	return new TN5250JSubSystem(host, getConnectorService(host));
	}
	
	public IConnectorService getConnectorService(IHost host) {
		return TN5250JConnectorServiceManager.getInstance().getConnectorService(host, ITN5250JSubSystem.class);
	}

	public boolean supportsUserId() {
		return false;
	}

	public boolean supportsServerLaunchProperties(IHost host) {
		return false;
	}

	public boolean supportsFilters() {
		return false;
	}

}
