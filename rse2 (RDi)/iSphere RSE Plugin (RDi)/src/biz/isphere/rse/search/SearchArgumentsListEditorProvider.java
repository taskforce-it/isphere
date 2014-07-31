package biz.isphere.rse.search;

import biz.isphere.core.search.ISearchArgumentsListEditorProvider;
import biz.isphere.core.search.SearchOptions;

public class SearchArgumentsListEditorProvider implements ISearchArgumentsListEditorProvider {

    public biz.isphere.core.search.SearchArgumentsListEditor getListEditor() {
        return new SearchArgumentsListEditor(SearchOptions.ARGUMENTS_SIZE);
    }

}
