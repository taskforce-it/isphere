package de.taskforce.tn5250j.rse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.rse.core.IRSESystemType;
import org.eclipse.rse.core.events.ISystemResourceChangeEvents;
import org.eclipse.rse.core.events.SystemResourceChangeEvent;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class CreateSubsystemsAction implements IViewActionDelegate {

	public void init(IViewPart viewPart) {
	}

	public void run(IAction action) {
		ISystemRegistry sr = SystemStartHere.getSystemRegistry();
		ISubSystemConfiguration ssc = sr.getSubSystemConfiguration("de.taskforce.tn5250j.rse.subsystems.TN5250JSubSystemConfiguration");
		ISystemProfile[] sp = sr.getSystemProfileManager().getSystemProfiles();
		for (int idx1 = 0; idx1 < sp.length; idx1 ++) {
			IHost[] host = sp[idx1].getHosts();
			for (int idx2 = 0; idx2 < host.length; idx2++) {
				if (host[idx2].getSystemType().getId().equals(IRSESystemType.SYSTEMTYPE_ISERIES_ID)) {
					boolean exist = false;
					ISubSystem[] ss = host[idx2].getSubSystems();
					for (int idx3 = 0; idx3 < ss.length; idx3++) {
						if (ss[idx3].getSubSystemConfiguration() == ssc) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						ssc.createSubSystem(host[idx2], true, null);
						sr.fireEvent(new SystemResourceChangeEvent(host[idx2], ISystemResourceChangeEvents.EVENT_REFRESH, host[idx2]));
						host[idx2].commit();
					}
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
