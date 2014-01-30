/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package de.taskforce.isphere.spooledfiles;

import com.ibm.as400.data.ProgramCallDocument;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;

public class SPLF_setFormType {

	public int run(
			AS400 _as400,
			String _formType) {

		int errno = 0;
		
		try {
			
			ProgramCallDocument pcml = 
				new ProgramCallDocument(
						_as400, 
						"de.taskforce.isphere.spooledfiles.SPLF_setFormType", 
						this.getClass().getClassLoader());

			pcml.setValue("SPLF_setFormType.formType", _formType);
			
			boolean rc = pcml.callProgram("SPLF_setFormType");

			if (rc == false) {
				
				AS400Message[] msgs = pcml.getMessageList("SPLF_setFormType");
				for (int idx = 0; idx < msgs.length; idx++) {
					System.out.println(msgs[idx].getID() + " - " + msgs[idx].getText());
				}
				System.out.println("*** Call to SPLF_setFormType failed. See messages above ***");
				
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
		//	System.out.println("*** Call to SPLF_setFormType failed. ***");
		//	return null;
			
		}
		
		return errno;
		
	} 

}