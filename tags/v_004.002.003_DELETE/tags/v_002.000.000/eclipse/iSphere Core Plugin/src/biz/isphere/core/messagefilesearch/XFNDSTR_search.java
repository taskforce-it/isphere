/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class XFNDSTR_search {

	public int run(
			AS400 _as400,
			int _handle,
			String _string,
			int _fromColumn,
			int _toColumn,
			String _case) {

		int errno = 0;
		
		try {
			
			ProgramCallDocument pcml = 
				new ProgramCallDocument(
						_as400, 
						"biz.isphere.core.messagefilesearch.XFNDSTR_search", 
						this.getClass().getClassLoader());

			pcml.setIntValue("XFNDSTR_search.handle", _handle);
			pcml.setValue("XFNDSTR_search.string", _string);
			pcml.setIntValue("XFNDSTR_search.fromColumn", _fromColumn);
			pcml.setIntValue("XFNDSTR_search.toColumn", _toColumn);
			pcml.setValue("XFNDSTR_search.case", _case);
			
			boolean rc = pcml.callProgram("XFNDSTR_search");

			if (rc == false) {
				
				AS400Message[] msgs = pcml.getMessageList("XFNDSTR_search");
				for (int idx = 0; idx < msgs.length; idx++) {
					System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
				}
				System.out.println("*** Call to XFNDSTR_search failed. See messages above ***");
				
				errno = -1;
				
			}
			else {
				
				errno = 1;
				
			}
			
		}
		catch (PcmlException e) {
		
			errno = -1;
			
		//	System.out.println(e.getLocalizedMessage());    
		//	e.printStackTrace();
		//	System.out.println("*** Call to XFNDSTR_search failed. ***");
		//	return null;
			
		}
		
		return errno;
		
	} 

}