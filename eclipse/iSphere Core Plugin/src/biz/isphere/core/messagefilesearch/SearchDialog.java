/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.AbstractSearchDialog;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchOptionConfig;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SearchDialog extends AbstractSearchDialog<SearchElement> {

    private static final String EXTERNAL_INCLUDE_FIRST_LEVEL_TEXT = "includeFirstLevelText";
    private static final String EXTERNAL_INCLUDE_SECOND_LEVEL_TEXT = "includeSecondLevelText";
    private static final String EXTERNAL_INCLUDE_MESSAGE_ID = "includeMessageId";

    private Map<String, SearchElement> searchElements;
    private Button includeFirstLevelTextButton;
    private Button includeSecondLevelTextButton;
    private Button includeMessageIdButton;

    public SearchDialog(Shell parentShell, Map<String, SearchElement> searchElements) {
        super(parentShell, 132, false, false);
        this.searchElements = searchElements;
    }

    public SearchDialog(Shell parentShell, Map<String, SearchElement> searchElements, boolean searchArgumentsListEditor) {
        super(parentShell, 132, searchArgumentsListEditor, false, SearchOptionConfig.getAdditionalMessageFileSearchOptions());
        this.searchElements = searchElements;
    }

    @Override
    protected String getTitle() {
        return Messages.iSphere_Message_File_Search;
    }

    @Override
    protected String[] getItems() {
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
    public ArrayList<SearchElement> getSelectedElements() {

        ArrayList<SearchElement> selectedSearchElements = new ArrayList<SearchElement>();

        Collection<SearchElement> allSearchElements = searchElements.values();
        for (SearchElement searchElement : allSearchElements) {
            selectedSearchElements.add(searchElement);
        }

        return selectedSearchElements;
    }

    @Override
    protected String getSearchArgument() {
        return Preferences.getInstance().getMessageFileSearchString();
    }

    @Override
    protected void setSearchArgument(String argument) {
        Preferences.getInstance().setMessageFileSearchString(argument);
    }

    private boolean isIncludeFirstLevelText() {
        return includeFirstLevelTextButton.getSelection();
    }

    private boolean isIncludeSecondLevelText() {
        return includeSecondLevelTextButton.getSelection();
    }

    private boolean isIncludeMessageId() {
        return includeMessageIdButton.getSelection();
    }

    @Override
    protected void createOptionsGroup(Composite container) {

        Group groupOptions = new Group(container, SWT.NONE);
        groupOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        groupOptions.setText(Messages.Options);
        groupOptions.setLayout(new GridLayout(2, false));

        GridData tGridData;
        includeFirstLevelTextButton = WidgetFactory.createCheckbox(groupOptions);
        includeFirstLevelTextButton.setText(Messages.IncludeFirstLevelText);
        includeFirstLevelTextButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_first_level_message_text);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
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
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 2, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeSecondLevelTextButton.setLayoutData(tGridData);
        includeSecondLevelTextButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setOKButtonEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        includeMessageIdButton = WidgetFactory.createCheckbox(groupOptions);
        includeMessageIdButton.setText(Messages.IncludeMessageId);
        includeMessageIdButton.setToolTipText(Messages.Specify_whether_or_not_to_include_the_message_id);
        tGridData = new GridData(SWT.HORIZONTAL, SWT.DEFAULT, false, false, 1, 1);
        tGridData.grabExcessHorizontalSpace = false;
        includeMessageIdButton.setLayoutData(tGridData);
        includeMessageIdButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                setOKButtonEnablement();
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
    }

    @Override
    protected void loadElementValues() {
        includeFirstLevelTextButton.setSelection(loadBooleanValue(EXTERNAL_INCLUDE_FIRST_LEVEL_TEXT, true));
        includeSecondLevelTextButton.setSelection(loadBooleanValue(EXTERNAL_INCLUDE_SECOND_LEVEL_TEXT, false));
        includeMessageIdButton.setSelection(loadBooleanValue(EXTERNAL_INCLUDE_MESSAGE_ID, false));
        if (!isIncludeFirstLevelText() && !isIncludeSecondLevelText() && !isIncludeMessageId()) {
            includeFirstLevelTextButton.setSelection(true);
        }
    };

    @Override
    protected void saveElementValues() {
        storeValue(EXTERNAL_INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        storeValue(EXTERNAL_INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
        storeValue(EXTERNAL_INCLUDE_MESSAGE_ID, isIncludeMessageId());
    };

    @Override
    protected boolean checkElements() {
        return isIncludeFirstLevelText() || isIncludeSecondLevelText() || isIncludeMessageId();
    }

    @Override
    protected void setElementsSearchOptions(SearchOptions _searchOptions) {
        _searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_FIRST_LEVEL_TEXT, isIncludeFirstLevelText());
        _searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_SECOND_LEVEL_TEXT, isIncludeSecondLevelText());
        _searchOptions.setGenericOption(GenericSearchOption.MSGF_INCLUDE_MESSAGE_ID, isIncludeMessageId());
    };

    protected void setSearchOptionsEnablement(Event anEvent) {

        if (!(anEvent.data instanceof SearchOptionConfig)) {
            return;
        }

        SearchOptionConfig config = (SearchOptionConfig)anEvent.data;

        includeFirstLevelTextButton.setEnabled(config.isIncludeFirstLevelTextEnabled());
        includeSecondLevelTextButton.setEnabled(config.isIncludeSecondLevelTextEnabled());
        includeMessageIdButton.setEnabled(config.isIncludeMessageIdEnabled());

        super.setSearchOptionsEnablement(anEvent);
    }

}
