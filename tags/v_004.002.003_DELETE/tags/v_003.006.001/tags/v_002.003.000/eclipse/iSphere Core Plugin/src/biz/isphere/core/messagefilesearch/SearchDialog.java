/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;

public class SearchDialog extends AbstractSearchDialog {

    private HashMap<String, SearchElement> searchElements;
    
    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell, 132, false);
        this.searchElements = searchElements;
    }

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, 132, searchArgumentsListEditor);
        this.searchElements = searchElements;
    }

    @Override
    public String getTitle() {
        return Messages.iSphere_Message_File_Search;
    }

    @Override
    public String[] getItems() {
        ArrayList<String> items = new ArrayList<String>();
        SortedSet<String> keys = new TreeSet<String>(searchElements.keySet());
        Iterator<String> _iterator = keys.iterator();
        while (_iterator.hasNext()) {
            String key = _iterator.next();
            SearchElement value = searchElements.get(key);
            String item = value.getLibrary() + "/" + value.getMessageFile() + " - \"" + value.getDescription() + "\"";
            items.add(item);
        }
        String[] _items = new String[items.size()];
        items.toArray(_items);
        return _items;
    }

    @Override
    public String getSearchArgument() {
        return Preferences.getInstance().getMessageFileSearchString();
    }

    @Override
    public void setSearchArgument(String argument) {
        Preferences.getInstance().setMessageFileSearchString(argument);
    }

}
