package biz.isphere.rse.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.sourcefilesearch.SearchOptions;
import biz.isphere.rse.Messages;

public class SearchArgumentsListEditor implements Listener {

    private static final String MATCH_ALL = "matchAll";
    private static final String NUM_CONDITIONS = "numberOfCompareConditions";
    private static final String COMPARE_CONDITION = "compareCondition";
    private static final String SEARCH_STRING = "searchString";
    private static final String CASE_SENSITIVE = "caseSensitive";

    private Composite searchStringGroup;
    private ScrolledComposite scrollable;

    private Button rdoMatchAll;
    private Button rdoMatchAny;
    private List<SearchArgumentEditor> searchArgumentEditors;
    private int maxNumSearchArguments;
    private Listener listener;

    public SearchArgumentsListEditor(int aMaxNumSearchArguments) {
        maxNumSearchArguments = aMaxNumSearchArguments;
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

        rdoMatchAll = new Button(tMatchGroup, SWT.RADIO);
        rdoMatchAll.setText(Messages.MatchAllConditions);

        rdoMatchAny = new Button(tMatchGroup, SWT.RADIO);
        rdoMatchAny.setText(Messages.MatchAnyCondition);

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
        searchStringGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrollable.setContent(searchStringGroup);

        searchArgumentEditors = new ArrayList<SearchArgumentEditor>();
    }

    private void addSearchArgumentEditorAndLayout() {
        addSearchArgumentEditor(null);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);
    }

    private void addSearchArgumentEditorAndLayout(Button aButton) {
        SearchArgumentEditor tEditor = addSearchArgumentEditor(aButton);
        scrollable.setMinSize(searchStringGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        searchStringGroup.layout(true);

        scrollable.setOrigin(tEditor.getBounds().x, tEditor.getBounds().y - tEditor.getBounds().height - 5);
        tEditor.setFocus();

        setAddButtonEnablement();
    }

    private SearchArgumentEditor addSearchArgumentEditor(Button aButton) {
        SearchArgumentEditor tEditor = new SearchArgumentEditor();
        tEditor.createContents(searchStringGroup);
        tEditor.addSearchStringListener(this);
        tEditor.addButtonListener(this);

        if (aButton == null) {
            searchArgumentEditors.add(tEditor);
        } else {
            searchArgumentEditors.add(findSearchArgumentEditor(aButton) + 1, tEditor);
        }

        rearrangeSearchArgumentEditors();

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
        for (SearchArgumentEditor tEditor : searchArgumentEditors) {
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
        for (SearchArgumentEditor tEditor : searchArgumentEditors) {
            tEditor.setParent(scrollable);
        }
        for (SearchArgumentEditor tEditor : searchArgumentEditors) {
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

        if (widget.getData() == SearchArgumentEditor.BUTTON_ADD && (type == SWT.Selection)) {
            addSearchArgumentEditorAndLayout((Button)widget);
        } else if (widget.getData() == SearchArgumentEditor.BUTTON_REMOVE && (type == SWT.Selection)) {
            removeSearchArgumentEditor((Button)widget);
        }

        if (listener != null) {
            listener.handleEvent(anEvent);
        }
    }

    public List<SearchArgument> getSearchArguments(int aStartColumn, int anEndColumn) {
        List<SearchArgument> tSearchArguments = new ArrayList<SearchArgument>();
        for (SearchArgumentEditor tSearchArgumentEditor : searchArgumentEditors) {
            tSearchArguments.add(new SearchArgument(tSearchArgumentEditor.getSearchString(), aStartColumn, anEndColumn, tSearchArgumentEditor
                .isCaseSensitive(), tSearchArgumentEditor.getCompareCondition()));
        }
        return tSearchArguments;
    }

    public boolean getIsMatchAll() {
        return rdoMatchAll.getSelection();
    }

    public void storeScreenValues(IDialogSettings aDialogSettings) {
        aDialogSettings.put(MATCH_ALL, rdoMatchAll.getSelection());

        aDialogSettings.put(NUM_CONDITIONS, searchArgumentEditors.size());
        for (int i = 0; i < searchArgumentEditors.size(); i++) {
            aDialogSettings.put(COMPARE_CONDITION + "_" + i, searchArgumentEditors.get(i).getCompareCondition());
            aDialogSettings.put(SEARCH_STRING + "_" + i, searchArgumentEditors.get(i).getSearchString());

            aDialogSettings.put(CASE_SENSITIVE + "_" + i, searchArgumentEditors.get(i).isCaseSensitive());
        }
    }

    public void loadScreenValues(IDialogSettings aDialogSettings) {
        rdoMatchAll.setSelection(loadBooleanValue(aDialogSettings, MATCH_ALL, true));
        rdoMatchAny.setSelection(!rdoMatchAll.getSelection());

        int numConditions = loadIntValue(aDialogSettings, NUM_CONDITIONS, 1);
        for (int i = 0; i < numConditions; i++) {
            try {
                addSearchArgumentEditorAndLayout();
                searchArgumentEditors.get(i).setCompareCondition(
                    IntHelper.tryParseInt(loadValue(aDialogSettings, COMPARE_CONDITION + "_" + i, ""), SearchOptions.CONTAINS));
                searchArgumentEditors.get(i).setSearchString(loadValue(aDialogSettings, SEARCH_STRING + "_" + i, "Enter search string here"));
                searchArgumentEditors.get(i).setCase(loadBooleanValue(aDialogSettings, CASE_SENSITIVE + "_" + i, false));
            } catch (Throwable e) {
                // ignore all errors
            }
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
}
