/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.streamfilesearch;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.internal.exception.InvalidFilterException;

public abstract class AbstractStreamFileSearchDelegate {

    private Shell shell;
    private IProgressMonitor monitor;

    public AbstractStreamFileSearchDelegate(Shell shell) {
        this(shell, null);
    }

    public AbstractStreamFileSearchDelegate(Shell shell, IProgressMonitor monitor) {
        this.shell = shell;
        this.monitor = monitor;
    }

    public boolean addElements(HashMap<String, SearchElement> searchElements, String path, String file, StreamFileSearchFilter filter)
        throws Exception {

        String streamFileFilterString = produceStreamFileFilterString(path, file); // $NON-NLS-1$

        return addElementsFromFilterString(searchElements, filter, streamFileFilterString);
    }

    protected abstract String produceStreamFileFilterString(String path, String file);

    protected abstract String getResourcePath(Object resource);

    protected abstract String getResourceDirectory(Object resource);

    protected abstract String getResourceName(Object resource);

    protected abstract String getResourceType(Object resource);

    public boolean addElementsFromFilterString(HashMap<String, SearchElement> searchElements, StreamFileSearchFilter filter, String... filterStrings)
        throws Exception {

        boolean doContinue = true;
        Object[] children = null;

        for (int idx = 0; idx < filterStrings.length; idx++) {

            if (isCanceled()) {
                break;
            }

            children = resolveFilterString(filterStrings[idx]);

            if ((children != null) && (children.length != 0)) {

                Object firstObject = children[0];

                if (isSystemMessageObject(firstObject)) {
                    throwSystemErrorMessage(firstObject);
                } else {
                    for (int idx2 = 0; idx2 < children.length; idx2++) {
                        if (isCanceled()) {
                            break;
                        }
                        Object element = children[idx2];
                        if (isDirectory(element)) {
                            String path = getResourcePath(element);
                            String file = getFileFromFilterString(filterStrings[idx]);
                            String filterString = produceStreamFileFilterString(path, file);
                            doContinue = addElementsFromFilterString(searchElements, filter, filterString);
                        } else if (isStreamFile(element)) {
                            addElement(searchElements, filter, element);
                        }

                        if (!doContinue) {
                            break;
                        }
                    }
                }
            }
        }

        return true;
    }

    protected abstract void throwSystemErrorMessage(Object object) throws InvalidFilterException;

    protected abstract boolean isSystemMessageObject(Object object);

    protected abstract boolean isDirectory(Object object);

    protected abstract boolean isStreamFile(Object object);

    protected abstract String getDirectoryFromFilterString(String filterString);

    protected abstract String getFileFromFilterString(String filterString);

    /**
     * Adds an element to the list of elements that are searched for a given
     * search string.
     * 
     * @param searchElements - list of elements that are searched
     * @param streamFile - message file that is added to the list
     */
    private void addElement(HashMap<String, SearchElement> searchElements, StreamFileSearchFilter filter, Object streamFile) {

        String directory = getResourceDirectory(streamFile);
        String file = getResourceName(streamFile);
        String type = getResourceType(streamFile);

        SearchElement aSearchElement = new SearchElement();
        aSearchElement.setDirectory(directory);
        aSearchElement.setStreamFile(file);
        aSearchElement.setType(type);

        if (filter.isItemSelected(aSearchElement)) {
            addSearchElement(searchElements, aSearchElement);
        }
    }

    private void addSearchElement(HashMap<String, SearchElement> searchElements, SearchElement aSearchElement) {
        String tKey = aSearchElement.getDirectory() + "/" + aSearchElement.getStreamFile(); //$NON-NLS-1$ //$NON-NLS-2$
        if (!searchElements.containsKey(tKey)) {
            searchElements.put(tKey, aSearchElement);
        }
    }

    public HashMap<String, SearchElement> createHashMap(ArrayList<SearchElement> filteredElements) {

        HashMap<String, SearchElement> map = new HashMap<String, SearchElement>();

        for (SearchElement searchElement : filteredElements) {
            addSearchElement(map, searchElement);
        }

        return map;
    }

    protected abstract Object[] resolveFilterString(String filterString) throws Exception;

    private boolean isCanceled() {

        if (monitor == null) {
            return false;
        }

        return monitor.isCanceled();
    }
}
