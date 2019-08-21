package biz.isphere.rse.search;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.search.AbstractSearchArgumentEditor;

public class SearchArgumentsListEditor extends biz.isphere.core.search.SearchArgumentsListEditor {

    public SearchArgumentsListEditor(int aMaxNumSearchArguments) {
        super(aMaxNumSearchArguments);
    }

    @Override
    protected AbstractSearchArgumentEditor createEditor(Composite aParent) {
        SearchArgumentEditor tEditor = new SearchArgumentEditor();
        tEditor.createContents(aParent);
        tEditor.addSearchStringListener(this);
        tEditor.addButtonListener(this);
        return tEditor;
    }

}
