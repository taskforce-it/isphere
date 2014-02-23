package biz.isphere.internal;

import biz.isphere.sourcefilesearch.SearchResult;

public interface ISourceFileSearchMemberFilterCreator {

	public boolean createMemberFilter(Object connection, String filterName, SearchResult[] searchResults);
	
}
