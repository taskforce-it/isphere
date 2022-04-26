/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SearchDialog extends AbstractSearchDialog {

    private static final String SHOW_RECORDS = "showRecords"; //$NON-NLS-1$

    private HashMap<String, SearchElement> searchElements;
    private Button showAllRecordsButton;

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell, SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN, false, false);
        this.searchElements = searchElements;
    }

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, SearchArgument.MAX_SOURCE_FILE_SEARCH_COLUMN, searchArgumentsListEditor, true);
        this.searchElements = searchElements;
    }

    @Override
    public String getTitle() {
        return Messages.iSphere_Source_File_Search;
    }

    @Override
    public String[] getItems() {
        ArrayList<String> items = new ArrayList<String>();
        SortedSet<String> keys = new TreeSet<String>(searchElements.keySet());
        Iterator<String> _iterator = keys.iterator();
        while (_iterator.hasNext()) {
            String key = _iterator.next();
            SearchElement value = searchElements.get(key);
            String item = value.getLibrary() + "/" + value.getFile() + "(" + value.getMember() + ")" + " - \"" + value.getDescription() + "\"";
            items.add(item);
        }
        String[] _items = new String[items.size()];
        items.toArray(_items);
        return _items;
    }

    @Override
    public String getSearchArgument() {
        return Preferences.getInstance().getSourceFileSearchString();
    }

    @Override
    public void setSearchArgument(String argument) {
        Preferences.getInstance().setSourceFileSearchString(argument);
    }

    private boolean isShowAllRecords() {
        return showAllRecordsButton.getSelection();
    }

    @Override
    public void createOptionsGroup(Composite container) {

        Group groupOptions = new Group(container, SWT.NONE);
        groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        groupOptions.setText(Messages.Options);
        groupOptions.setLayout(new GridLayout(2, false));

        GridData tGridData;
        showAllRecordsButton = WidgetFactory.createCheckbox(groupOptions);
        showAllRecordsButton.setText(Messages.ShowAllRecords);
        showAllRecordsButton.setToolTipText(Messages.Specify_whether_all_matching_records_are_returned);
        tGridData = new GridData(SWT.HORIZONTAL);
        tGridData.grabExcessHorizontalSpace = false;
        showAllRecordsButton.setLayoutData(tGridData);
    }

    @Override
    public void loadElementValues() {
        showAllRecordsButton.setSelection(loadBooleanValue(SHOW_RECORDS, true));
    };

    @Override
    public void saveElementValues() {
        storeValue(SHOW_RECORDS, isShowAllRecords());
    };

    @Override
    public void setElementsSearchOptions(SearchOptions _searchOptions) {
        _searchOptions.setShowAllItems(isShowAllRecords());
    };

}
