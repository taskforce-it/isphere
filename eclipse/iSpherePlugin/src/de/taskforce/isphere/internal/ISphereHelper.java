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

package de.taskforce.isphere.internal;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CharacterDataArea;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.IllegalObjectTypeException;
import com.ibm.as400.access.ObjectDoesNotExistException;

import de.taskforce.isphere.ISpherePlugin;
import de.taskforce.isphere.Messages;

public class ISphereHelper {

	public static boolean checkISphereLibrary(Shell shell, AS400 as400) {
		
		if (!executeCommand(as400, "CHKOBJ OBJ(QSYS/" + ISpherePlugin.getISphereLibrary() + ") OBJTYPE(*LIB)").equals("")) {

			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText(Messages.getString("E_R_R_O_R"));
			String message = Messages.getString("iSphere_library_&1_does_not_exist_on_system_&2._Please_transfer_iSphere_library_&1_to_system_&2.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			errorBox.setMessage(message);
			errorBox.open();
			
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

			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText(Messages.getString("E_R_R_O_R"));
			String message = Messages.getString("Specified_iSphere_library_&1_on_System_&2_is_not_a_iSphere_library.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			errorBox.setMessage(message);
			errorBox.open();
			
			return false;
			
		}
		
		String serverProvided = dataAreaISphereContent.substring(7, 13);
		String clientProvided = "010300"; // 1.3.0
		String serverNeedsClient = dataAreaISphereContent.substring(21, 27);
		String clientNeedsServer = "010100";
		
		if (serverProvided.compareTo(clientNeedsServer) < 0) {

			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText(Messages.getString("E_R_R_O_R"));
			String message = Messages.getString("iSphere_library_&1_on_System_&2_is_of_version_&3,_but_at_least_version_&4_is_needed._Please_transfer_the_current_iSphere_library_&1_to_system_&2.");
			message = message.replace("&1", ISpherePlugin.getISphereLibrary());
			message = message.replace("&2", as400.getSystemName());
			message = message.replace("&3", serverProvided);
			message = message.replace("&4", clientNeedsServer);
			errorBox.setMessage(message);
			errorBox.open();
			
			return false;
			
		}
		
		if (clientProvided.compareTo(serverNeedsClient) < 0) {

			MessageBox errorBox = new MessageBox(shell, SWT.ICON_ERROR);
			errorBox.setText(Messages.getString("E_R_R_O_R"));
			String message = Messages.getString("The_current_installed_iSphere_client_is_of_version_&1,_but_the_iSphere_server_needs_at_least_version_&2._Please_install_the_current_iSphere_client.");
			message = message.replace("&1", clientProvided);
			message = message.replace("&2", serverNeedsClient);
			errorBox.setMessage(message);
			errorBox.open();
			
			return false;
			
		}
		
		return true;
		
	}
	
	public static String executeCommand(AS400 as400, String command) {
		
		CommandCall commandCall = new CommandCall(as400);

		if (commandCall != null) {

			try {
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
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return "CPF0000";
		
	}
	
}
