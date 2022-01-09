/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.contentproviders;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;

public class JournalPropertiesContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private Object[] input;

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        if (newInput instanceof Object[]) {
            Object[] inputArray = (Object[])newInput;

            for (Object inputObject : inputArray) {
                if (!(inputObject instanceof JournalProperties)) {
                    input = null;
                    return;
                }
            }

            if (input == null || !equalInput((Object[])newInput, input)) {
                input = (Object[])newInput;
            }
        } else {

            input = null;
        }
    }

    private boolean equalInput(Object[] newInput, Object[] currentInput) {

        int newInputLength = newInput.length;
        int currentInputLength = currentInput.length;
        JournalProperties newInputObject;
        JournalProperties oldInputObject;

        if (newInputLength != currentInputLength) {
            return false;
        } else {
            for (int i = 0; i < newInputLength; i++) {
                newInputObject = (JournalProperties)newInput[i];
                oldInputObject = (JournalProperties)currentInput[i];

                if (!newInputObject.getJournalEntry().equals(oldInputObject.getJournalEntry())) {
                    return false;
                }
            }
            return true;
        }
    }

    public Object[] getChildren(Object parent) {
        if (parent instanceof JournalProperties) {
            return ((JournalProperties)parent).toArray();
        } else if (parent instanceof JOESDProperty) {
            return ((JOESDProperty)parent).toPropertyArray();
        } else {
            return new Object[0];
        }
    }

    public Object getParent(Object element) {
        if (element instanceof JournalProperty) {
            return ((JournalProperty)element).parent;
        } else {
            return null;
        }
    }

    public boolean hasChildren(Object element) {

        if (element instanceof JournalProperties) {
            return true;
        } else if (element instanceof JOESDProperty) {
            return true;
        } else {

            return false;
        }
    }

    public Object[] getElements(Object inputElement) {
        return input;
    }
}
