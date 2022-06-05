/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import biz.isphere.base.internal.BooleanHelper;
import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class SearchArgumentsListEditor implements Listener {

    private static final String CONFIG_DATA = "CONFIG_DATA";

    private static final String MATCH_ALL = "matchAll";
    private static final String NUM_CONDITIONS = "numberOfCompareConditions";
    private static final String COMPARE_CONDITION = "compareCondition";
    private static final String SEARCH_STRING = "searchString";
    private static final String CASE_SENSITIVE = "caseSensitive";
    private static final String REGULAR_EXPRESSION = "regularExpression";

    private Composite searchStringGroup;
    private ScrolledComposite scrollable;

    private Button[] rdoMatchOptions;

    private List<AbstractSearchArgumentEditor> searchArgumentEditors;
    private int maxNumSearchArguments;
    private SearchOptionConfig[] rdoAdditionalMatchOptions;
    private Listener listener;
    private boolean regularExpressionsOption;

    public SearchArgumentsListEditor(int aMaxNumSearchArguments) {
        this(aMaxNumSearchArguments, false);
    }

    public SearchArgumentsListEditor(int aMaxNumSearchArguments, boolean aRegularExpressionsOption) {
        this(aMaxNumSearchArguments, aRegularExpressionsOption, null);
    }

    public SearchArgumentsListEditor(int aMaxNumSearchArguments, boolean aRegularExpressionsOption, SearchOptionConfig[] anAdditionalMatchOptions) {
        regularExpressionsOption = aRegularExpressionsOption;
        maxNumSearchArguments = aMaxNumSearchArguments;
        rdoAdditionalMatchOptions = anAdditionalMatchOptions;
        listener = null;
    }

    public void setListener(Listener aListener) {
        listener = aListener;
    }

    public void createControl(Composite aParent) {

        Composite tMatchGroup = new Composite(aParent, SWT.NONE);
        FillLayout tMatchGroupLayout = new FillLayout(SWT.HORIZONTAL);
        tMatchGroupLayout.marginHeight = 5;
        tMatchGroup.setLayout(tMatchGroupLayout);

        List<SearchOptionConfig> rdoOptions = new ArrayList<SearchOptionConfig>();
        rdoOptions.add(new SearchOptionConfig(MatchOption.ALL, Messages.MatchAllConditions));
        rdoOptions.add(new SearchOptionConfig(MatchOption.ANY, Messages.MatchAnyCondition));

        if (rdoAdditionalMatchOptions != null) {
            for (int i = 0; i < rdoAdditionalMatchOptions.length; i++) {
                rdoOptions.add(rdoAdditionalMatchOptions[i]);
            }
        }

        rdoMatchOptions = new Button[rdoOptions.size()];
        for (int i = 0; i < rdoOptions.size(); i++) {
            rdoMatchOptions[i] = createSearchOption(tMatchGroup, rdoOptions.get(i));
        }

        Composite scrollableContainer = new Composite(aParent, SWT.NONE);
        scrollableContainer.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 135;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        scrollableContainer.setLayoutData(gd);

        scrollable = new ScrolledComposite(scrollableContainer, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrollable.setLayout(new GridLayout(1, false));
        scrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setExpandHorizontal(true);
        scrollable.setExpandVertical(true);

        searchStringGroup = new Composite(scrollable, SWT.NONE);
        searchStringGroup.setLayout(new GridLayout(1, false));
        searchStringGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setContent(searchStringGroup);

        searchArgumentEditors = new ArrayList<AbstractSearchArgumentEditor>();
    }

    private Button createSearchOption(Composite tMatchGroup, SearchOptionConfig searchOptionConfig) {

        Button rdoOption = WidgetFactory.createRadioButton(tMatchGroup);
        rdoOption.setText(searchOptionConfig.getLabel());
        rdoOption.setData(CONFIG_DATA, searchOptionConfig);
        rdoOption.addSelectionListener(new SearchOptionSelectionListener());

        return rdoOption;
    }

    protected boolean isRegularExpressions() {
        return regularExpressionsOption;
    }

    private void addSearchArgumentEditorAndLayout() {
        addSearchArgumentEditor(null);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);
    }

    private void addSearchArgumentEditorAndLayout(Button aButton) {
        AbstractSearchArgumentEditor tEditor = addSearchArgumentEditor(aButton);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);

        scrollable.setOrigin(tEditor.getBounds().x, tEditor.getBounds().y - tEditor.getBounds().height - 5);
        tEditor.setFocus();

        setAddButtonEnablement();
    }

    private AbstractSearchArgumentEditor addSearchArgumentEditor(Button aButton) {
        AbstractSearchArgumentEditor tEditor = createEditor(searchStringGroup);

        if (aButton == null) {
            searchArgumentEditors.add(tEditor);
        } else {
            searchArgumentEditors.add(findSearchArgumentEditor(aButton) + 1, tEditor);
        }

        rearrangeSearchArgumentEditors();

        Button currentMatchOption = getCurrentMatchOption();
        applySearchOptionConfig(currentMatchOption);

        return tEditor;
    }

    private void removeSearchArgumentEditor(Button aButton) {
        if (searchArgumentEditors.size() == 1) {
            return;
        }
        removeSearchArgumentEditor(findSearchArgumentEditor(aButton));
    }

    private void removeSearchArgumentEditor(int anEditor) {
        if (anEditor < 0) {
            return;
        }
        searchArgumentEditors.get(anEditor).dispose();
        searchArgumentEditors.remove(anEditor);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);

        if (anEditor > searchArgumentEditors.size() - 1) {
            searchArgumentEditors.get(searchArgumentEditors.size() - 1).setFocus();
        } else {
            searchArgumentEditors.get(anEditor).setFocus();
        }

        setAddButtonEnablement();
    }

    private void setAddButtonEnablement() {
        boolean isEnabled = false;
        if (searchArgumentEditors.size() < maxNumSearchArguments) {
            isEnabled = true;
        }
        for (AbstractSearchArgumentEditor tEditor : searchArgumentEditors) {
            tEditor.setAddButtonEnablement(isEnabled);
        }
    }

    private int findSearchArgumentEditor(Button aButton) {
        for (int i = 0; i < searchArgumentEditors.size(); i++) {
            if (searchArgumentEditors.get(i).hasButton(aButton)) {
                return i;
            }
        }
        return -1;
    }

    private void rearrangeSearchArgumentEditors() {
        for (AbstractSearchArgumentEditor tEditor : searchArgumentEditors) {
            tEditor.setParent(scrollable);
        }
        for (AbstractSearchArgumentEditor tEditor : searchArgumentEditors) {
            tEditor.setParent(searchStringGroup);
        }
    }

    /**
     * Handles "modify" and "selection" events to enable/disable widgets and
     * error checking.
     */
    public void handleEvent(Event anEvent) {

        Widget widget = anEvent.widget;
        int type = anEvent.type;

        if (widget != null) {
            if (widget.getData() == AbstractSearchArgumentEditor.BUTTON_ADD && (type == SWT.Selection)) {
                addSearchArgumentEditorAndLayout((Button)widget);
            } else if (widget.getData() == AbstractSearchArgumentEditor.BUTTON_REMOVE && (type == SWT.Selection)) {
                removeSearchArgumentEditor((Button)widget);
            }
        }

        if (listener != null) {
            listener.handleEvent(anEvent);
        }
    }

    public List<SearchArgument> getSearchArguments(int aStartColumn, int anEndColumn) {
        List<SearchArgument> tSearchArguments = new ArrayList<SearchArgument>();
        for (AbstractSearchArgumentEditor tSearchArgumentEditor : searchArgumentEditors) {
            tSearchArguments.add(new SearchArgument(tSearchArgumentEditor.getSearchString(), aStartColumn, anEndColumn, tSearchArgumentEditor
                .isCaseSensitive(), tSearchArgumentEditor.isRegularExpression(), tSearchArgumentEditor.getCompareCondition()));
        }
        return tSearchArguments;
    }

    public MatchOption getMatchOption() {

        for (int i = 0; i < rdoMatchOptions.length; i++) {
            if (rdoMatchOptions[i].getSelection()) {
                SearchOptionConfig config = (SearchOptionConfig)rdoMatchOptions[i].getData(CONFIG_DATA);
                return config.getOption();
            }
        }

        return null;
    }

    public void storeScreenValues(IDialogSettings aDialogSettings) {

        for (int i = 0; i < rdoMatchOptions.length; i++) {
            if (rdoMatchOptions[i].getSelection()) {
                SearchOptionConfig config = (SearchOptionConfig)rdoMatchOptions[i].getData(CONFIG_DATA);
                aDialogSettings.put(MATCH_ALL, config.getOption().getId());
            }
        }

        aDialogSettings.put(NUM_CONDITIONS, searchArgumentEditors.size());
        for (int i = 0; i < searchArgumentEditors.size(); i++) {
            aDialogSettings.put(COMPARE_CONDITION + "_" + i, searchArgumentEditors.get(i).getCompareCondition());
            aDialogSettings.put(SEARCH_STRING + "_" + i, searchArgumentEditors.get(i).getSearchString());

            aDialogSettings.put(CASE_SENSITIVE + "_" + i, searchArgumentEditors.get(i).isCaseSensitive());
            aDialogSettings.put(REGULAR_EXPRESSION + "_" + i, searchArgumentEditors.get(i).isRegularExpression());
        }
    }

    public void loadScreenValues(IDialogSettings aDialogSettings) {

        Button defaultMatchOption = null;
        String id = loadValue(aDialogSettings, MATCH_ALL, MatchOption.ALL.getId());
        for (int i = 0; i < rdoMatchOptions.length; i++) {
            SearchOptionConfig config = (SearchOptionConfig)rdoMatchOptions[i].getData(CONFIG_DATA);
            if (id.equals(config.getOption().getId())) {
                rdoMatchOptions[i].setSelection(true);
                defaultMatchOption = rdoMatchOptions[i];
            } else {
                rdoMatchOptions[i].setSelection(false);
            }
        }

        if (defaultMatchOption == null) {
            rdoMatchOptions[0].setSelection(true); // Default setting
            defaultMatchOption = rdoMatchOptions[0];
        }

        int numConditions = loadIntValue(aDialogSettings, NUM_CONDITIONS, 1);
        for (int i = 0; i < numConditions; i++) {
            try {
                addSearchArgumentEditorAndLayout();
                searchArgumentEditors.get(i).setCompareCondition(
                    IntHelper.tryParseInt(loadValue(aDialogSettings, COMPARE_CONDITION + "_" + i, ""), SearchOptions.CONTAINS));
                searchArgumentEditors.get(i).setSearchString(loadValue(aDialogSettings, SEARCH_STRING + "_" + i, "Enter search string here"));
                searchArgumentEditors.get(i).setCase(loadBooleanValue(aDialogSettings, CASE_SENSITIVE + "_" + i, false));
                searchArgumentEditors.get(i).setRegularExpression(loadBooleanValue(aDialogSettings, REGULAR_EXPRESSION + "_" + i, false));
            } catch (Throwable e) {
                // ignore all errors
            }
        }

        if (defaultMatchOption != null) {
            applySearchOptionConfig(defaultMatchOption);
        }

        searchArgumentEditors.get(0).setFocus();
    }

    protected String loadValue(IDialogSettings aDialogSettings, String aKey, String aDefault) {
        String tValue = aDialogSettings.get(aKey);
        if (StringHelper.isNullOrEmpty(tValue)) {
            tValue = aDefault;
        }
        return tValue;
    }

    protected boolean loadBooleanValue(IDialogSettings aDialogSettings, String aKey, boolean aDefault) {
        String tValue = aDialogSettings.get(aKey);
        return BooleanHelper.tryParseBoolean(tValue, aDefault);
    }

    protected int loadIntValue(IDialogSettings aDialogSettings, String aKey, int aDefault) {
        return IntHelper.tryParseInt(aDialogSettings.get(aKey), aDefault);
    }

    protected abstract AbstractSearchArgumentEditor createEditor(Composite aParent);

    private Button getCurrentMatchOption() {

        for (int i = 0; i < rdoMatchOptions.length; i++) {
            if (rdoMatchOptions[i].getSelection()) {
                return rdoMatchOptions[i];
            }
        }

        return null;
    }

    private void applySearchOptionConfig(Widget widget) {

        if (widget == null) {
            return;
        }

        Object data = widget.getData(CONFIG_DATA);
        if (!(data instanceof SearchOptionConfig)) {
            return;
        }

        SearchOptionConfig searchOptionConfig = (SearchOptionConfig)data;
        for (AbstractSearchArgumentEditor searchArgumentEditor : searchArgumentEditors) {
            applySearchOptionConfig(searchArgumentEditor, searchOptionConfig);
        }

        Event event = new Event();
        event.type = SWT.Selection;
        event.widget = widget;
        event.data = searchOptionConfig;
        handleEvent(event);
    }

    private void applySearchOptionConfig(AbstractSearchArgumentEditor searchArgumentEditor, SearchOptionConfig searchOptionConfig) {

        searchArgumentEditor.setCaseEnabled(searchOptionConfig.isCaseEnabled());
        searchArgumentEditor.setRegularExpressionEnabled(searchOptionConfig.isRegularExpressionEnabled());
        searchArgumentEditor.setConditionEnabled(searchOptionConfig.isConditionEnabled());
    }

    private class SearchOptionSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
            applySearchOptionConfig(e.widget);
        }

        public void widgetSelected(SelectionEvent e) {
            applySearchOptionConfig(e.widget);
        }
    }
}
