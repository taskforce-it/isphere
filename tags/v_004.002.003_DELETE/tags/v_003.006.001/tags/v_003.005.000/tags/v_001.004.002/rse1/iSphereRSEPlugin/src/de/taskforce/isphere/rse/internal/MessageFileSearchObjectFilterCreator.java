package de.taskforce.isphere.rse.internal;

import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.core.api.ISeriesConnection;

import de.taskforce.isphere.internal.IMessageFileSearchObjectFilterCreator;
import de.taskforce.isphere.messagefilesearch.SearchResult;

public class MessageFileSearchObjectFilterCreator implements IMessageFileSearchObjectFilterCreator {

	public boolean createObjectFilter(Object connection, String filterName, SearchResult[] searchResults) {
		
		ISeriesObjectFilterString[] filterStrings = new ISeriesObjectFilterString[searchResults.length];

		for (int idx = 0; idx < searchResults.length; idx++) {
			
			ISeriesObjectFilterString filterString = new ISeriesObjectFilterString();
			filterString.setLibrary(searchResults[idx].getLibrary());
			filterString.setObject(searchResults[idx].getMessageFile());
			filterString.setObjectType("*MSGF");

			filterStrings[idx] = filterString;
			
		}
		
		if (RSEHelper.createObjectFilter((ISeriesConnection)connection, filterName, filterStrings) == null) {
			return false;
		}
		else {
			return true;
		}
		
	}

}
