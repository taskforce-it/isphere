/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.command;

import org.eclipse.rse.internal.useractions.ui.compile.SystemCompileCommand;
import org.eclipse.rse.internal.useractions.ui.compile.SystemCompileManager;
import org.eclipse.rse.internal.useractions.ui.compile.SystemCompileProfile;
import org.eclipse.rse.internal.useractions.ui.compile.SystemCompileType;

import com.ibm.etools.iseries.rse.ui.compile.QSYSCompileManagerHandler;

@SuppressWarnings("restriction")
public class RSECommandHelper {
	
	// Experimental
	public static void getAllCompileTypes() {
		SystemCompileManager compMgr = QSYSCompileManagerHandler.getInstance().getQSYSCompileManager();
		SystemCompileProfile[] profiles = compMgr.getAllCompileProfiles();
		for (int i = 0; i < profiles.length; i++) {
			System.out.println(profiles[i].getProfileName());
			String[] profileTypes = profiles[i].getCompileTypesArray();
			for (int j = 0; j < profileTypes.length; j++) {
				System.out.println("   " + profileTypes[j]);
				SystemCompileType scType = profiles[i].getCompileType(profileTypes[j]);
				SystemCompileCommand[] commands = scType.getCompileCommandsArray();
				for (int z = 0; z < commands.length; z++) {
					System.out.println("      " + commands[z].getLabel() + " / " + commands[z].getCurrentString());
				}
			}
		}
	}

}
