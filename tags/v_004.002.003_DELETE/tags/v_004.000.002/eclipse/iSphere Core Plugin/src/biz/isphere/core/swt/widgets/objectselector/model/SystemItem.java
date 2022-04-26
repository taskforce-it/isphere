/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import biz.isphere.core.internal.ISeries;

import com.ibm.as400.access.AS400;

public class SystemItem extends AbstractListItem {

    private Map<String, AbstractListItem> specialLibraries;
    private Map<String, AbstractListItem> libraries;

    private List<AbstractListItem> children;

    public SystemItem(AS400 system, String connectionName, Image image, String objectType) {
        super(system, image, connectionName, objectType);

        this.specialLibraries = new HashMap<String, AbstractListItem>();
        this.libraries = new HashMap<String, AbstractListItem>();
    }

    public CurrentLibraryItem addCurrentLibrary() {

        if (specialLibraries.containsKey(ISeries.SPCVAL_CURLIB)) {
            return null;
        }

        CurrentLibraryItem currentLibraryibraryItem = ListItemsFactory.createCurrentLibrary(getSystem(), getObjectTypeFilter());
        if (currentLibraryibraryItem != null) {
            specialLibraries.put(currentLibraryibraryItem.getName(), currentLibraryibraryItem);
        }

        return currentLibraryibraryItem;
    }

    public LibraryListItem addLibraryList() {

        if (specialLibraries.containsKey(ISeries.SPCVAL_LIBL)) {
            return null;
        }

        LibraryListItem libraryListItem = ListItemsFactory.createLibraryList(getSystem(), getObjectTypeFilter());
        specialLibraries.put(libraryListItem.getName(), libraryListItem);

        return libraryListItem;
    }

    public void addLibraries(String[] libraryNames) {

        for (String libraryName : libraryNames) {
            addLibrary(libraryName);
        }
    }

    public LibraryItem addLibrary(String libraryName) {

        if (libraries.containsKey(libraryName)) {
            return null;
        }

        LibraryItem libraryItem = ListItemsFactory.createLibrary(getSystem(), libraryName, getObjectTypeFilter());
        libraries.put(libraryItem.getName(), libraryItem);

        return libraryItem;
    }

    @Override
    public String getLabel() {
        return super.getName();
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public AbstractListItem[] resolveChildren() {

        if (children == null) {
            children = new LinkedList<AbstractListItem>();
            addItems(specialLibraries.values());
            addItems(libraries.values());
        }

        return children.toArray(new AbstractListItem[children.size()]);
    }

    private void addItems(Collection<AbstractListItem> listItems) {
        Collections.sort(new ArrayList<AbstractListItem>(listItems), new Comparator<AbstractListItem>() {
            public int compare(AbstractListItem me, AbstractListItem other) {
                if (other == null) {
                    return 1;
                } else {
                    return me.getName().compareTo(other.getName());
                }
            }
        });

        children.addAll(listItems);
    }
}
