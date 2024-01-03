package biz.isphere.core.sourcefilesearch;

import biz.isphere.core.search.SearchOptions;

public class ExtendedSearchResult {

    private SearchOptions searchOptions;
    private SearchResult[] searchResults;
    
    public ExtendedSearchResult() {
        searchOptions = null;
        searchResults = null;
    }

    public SearchOptions getSearchOptions() {
        return searchOptions;
    }
    
    public void setSearchOptions(SearchOptions searchOptions) {
        this.searchOptions = searchOptions;
    }
    
    public SearchResult[] getSearchResults() {
        return searchResults;
    }
    
    public void setSearchResults(SearchResult[] searchResults) {
        this.searchResults = searchResults;
    }
    
}
