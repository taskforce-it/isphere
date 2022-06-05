/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.events.ISystemRemoteChangeEvents;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.swt.widgets.Display;

import com.ibm.as400.ui.util.CommandPrompter;
import com.ibm.etools.iseries.rse.util.clprompter.CLPrompter;

public class SpooledFileChangeAction extends AbstractSpooledFileAction {

	public String execute(SpooledFileResource spooledFileResource) {
		try {
			CLPrompter command = new CLPrompter();
			command.setCommandString(spooledFileResource.getSpooledFile().getCommandChangeAttribute());
			command.setConnection(getISeriesConnection());
			command.setParent(Display.getCurrent().getActiveShell());
			if (command.showDialog() == CommandPrompter.OK) {
				
				String message = spooledFileResource.getSpooledFile().changeAttribute(command.getCommandString());
				
				if (message == null) {
					ISystemRegistry sr = RSECorePlugin.getTheSystemRegistry();
					Vector<SpooledFileResource> spooledFileVector = new Vector<SpooledFileResource>();
					spooledFileVector.addElement(spooledFileResource);
					sr.fireRemoteResourceChangeEvent(
							ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_CREATED, 
							spooledFileVector, 
                            spooledFileResource.getSubSystem(), 
                            null, 
							null, 
							null);
				}

				return message;
				
			}
			else {
				return null;
			}
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
}