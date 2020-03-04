/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcefilesearch;

import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class FNDSTR_getNumberOfSearchElements {

	public int run(
			AS400 _as400,
			int handle) {

		int numberOfSearchElements = 0;
		
		try {
			
			ProgramCallDocument pcml = 
				new ProgramCallDocument(
						_as400, 
						"biz.isphere.core.sourcefilesearch.FNDSTR_getNumberOfSearchElements", 
						this.getClass().getClassLoader());

			pcml.setIntValue("FNDSTR_getNumberOfSearchElements.handle", handle);
			
			boolean rc = pcml.callProgram("FNDSTR_getNumberOfSearchElements");

			if (rc == false) {
				
				AS400Message[] msgs = pcml.getMessageList("FNDSTR_getNumberOfSearchElements");
				for (int idx = 0; idx < msgs.length; idx++) {
					System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
				}
				System.out.println("*** Call to FNDSTR_getNumberOfSearchElements failed. See messages above ***");
				
				numberOfSearchElements = -1;
				
			}
			else {
				
				numberOfSearchElements = pcml.getIntValue("FNDSTR_getNumberOfSearchElements.numberOfSearchElements");
				
			}
			
		}
		catch (PcmlException e) {
		
			numberOfSearchElements = -1;
			
		//	System.out.println(e.getLocalizedMessage());    
		//	e.printStackTrace();
		//	System.out.println("*** Call to FNDSTR_getNumberOfSearchElements failed. ***");
		//	return null;
			
		}
		
		return numberOfSearchElements;
		
	} 

}