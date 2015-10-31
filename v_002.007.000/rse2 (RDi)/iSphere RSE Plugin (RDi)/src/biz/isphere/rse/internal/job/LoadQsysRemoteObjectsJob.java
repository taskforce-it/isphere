/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal.job;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.model.ISystemRegistry;
import org.eclipse.rse.core.subsystems.ISubSystem;

import biz.isphere.core.dataspaceeditordesigner.rse.IListOfRemoteObjectsReceiver;
import biz.isphere.core.internal.RemoteObject;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public class LoadQsysRemoteObjectsJob extends Job {

    String[] droppedObjects;
    IListOfRemoteObjectsReceiver receiver;

    public LoadQsysRemoteObjectsJob(String name, String[] droppedObjects, IListOfRemoteObjectsReceiver receiver) {
        super(name);
        this.droppedObjects = droppedObjects;
        this.receiver = receiver;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {

        List<QSYSRemoteObject> qsysRemoteObjects = getRSESourceObjects(droppedObjects);
        List<RemoteObject> remoteObjects = new ArrayList<RemoteObject>();
        for (QSYSRemoteObject qsysRemoteObject : qsysRemoteObjects) {
            String connectionName = IBMiConnection.getConnection(getHost(qsysRemoteObject)).getConnectionName();
            String name = qsysRemoteObject.getName();
            String library = qsysRemoteObject.getLibrary();
            String objectType = qsysRemoteObject.getType();
            String description = qsysRemoteObject.getDescription();
            remoteObjects.add(new RemoteObject(connectionName, name, library, objectType, description));
        }
        receiver.setRemoteObjects(remoteObjects.toArray(new RemoteObject[remoteObjects.size()]));

        return Status.OK_STATUS;
    }

    private IHost getHost(QSYSRemoteObject object) {
        IHost host = object.getRemoteObjectContext().getObjectSubsystem().getHost();
        return host;
    }

    private ArrayList<QSYSRemoteObject> getRSESourceObjects(String[] droppedObjects) {

        // Load objects from system
        ArrayList<QSYSRemoteObject> qsysRemoteObjects = new ArrayList<QSYSRemoteObject>();
        for (int i = 0; i < droppedObjects.length; i++) {
            String droppedObject = droppedObjects[i];

            Object anyObject = getObjectFor(droppedObject);
            if (anyObject instanceof QSYSRemoteObject) {
                QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)anyObject;
                qsysRemoteObjects.add(qsysRemoteObject);
            }
        }

        return qsysRemoteObjects;
    }

    private Object getObjectFor(String droppedObject) {
        ISystemRegistry registry = RSECorePlugin.getTheSystemRegistry();

        // Get connection delimiter
        int connectionDelim = droppedObject.indexOf(":"); //$NON-NLS-1$
        if (connectionDelim == -1) {
            int profileDelim = droppedObject.indexOf("."); //$NON-NLS-1$
            if (profileDelim != -1) {
                String profileId = droppedObject.substring(0, profileDelim);
                String connectionId = droppedObject.substring(profileDelim + 1, droppedObject.length());
                ISystemProfile profile = registry.getSystemProfile(profileId);
                return registry.getHost(profile, connectionId);
            }
        }

        // Get subsystem delimiter
        int subsystemDelim = droppedObject.indexOf(":", connectionDelim + 1); //$NON-NLS-1$
        if (subsystemDelim == -1) {
            return registry.getSubSystem(droppedObject);
        }

        String subSystemId = droppedObject.substring(0, subsystemDelim);
        String objectKey = droppedObject.substring(subsystemDelim + 1, droppedObject.length());

        ISubSystem subSystem = registry.getSubSystem(subSystemId);
        if (subSystem != null) {
            Object result = null;
            try {
                result = subSystem.getObjectWithAbsoluteName(objectKey, new NullProgressMonitor());
            } catch (Exception localException) {
            }

            if (result != null) {
                return result;
            }

            return null;
        }

        return null;
    }
}
