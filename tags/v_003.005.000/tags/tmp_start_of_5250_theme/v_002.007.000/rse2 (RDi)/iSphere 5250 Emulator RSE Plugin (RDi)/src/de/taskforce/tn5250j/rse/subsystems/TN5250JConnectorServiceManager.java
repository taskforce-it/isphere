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

import org.eclipse.rse.core.subsystems.AbstractConnectorServiceManager;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.model.IHost;

public class TN5250JConnectorServiceManager extends AbstractConnectorServiceManager {

	private static TN5250JConnectorServiceManager inst;

	public TN5250JConnectorServiceManager() {
		super();
	}

	public static TN5250JConnectorServiceManager getInstance() {
		if (inst == null)
			inst = new TN5250JConnectorServiceManager();
		return inst;
	}

	public IConnectorService createConnectorService(IHost host) {
		return new TN5250JConnectorService(host);
	}

	public boolean sharesSystem(ISubSystem otherSubSystem) {
		return (otherSubSystem instanceof ITN5250JSubSystem);
	}

	public Class<ITN5250JSubSystem> getSubSystemCommonInterface(ISubSystem subsystem) {
		return ITN5250JSubSystem.class;
	}

}
