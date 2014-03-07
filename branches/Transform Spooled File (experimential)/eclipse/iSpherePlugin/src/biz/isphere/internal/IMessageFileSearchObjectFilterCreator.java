package biz.isphere.internal;

import biz.isphere.messagefilesearch.SearchResult;

public interface IMessageFileSearchObjectFilterCreator {

	public boolean createObjectFilter(Object connection, String filterName, SearchResult[] searchResults);
	
}
