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

import com.ibm.etools.systems.subsystems.ISystem;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractSystemManager;

public class TN5250JSystemManager extends AbstractSystemManager {
	
	private static TN5250JSystemManager inst;

	protected TN5250JSystemManager() {
		super();
	}

	public static TN5250JSystemManager getTN5250JSystemManager() {
		if (inst == null)
			inst = new TN5250JSystemManager();
		return inst;
	}

	public ISystem createSystemObject(SubSystem subsystem) {
		return new TN5250JSystem(subsystem);
	}

	public boolean sharesSystem(SubSystem otherSubSystem) {
		return (otherSubSystem instanceof ITN5250JSubSystem);
	}

	public Class getSubSystemCommonInterface(SubSystem subsystem) {
		return ITN5250JSubSystem.class;
	}

}
