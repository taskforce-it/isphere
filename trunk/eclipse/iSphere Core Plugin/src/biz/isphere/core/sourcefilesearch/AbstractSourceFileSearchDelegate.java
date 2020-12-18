/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.exception.InvalidFilterException;

public abstract class AbstractSourceFileSearchDelegate {

    private Shell shell;
    private IProgressMonitor monitor;

    public AbstractSourceFileSearchDelegate(Shell shell) {
        this(shell, null);
    }

    public AbstractSourceFileSearchDelegate(Shell shell, IProgressMonitor monitor) {
        this.shell = shell;
        this.monitor = monitor;
    }

    public boolean addElements(HashMap<String, SearchElement> searchElements, String library, String sourceFile, String sourceMember)
        throws Exception {

        ISeriesMemberFilterString memberFilterString = new ISeriesMemberFilterString();
        memberFilterString.setLibrary(library);
        memberFilterString.setFile(sourceFile);
        memberFilterString.setMember(sourceMember);
        memberFilterString.setMemberType("*"); //$NON-NLS-1$

        return addElementsFromFilterString(searchElements, memberFilterString.toString());
    }

    public boolean addElementsFromFilterString(HashMap<String, SearchElement> searchElements, String... filterStrings) throws Exception {

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
                        if (isLibrary(element)) {
                            doContinue = addElementsFromLibrary(searchElements, element);
                        } else if (isSourceFile(element)) {
                            addElementsFromSourceFile(searchElements, getResourceLibrary(element), getResourceName(element));
                        } else if (isSourceMember(element)) {
                            addElement(searchElements, element);
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

    protected abstract boolean isLibrary(Object object);

    protected abstract boolean isSourceFile(Object object);

    protected abstract boolean isSourceMember(Object object);

    private boolean addElementsFromLibrary(HashMap<String, SearchElement> searchElements, Object library) throws Exception {

        Object[] sourceFiles = null;

        ISeriesObjectFilterString objectFilterString = new ISeriesObjectFilterString();
        objectFilterString.setObject("*"); //$NON-NLS-1$
        objectFilterString.setObjectType(ISeries.FILE);
        String attributes = "*FILE:PF-SRC *FILE:PF38-SRC"; //$NON-NLS-1$
        objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));

        objectFilterString.setLibrary(getResourceName(library));

        try {
            sourceFiles = resolveFilterString(objectFilterString.toString());
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            throwSystemErrorMessage(e);
            return false;
        }

        if ((sourceFiles == null) || (sourceFiles.length == 0)) {
            return true;
        }

        Object firstObject = sourceFiles[0];
        if (isSystemMessageObject(firstObject)) {
            throwSystemErrorMessage(firstObject);
            return true;
        }

        for (int idx2 = 0; idx2 < sourceFiles.length; idx2++) {

            if (isCanceled()) {
                break;
            }

            Object element = sourceFiles[idx2];
            if (isSourceFile(element)) {
                addElementsFromSourceFile(searchElements, getResourceLibrary(element), getResourceName(element));
            }
        }

        return true;
    }

    protected abstract String getResourceLibrary(Object resource);

    protected abstract String getResourceName(Object resource);

    protected abstract String getMemberResourceLibrary(Object resource);

    protected abstract String getMemberResourceFile(Object resource);

    protected abstract String getMemberResourceName(Object resource);

    protected abstract String getMemberResourceType(Object resource);

    protected abstract String getMemberResourceDescription(Object resource);

    protected abstract Date getMemberLastChangedDate(Object resource);

    private void addElementsFromSourceFile(HashMap<String, SearchElement> searchElements, String library, String sourceFile) throws Exception {

        ISeriesMemberFilterString memberFilterString = new ISeriesMemberFilterString();
        memberFilterString.setLibrary(library);
        memberFilterString.setFile(sourceFile);
        memberFilterString.setMember("*"); //$NON-NLS-1$
        memberFilterString.setMemberType("*"); //$NON-NLS-1$

        addElementsFromFilterString(searchElements, memberFilterString.toString());
    }

    /**
     * Adds an element to the list of elements that are searched for a given
     * search string.
     * 
     * @param searchElements - list of elements that are searched
     * @param sourceMember - message file that is added to the list
     */
    private void addElement(HashMap<String, SearchElement> searchElements, Object sourceMember) {

        String library = getMemberResourceLibrary(sourceMember);
        String file = getMemberResourceFile(sourceMember);
        String member = getMemberResourceName(sourceMember);
        String type = getMemberResourceType(sourceMember);
        String description = getMemberResourceDescription(sourceMember);

        SearchElement aSearchElement = new SearchElement();
        aSearchElement.setLibrary(library);
        aSearchElement.setFile(file);
        aSearchElement.setMember(member);
        aSearchElement.setType(type);
        aSearchElement.setDescription(description);

        addSearchElement(searchElements, aSearchElement);
    }

    private void addSearchElement(HashMap<String, SearchElement> searchElements, SearchElement aSearchElement) {
        String tKey = aSearchElement.getLibrary() + "-" + aSearchElement.getFile() + "-" + aSearchElement.getMember(); //$NON-NLS-1$ //$NON-NLS-2$
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

    protected Shell getShell() {
        return shell;
    }

    protected abstract Object[] resolveFilterString(String filterString) throws Exception;

    private boolean isCanceled() {

        if (monitor == null) {
            return false;
        }

        return monitor.isCanceled();
    }
}
