/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.internal.ISeries;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;

public abstract class AbstractSourceFileSearchDelegate {

    private Shell shell;

    public AbstractSourceFileSearchDelegate(Shell shell) {
        this.shell = shell;
    }

    public boolean addElementFromFilterString(HashMap<String, SearchElement> searchElements, String filterString) throws Exception {
        return addElementsFromFilterString(searchElements, filterString);
    }

    public boolean addElementsFromFilterString(HashMap<String, SearchElement> searchElements, String... filterStrings) throws Exception {

        boolean doContinue = true;
        Object[] children = null;

        for (int idx = 0; idx < filterStrings.length; idx++) {

            children = resolveFilterString(filterStrings[idx]);

            if ((children != null) && (children.length != 0)) {

                Object firstObject = children[0];

                if (isSystemMessageObject(firstObject)) {
                    displaySystemErrorMessage(firstObject);
                } else {
                    for (int idx2 = 0; idx2 < children.length; idx2++) {
                        Object element = children[idx2];
                        if (isLibrary(element)) {
                            doContinue = addElementsFromLibrary(searchElements, element);
                        } else if (isSourceFile(element)) {
                            addElementsFromSourceFile(searchElements, getResourceLibrary(element), getResourceName(element));
                        } else if (isMember(element)) {
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

    protected abstract void displaySystemErrorMessage(Object object);

    protected abstract boolean isSystemMessageObject(Object object);

    protected abstract boolean isLibrary(Object object);

    protected abstract boolean isSourceFile(Object object);

    protected abstract boolean isMember(Object object);

    private boolean addElementsFromLibrary(HashMap<String, SearchElement> searchElements, Object library) throws Exception {

        Vector<Object> libElements = new Vector<Object>();
        Object[] children = null;

        ISeriesObjectFilterString objectFilterString = new ISeriesObjectFilterString();
        objectFilterString.setObject("*");
        objectFilterString.setObjectType(ISeries.FILE);
        String attributes = "*FILE:PF-SRC *FILE:PF38-SRC";
        objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));

        objectFilterString.setLibrary(getResourceName(library));
        String filterString = objectFilterString.toString();

        try {
            children = resolveFilterString(filterString);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            displaySystemErrorMessage(e);
            return false;
        }

        if ((children == null) || (children.length == 0)) {
            return true;
        }

        Object firstObject = children[0];
        if (isSystemMessageObject(firstObject)) {
            displaySystemErrorMessage(firstObject);
            return true;
        }

        for (int idx2 = 0; idx2 < children.length; idx2++) {
            libElements.addElement(children[idx2]);
        }

        for (Enumeration<Object> enumeration = libElements.elements(); enumeration.hasMoreElements();) {
            library = enumeration.nextElement();
            addElementsFromSourceFile(searchElements, getResourceLibrary(library), getResourceName(library));
        }

        return true;

    }

    protected abstract String getResourceLibrary(Object resource);

    protected abstract String getResourceName(Object resource);

    protected abstract String getMemberResourceLibrary(Object resource);

    protected abstract String getMemberResourceFile(Object resource);

    protected abstract String getMemberResourceName(Object resource);

    protected abstract String getMemberResourceDescription(Object resource);

    private void addElementsFromSourceFile(HashMap<String, SearchElement> searchElements, String library, String sourceFile) throws Exception {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*");
        _memberFilterString.setMemberType("*");

        String[] _filterStrings = new String[1];
        _filterStrings[0] = _memberFilterString.toString();
        addElementsFromFilterString(searchElements, _filterStrings);

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
        
        String tKey = library + "-" + file + "-" + member;
        if (!searchElements.containsKey(tKey)) {
            SearchElement aSearchElement = new SearchElement();
            aSearchElement.setLibrary(library);
            aSearchElement.setFile(file);
            aSearchElement.setMember(member);
            aSearchElement.setDescription(getMemberResourceDescription(sourceMember));
            searchElements.put(tKey, aSearchElement);
        }
    }

    protected Shell getShell() {
        return shell;
    }

    protected abstract Object[] resolveFilterString(String filterString) throws Exception;

}
