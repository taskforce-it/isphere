/*******************************************************************************
 * Copyright (c) 2012-2020 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import biz.isphere.core.internal.exception.CanceledByUserException;

import com.ibm.as400.ui.util.CommandPrompter;
import com.ibm.etools.iseries.core.util.clprompter.CLPrompter;
import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.model.ISystemRemoteChangeEvents;
import com.ibm.etools.systems.model.SystemRegistry;

public class SpooledFileChangeAction extends AbstractSpooledFileAction {

    @Override
    public String execute(SpooledFileResource spooledFileResource) throws CanceledByUserException {
        try {
            CLPrompter command = new CLPrompter();
            command.setCommandString(spooledFileResource.getSpooledFile().getCommandChangeAttribute());
            command.setConnection(getISeriesConnection());
            command.setParent(Display.getCurrent().getActiveShell());
            if (command.showDialog() == CommandPrompter.OK) {

                String message = spooledFileResource.getSpooledFile().changeAttribute(command.getCommandString());

                if (message == null) {
                    SystemRegistry sr = SystemPlugin.getDefault().getSystemRegistry();
                    Vector<SpooledFileResource> spooledFileVector = new Vector<SpooledFileResource>();
                    spooledFileVector.addElement(spooledFileResource);
                    sr.fireRemoteResourceChangeEvent(ISystemRemoteChangeEvents.SYSTEM_REMOTE_RESOURCE_CREATED, spooledFileVector, spooledFileVector
                        .firstElement().getSubSystem(), null, null, null);
                }

                return message;

            } else {
                return null;
            }
        } catch (Exception e) {
            if (e instanceof CanceledByUserException) {
                throw new CanceledByUserException();
            }
            return e.getMessage();
        }
    }

}