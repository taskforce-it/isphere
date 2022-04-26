/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import org.eclipse.swt.graphics.Image;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.swt.widgets.objectselector.SelectQSYSObjectDialog;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.Job;

/**
 * This class is a factory for producing list items suitable for a
 * {@link SelectQSYSObjectDialog}.
 */
public final class ListItemsFactory {

    private static final String ASTERISK = "*"; //$NON-NLS-1$

    /**
     * Produces a <i>System</i> root node.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param connectionName - connection name of the system.
     * @param objectTypeFilter - type of the object that is to be opened
     * @return system list item
     */
    public static SystemItem createSystem(AS400 system, String connectionName, String objectTypeFilter) {
        Image image = ISpherePlugin.getDefault().getImage(ISpherePlugin.IMAGE_SYSTEM);
        return new SystemItem(system, connectionName, image, objectTypeFilter);
    }

    /**
     * Produces a <i>Current Library</i> (*CURLIB) list item.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param objectTypeFilter - type of the object that is to be opened
     * @return current library list item
     */
    public static CurrentLibraryItem createCurrentLibrary(AS400 system, String objectTypeFilter) {
        try {
            Image image = ISpherePlugin.getDefault().getImage(ISpherePlugin.IMAGE_CURRENT_LIBRARY);
            Job[] jobs = system.getJobs(AS400.COMMAND);
            if (jobs.length > 0) {
                Job job = jobs[0];
                if (job.getCurrentLibraryExistence()) {
                    String libraryName = job.getCurrentLibrary();
                    return new CurrentLibraryItem(system, libraryName, image, objectTypeFilter);
                }
            }
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not resolve current library ***", e); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Produces a <i>Library List</i> (*LIBL) list item.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param objectTypeFilter - type of the object that is to be opened
     * @return library list list item
     */
    public static LibraryListItem createLibraryList(AS400 system, String objectTypeFilter) {
        Image image = ISpherePlugin.getDefault().getImage(ISpherePlugin.IMAGE_LIBRARY_LIST);
        return new LibraryListItem(system, image, objectTypeFilter);
    }

    /**
     * Produces a <i>Library</i> list item.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param libraryName - name of the library
     * @param objectTypeFilter - type of the object that is to be opened
     * @return library list item
     */
    public static LibraryItem createLibrary(AS400 system, String libraryName, String objectTypeFilter) {
        Image image = ISpherePlugin.getDefault().getImage(ISpherePlugin.IMAGE_LIBRARY);
        return new LibraryItem(system, libraryName, image, objectTypeFilter);
    }

    /**
     * Produces a <i>Message file</i> list item.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param libraryName - name of the library
     * @param messageFileName - name of the message file
     * @param objectTypeFilter - type of the object that is to be opened
     * @return message file list item
     */
    public static MessageFileItem createMessageFile(AS400 system, String libraryName, String messageFileName, String objectTypeFilter) {
        Image image = ISpherePlugin.getDefault().getImage(ISpherePlugin.IMAGE_MESSAGE_FILE);
        return new MessageFileItem(system, libraryName, messageFileName, image, objectTypeFilter);
    }

    /**
     * Produces a list item of any object type.
     * 
     * @param system - the system (AS400) the object resides on.
     * @param libraryName - name of the library
     * @param objectName - name of the object
     * @param objectType - type of the object
     * @param objectTypeFilter - type of the object that is to be opened
     * @return object list item
     */
    public static ObjectItem createObject(AS400 system, String libraryName, String objectName, String objectType, String objectTypeFilter) {

        if (!objectType.startsWith(ASTERISK)) {
            objectType = ASTERISK + objectType;
        }

        if (ISeries.MSGF.equals(objectType)) {
            return createMessageFile(system, libraryName, objectName, objectTypeFilter);
        }

        throw new IllegalArgumentException("Invalid objecttype: " + objectTypeFilter); //$NON-NLS-1$
    }
}
