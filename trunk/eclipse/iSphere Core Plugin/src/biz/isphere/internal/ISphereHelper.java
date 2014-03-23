/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.internal;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;


public class ISphereHelper {

	public static boolean checkISphereLibrary(Shell shell, AS400 as400) {

		String messageId = null;
		try {
			messageId = executeCommand(as400, "CHKOBJ OBJ(QSYS/" + ISpherePlugin.getISphereLibrary() + ") OBJTYPE(*LIB)");
		} 
		catch (Exception e) {
		}
		
		if (messageId == null || !messageId.equals("")) {

			String text = Messages.getString("E_R_R_O_R");
			String message = Messages.getString("iSphere_library_&1_does_not_exist_on_system_&2._Please_transfer_iSphere_library_&1_to_system_&2.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			new DisplayMessage(shell, text, message).start();
			
			return false;
			
		}
		
		String dataAreaISphereContent = null;
		CharacterDataArea dataAreaISphere = new CharacterDataArea(as400, "/QSYS.LIB/" + ISpherePlugin.getISphereLibrary() + ".LIB/ISPHERE.DTAARA");
		try {
			dataAreaISphereContent = dataAreaISphere.read();
		} 
		catch (AS400SecurityException e) {
			e.printStackTrace();
		} 
		catch (ErrorCompletingRequestException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectTypeException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ObjectDoesNotExistException e) {
			e.printStackTrace();
		}
		if (dataAreaISphereContent == null) {

			String text = Messages.getString("E_R_R_O_R");
			String message = Messages.getString("Specified_iSphere_library_&1_on_System_&2_is_not_a_iSphere_library.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			new DisplayMessage(shell, text, message).start();
			
			return false;
			
		}
		
		String serverProvided = dataAreaISphereContent.substring(7, 13);
		String clientProvided = "010402"; // 1.4.2
		String serverNeedsClient = dataAreaISphereContent.substring(21, 27);
		String clientNeedsServer = "010200";
		
		if (serverProvided.compareTo(clientNeedsServer) < 0) {

			String text = Messages.getString("E_R_R_O_R");
			String message = Messages.getString("iSphere_library_&1_on_System_&2_is_of_version_&3,_but_at_least_version_&4_is_needed._Please_transfer_the_current_iSphere_library_&1_to_system_&2.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			message = message.replace("&3", Integer.parseInt(serverProvided.substring(0, 2)) + "." + Integer.parseInt(serverProvided.substring(2, 4)) + "." + Integer.parseInt(serverProvided.substring(4, 6)));
			message = message.replace("&4", Integer.parseInt(clientNeedsServer.substring(0, 2)) + "." + Integer.parseInt(clientNeedsServer.substring(2, 4)) + "." + Integer.parseInt(clientNeedsServer.substring(4, 6)));
			new DisplayMessage(shell, text, message).start();
			
			return false;
			
		}
		
		if (clientProvided.compareTo(serverNeedsClient) < 0) {

			String text = Messages.getString("E_R_R_O_R");
			String message = Messages.getString("The_current_installed_iSphere_client_is_of_version_&1,_but_the_iSphere_server_needs_at_least_version_&2._Please_install_the_current_iSphere_client.");
			message = message.replace("&1", Integer.parseInt(clientProvided.substring(0, 2)) + "." + Integer.parseInt(clientProvided.substring(2, 4)) + "." + Integer.parseInt(clientProvided.substring(4, 6)));
			message = message.replace("&2", Integer.parseInt(serverNeedsClient.substring(0, 2)) + "." + Integer.parseInt(serverNeedsClient.substring(2, 4)) + "." + Integer.parseInt(serverNeedsClient.substring(4, 6)));
			new DisplayMessage(shell, text, message).start();
			
			return false;
			
		}
		
		return true;
		
	}
	
	public static String executeCommand(AS400 as400, String command) throws Exception {
		
		CommandCall commandCall = new CommandCall(as400);

		if (commandCall != null) {

			commandCall.run(command);
			AS400Message[] messageList = commandCall.getMessageList();
			if (messageList.length > 0) {
				for (int idx = 0; idx < messageList.length; idx++) {
					if (messageList[idx].getType() == AS400Message.ESCAPE) {
						return messageList[idx].getID();
					}
				}
			}
			return "";
			
		}
		
		return "CPF0000";
		
	}

	public static String getCurrentLibrary(AS400 _as400) throws Exception {
		
		String currentLibrary = null;
		
		Job[] jobs = _as400.getJobs(AS400.COMMAND);
		
		if (jobs.length == 1) {

			if (!jobs[0].getCurrentLibraryExistence()) {
				currentLibrary = "*CRTDFT";
			}
			else {
				currentLibrary = jobs[0].getCurrentLibrary();
			}
			
		}
		
		return currentLibrary;

	}

	public static boolean setCurrentLibrary(AS400 _as400, String currentLibrary) throws Exception {
		
		String command = "CHGCURLIB CURLIB(" + currentLibrary + ")";
		CommandCall commandCall = new CommandCall(_as400);
		
		if (commandCall.run(command)) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
}
