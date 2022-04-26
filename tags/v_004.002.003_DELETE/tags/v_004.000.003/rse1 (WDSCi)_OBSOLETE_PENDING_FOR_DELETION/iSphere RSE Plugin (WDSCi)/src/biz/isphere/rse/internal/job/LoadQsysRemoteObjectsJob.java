/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import biz.isphere.core.dataspaceeditordesigner.rse.IListOfRemoteObjectsReceiver;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.ibm.helper.ISeriesDataElementHelper;

import com.ibm.etools.iseries.core.api.ISeriesObject;
import com.ibm.etools.systems.core.SystemPlugin;
import com.ibm.etools.systems.dstore.core.model.DataElement;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.SystemRegistry;
import com.ibm.etools.systems.subsystems.SubSystem;

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

        List<DataElement> qsysRemoteObjects = getRSESourceObjects(droppedObjects);
        List<RemoteObject> remoteObjects = new ArrayList<RemoteObject>();
        for (DataElement qsysRemoteObject : qsysRemoteObjects) {

            ISeriesObject iSeriesObject = new ISeriesObject(qsysRemoteObject);
            String connectionName = getConnectionName(qsysRemoteObject);
            String name = iSeriesObject.getName();
            String library = iSeriesObject.getLibrary();
            String type = iSeriesObject.getType();
            String description = iSeriesObject.getDescription();
            remoteObjects.add(new RemoteObject(connectionName, name, library, type, description));
        }
        receiver.setRemoteObjects(remoteObjects.toArray(new RemoteObject[remoteObjects.size()]));

        return Status.OK_STATUS;
    }

    private String getConnectionName(DataElement object) {
        return ISeriesDataElementHelper.getConnection(object).getAliasName();
    }

    private ArrayList<DataElement> getRSESourceObjects(String[] droppedObjects) {

        // Load objects from system
        ArrayList<DataElement> qsysRemoteObjects = new ArrayList<DataElement>();
        for (int i = 0; i < droppedObjects.length; i++) {
            String droppedObject = droppedObjects[i];

            Object anyObject = getObjectFor(droppedObject);
            if (anyObject instanceof DataElement) {
                DataElement qsysRemoteObject = (DataElement)anyObject;
                qsysRemoteObjects.add(qsysRemoteObject);
            }
        }

        return qsysRemoteObjects;
    }

    private Object getObjectFor(String droppedObject) {
        SystemRegistry registry = SystemPlugin.getTheSystemRegistry();

        // Get connection delimiter
        int connectionDelim = droppedObject.indexOf(":");
        if (connectionDelim == -1) {
            int profileDelim = droppedObject.indexOf(".");
            if (profileDelim != -1) {
                String profileId = droppedObject.substring(0, profileDelim);
                String connectionId = droppedObject.substring(profileDelim + 1, droppedObject.length());
                SystemProfile profile = registry.getSystemProfile(profileId);
                return registry.getConnection(profile, connectionId);
            }
        }

        // Get subsystem delimiter
        int subsystemDelim = droppedObject.indexOf(":", connectionDelim + 1);
        if (subsystemDelim == -1) {
            return registry.getSubSystem(droppedObject);
        }

        String subSystemId = droppedObject.substring(0, subsystemDelim);
        String objectKey = droppedObject.substring(subsystemDelim + 1, droppedObject.length());

        SubSystem subSystem = registry.getSubSystem(subSystemId);
        if (subSystem != null) {
            Object result = null;
            try {
                result = subSystem.getObjectWithAbsoluteName(objectKey);
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
