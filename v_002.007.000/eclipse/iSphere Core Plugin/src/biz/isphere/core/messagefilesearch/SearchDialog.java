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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SearchDialog extends AbstractSearchDialog {

    private HashMap<String, SearchElement> searchElements;
    private Button includeFirstLevelTextButton;
    private Button includeSecondLevelTextButton;

    // iSphere settings
    private static final String INCLUDE_FIRST_LEVEL_TEXT = "includeFirstLevelText";
    private static final String INCLUDE_SECOND_LEVEL_TEXT = "includeSecondLevelText";

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements) {
        super(parentShell, 132, false, false);
        this.searchElements = searchElements;
    }

    public SearchDialog(Shell parentShell, HashMap<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, 132, searchArgumentsListEditor, false);
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

    public boolean isIncludeFirstLevelText() {
        return includeFirstLevelTextButton.getSelection();
    }

    public boolean isIncludeSecondLevelText() {
        return includeSecondLevelTextButton.getSelection();
    }

    @Override
    public void addElements(Composite container) {

        Group groupOptions = new Group(container, SWT.NONE);
        groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        groupOptions.setText(Messages.Options);
        groupOptions.setLayout(new GridLayout(1, false));

        GridData tGridData;
        includeFirstLevelTextButton = WidgetFactory.createCheckbox(groupOptions);
        includeFirstLevelTextButton.setText(Messages.IncludeFirstLevelText);
        includeFirstLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_first_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL);
        tGridData.grabExcessHorizontalSpace = false;
        includeFirstLevelTextButton.setLayoutData(tGridData);
        includeFirstLevelTextButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setOKButtonEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        includeSecondLevelTextButton = WidgetFactory.createCheckbox(groupOptions);
        includeSecondLevelTextButton.setText(Messages.IncludeSecondLevelText);
        includeSecondLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_second_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL);
        tGridData.grabExcessHorizontalSpace = false;
        includeSecondLevelTextButton.setLayoutData(tGridData);
        includeSecondLevelTextButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setOKButtonEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

    }

    @Override
    public void loadElementValues() {
        includeFirstLevelTextButton.setSelection(loadBooleanValue(INCLUDE_FIRST_LEVEL_TEXT, true));
        includeSecondLevelTextButton.setSelection(loadBooleanValue(INCLUDE_SECOND_LEVEL_TEXT, false));
        if (!isIncludeFirstLevelText() && !isIncludeSecondLevelText()) {
            includeFirstLevelTextButton.setSelection(true);
        }
    };

    @Override
    public void saveElementValues() {
        storeValue(INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        storeValue(INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
    };

    @Override
    public boolean checkElements() {
        return isIncludeFirstLevelText() || isIncludeSecondLevelText();
    }

    @Override
    public void setElementsSearchOptions(SearchOptions _searchOptions) {
        _searchOptions.setOption(SearchExec.INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        _searchOptions.setOption(SearchExec.INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
        // _searchOptions.setOption(SearchExec.INCLUDE_FIRST_LEVEL_TEXT, new
        // Boolean(isIncludeFirstLevelText()));
        // _searchOptions.setOption(SearchExec.INCLUDE_SECOND_LEVEL_TEXT, new
        // Boolean(isIncludeSecondLevelText()));
    };

}
