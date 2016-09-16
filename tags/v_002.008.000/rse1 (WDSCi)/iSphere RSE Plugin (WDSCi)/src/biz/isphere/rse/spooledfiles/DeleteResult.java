/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Result of a "delete spooled file" task.
 * 
 * @author Thomas Raddatz
 */
public class DeleteResult {

    /**
     * Inner class representing a deleted spooled file.
     * 
     * @author Thomas Raddatz
     */
    private class Entry {

        private SpooledFileResource spooledfileResource;

        private String message;

        public Entry(SpooledFileResource aSpooledFileResource, String aMessage) {
            spooledfileResource = aSpooledFileResource;
            message = aMessage;
        }

        public boolean isError() {
            return message != null;
        }

        public SpooledFileResource getSpooledFileResource() {
            return spooledfileResource;
        }

    }

    /*
     * Implementation of DeleteResult
     */

    private ArrayList<Entry> deleteResults;

    public DeleteResult() {
        deleteResults = new ArrayList<Entry>();
    }

    public void add(SpooledFileResource aSpooledFileResource, String aMessage) {
        Entry entry = new Entry(aSpooledFileResource, aMessage);
        deleteResults.add(entry);
    }

    public Vector<SpooledFileResource> getDeletedSpooledFiles() {
        Vector<SpooledFileResource> spooledFileResources = new Vector<SpooledFileResource>();
        for (Entry deleteResult : deleteResults) {
            if (!deleteResult.isError()) {
                spooledFileResources.add(deleteResult.getSpooledFileResource());
            }
        }
        return spooledFileResources;
    }

}
