package biz.isphere.rse.internal;

import biz.isphere.internal.ISourceFileSearchMemberFilterCreator;
import biz.isphere.sourcefilesearch.SearchResult;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;


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
		
		if (RSEHelper.createMemberFilter((IBMiConnection)connection, filterName, filterStrings) == null) {
			return false;
		}
		else {
			return true;
		}
		
	}

}
