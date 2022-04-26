package biz.isphere.rse.internal;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.core.api.ISeriesConnection;

import biz.isphere.core.internal.ISourceFileSearchMemberFilterCreator;
import biz.isphere.core.sourcefilesearch.SearchResult;

public class SourceFileSearchMemberFilterCreator implements ISourceFileSearchMemberFilterCreator {

	public boolean createMemberFilter(Object connection, String filterName, SearchResult[] searchResults) {
		
		ISeriesMemberFilterString[] filterStrings = new ISeriesMemberFilterString[searchResults.length];

		for (int idx = 0; idx < searchResults.length; idx++) {
			
			ISeriesMemberFilterString filterString = new ISeriesMemberFilterString();
			filterString.setLibrary(searchResults[idx].getLibrary());
			filterString.setFile(searchResults[idx].getFile());
			filterString.setMember(searchResults[idx].getMember());
			filterString.setMemberType("*");

			filterStrings[idx] = filterString;
			
		}
		
		if (RSEHelper.createMemberFilter((ISeriesConnection)connection, filterName, filterStrings) == null) {
			return false;
		}
		else {
			return true;
		}
		
	}

}
