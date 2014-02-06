package de.taskforce.isphere.internal;

import de.taskforce.isphere.messagefilesearch.SearchResult;

public interface IMessageFileSearchObjectFilterCreator {

	public boolean createObjectFilter(Object connection, String filterName, SearchResult[] searchResults);
	
}
