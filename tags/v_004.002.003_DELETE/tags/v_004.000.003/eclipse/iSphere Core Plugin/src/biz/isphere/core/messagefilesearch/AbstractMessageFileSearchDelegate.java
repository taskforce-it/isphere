/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.util.HashMap;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.internal.ISeries;

public abstract class AbstractMessageFileSearchDelegate {

    private Shell shell;

    public AbstractMessageFileSearchDelegate(Shell shell) {
        this.shell = shell;
    }

    public boolean addElements(HashMap<String, SearchElement> searchElements, String library, String messageFile) throws Exception {

        String objectFilterString = produceFilterString(library, messageFile, ISeries.MSGF);

        return addElementsFromFilterString(searchElements, objectFilterString);
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
                        } else if (isMessageFile(element)) {
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

    protected abstract String produceFilterString(String library, String messageFile, String objectType);

    protected abstract void displaySystemErrorMessage(Object object);

    protected abstract boolean isSystemMessageObject(Object object);

    protected abstract boolean isLibrary(Object object);

    protected abstract boolean isMessageFile(Object object);

    private boolean addElementsFromLibrary(HashMap<String, SearchElement> searchElements, Object library) throws Exception {

        String filterString = produceFilterString(getResourceName(library), "*", ISeries.MSGF);

        Object[] messageFiles = null;

        try {
            messageFiles = resolveFilterString(filterString);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            displaySystemErrorMessage(e);
            return false;
        }

        if ((messageFiles == null) || (messageFiles.length == 0)) {
            return true;
        }

        Object firstObject = messageFiles[0];
        if (isSystemMessageObject(firstObject)) {
            displaySystemErrorMessage(firstObject);
            return true;
        }

        for (int idx2 = 0; idx2 < messageFiles.length; idx2++) {
            addElement(searchElements, messageFiles[idx2]);
        }

        return true;

    }

    protected abstract String getResourceLibrary(Object resource);

    protected abstract String getResourceName(Object resource);

    protected abstract String getResourceDescription(Object resource);

    /**
     * Adds an element to the list of elements that are searched for a given
     * search string.
     * 
     * @param searchElements - list of elements that are searched
     * @param messageFile - message file that is added to the list
     */
    public void addElement(HashMap<String, SearchElement> searchElements, Object messageFile) {

        String library = getResourceLibrary(messageFile);
        String file = getResourceName(messageFile);
        String description = getResourceDescription(messageFile);

        String tKey = library + "-" + file; //$NON-NLS-1$
        if (!searchElements.containsKey(tKey)) {
            SearchElement aSearchElement = new SearchElement();
            aSearchElement.setLibrary(library);
            aSearchElement.setMessageFile(file);
            aSearchElement.setDescription(description);
            searchElements.put(tKey, aSearchElement);
        }
    }

    protected Shell getShell() {
        return shell;
    }

    protected abstract Object[] resolveFilterString(String filterString) throws Exception;

}
