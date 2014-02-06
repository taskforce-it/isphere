package de.taskforce.isphere.internal;

import de.taskforce.isphere.sourcefilesearch.SearchResult;

public interface ISourceFileSearchMemberFilterCreator {

	public boolean createMemberFilter(Object connection, String filterName, SearchResult[] searchResults);
	
}
