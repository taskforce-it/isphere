/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.spooledfiles;

import java.io.File;
import java.io.FileOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;
import biz.isphere.internal.BrowserEditorInput;
import biz.isphere.internal.ISphereHelper;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileInputStream;
import com.ibm.as400.access.PrintObject;


public class SpooledFile {

	private AS400 as400;
	private String file;
	private int fileNumber;
	private String jobName;
	private String jobUser;
	private String jobNumber;
	private String jobSystem;
	private String creationDate;
	private String creationTime;
	private String status;
	private String outputQueue;
	private String outputQueueLibrary;
	private String outputPriority;
	private String userData;
	private String formType;
	private int copies;
	private int pages;
	private int currentPage;
	private Object data;
	private com.ibm.as400.access.SpooledFile toolboxSpooledFile;

	public SpooledFile() {
		as400 = null;
		file = "";
		fileNumber = 0;
		jobName = "";
		jobUser = "";
		jobNumber = "";
		jobSystem = "";
		creationDate = "";
		creationTime = "";
		status = "";
		outputQueue = "";
		outputQueueLibrary = "";
		outputPriority = "";
		userData = "";
		formType = "";
		copies = 0;
		pages = 0;
		currentPage = 0;
		data = null;
		toolboxSpooledFile = null;
	}

	public AS400 getAS400() {
		return as400;
	}

	public void setAS400(AS400 as400) {
		this.as400 = as400;
	}
	
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public int getFileNumber() {
		return fileNumber;
	}
	
	public void setFileNumber(int fileNumber) {
		this.fileNumber = fileNumber;
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public String getJobUser() {
		return jobUser;
	}
	
	public void setJobUser(String jobUser) {
		this.jobUser = jobUser;
	}
	
	public String getJobNumber() {
		return jobNumber;
	}
	
	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}
	
	public String getJobSystem() {
		return jobSystem;
	}

	public void setJobSystem(String jobSystem) {
		this.jobSystem = jobSystem;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getCreationTime() {
		return creationTime;
	}
	
	public void setCreationTime(String creationTime) {
        this.creationTime = creationTime; 
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getOutputQueue() {
		return outputQueue;
	}
	
	public void setOutputQueue(String outputQueue) {
		this.outputQueue = outputQueue;
	}
	
	public String getOutputQueueLibrary() {
		return outputQueueLibrary;
	}
	
	public void setOutputQueueLibrary(String outputQueueLibrary) {
		this.outputQueueLibrary = outputQueueLibrary;
	}
	
	public String getOutputPriority() {
		return outputPriority;
	}
	
	public void setOutputPriority(String outputPriority) {
		this.outputPriority = outputPriority;
	}
	
	public String getUserData() {
		return userData;
	}
	
	public void setUserData(String userData) {
		this.userData = userData;
	}
	
	public String getFormType() {
		return formType;
	}
	
	public void setFormType(String formType) {
		this.formType = formType;
	}

	public int getCopies() {
		return copies;
	}
	
	public void setCopies(int copies) {
		this.copies = copies;
	}
	
	public int getPages() {
		return pages;
	}
	
	public void setPages(int pages) {
		this.pages = pages;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	private com.ibm.as400.access.SpooledFile getToolboxSpooledFile() {
		return new com.ibm.as400.access.SpooledFile(
				as400,
				file,
				fileNumber,
				jobName,
				jobUser,
				jobNumber,
				jobSystem,
				creationDate,
				creationTime);
	}
	
	public String hold() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			toolboxSpooledFile.hold(null);
			refreshSpooledFile();
			return null;
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public String release() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			toolboxSpooledFile.release();
			refreshSpooledFile();
			return null;
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public String delete() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			toolboxSpooledFile.delete();
			return null;
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public AS400Message getMessage() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			return toolboxSpooledFile.getMessage();
		} 
		catch (Exception e) {
			return null;
		}
	}

	public String answerMessage(String reply) {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			toolboxSpooledFile.answerMessage(reply);
			refreshSpooledFile();
			return null;
		} 
		catch (Exception e) {
			return e.getMessage();
		}
	}
	
	public String replyMessage() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		AS400Message message = null;
		try {
			message = toolboxSpooledFile.getMessage();
		} 
		catch (Exception e) {
		}
		if (message == null) {
			MessageDialog.openError(
					Display.getCurrent().getActiveShell(),
					Messages.getString("Error"), 
					Messages.getString("No_Messages").replaceAll("&1", file).replaceAll("&2", Integer.toString(fileNumber))); 
		} 
		else {
			SpooledFileMessageDialog dialog = new SpooledFileMessageDialog(Display.getCurrent().getActiveShell(), this);
			dialog.open();
		}
		return null;
	}

	private void refreshSpooledFile() {
		if (toolboxSpooledFile == null) {
			toolboxSpooledFile = getToolboxSpooledFile();
		}
		try {
			status = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_SPLFSTATUS);
		} catch (Exception e) {}
		try {
			String outqdev = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPUT_QUEUE);
			if (outqdev.endsWith(".OUTQ")) {
				outqdev = outqdev.substring(10);
				int slash = outqdev.indexOf("/");
				String library = outqdev.substring(0, slash - 4);
				String outq = outqdev.substring(slash + 1);
				outputQueue = outq.substring(0, outq.indexOf(".OUTQ"));
				outputQueueLibrary = library;
			}
			else {
				outputQueue = outqdev;
				outputQueueLibrary = "*LIBL";
			}
		} catch (Exception e) {}
		try {
			outputPriority = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_OUTPTY);
		} catch (Exception e) {}		
		try {
			userData = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_USERDATA);
		} catch (Exception e) {}
		try {
			formType = toolboxSpooledFile.getStringAttribute(PrintObject.ATTR_FORMTYPE);
		} catch (Exception e) {}
		try {
			copies = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_COPIES);
		} catch (Exception e) {}
		try {
			pages = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_PAGES);
		} catch (Exception e) {}
		try {
			currentPage = toolboxSpooledFile.getIntegerAttribute(PrintObject.ATTR_CURPAGE);
		} catch (Exception e) {}
	}

	public String getCommandChangeAttribute() {
		return "CHGSPLFA FILE(" + file + ") JOB(" + jobNumber + "/" + jobUser + "/" + jobName + ") SPLNBR(" + fileNumber + ")";
	}

	public String changeAttribute(String command) {
		String message = executeCommand(command);
		if (message == null) {
			refreshSpooledFile();
		}
		return message;
	}
	
	public String executeCommand(String command) {
		try {
			CommandCall commandCall = new CommandCall(as400, command);
			if (!commandCall.run()) {
				AS400Message message = commandCall.getMessageList(0);
				if (message != null) {
					return message.getText();
				}
			}	
		} 
		catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}
	
	public String getOutputQueueFormated() {
		return outputQueueLibrary + "/" + outputQueue;
	}
	
	public String getCreationDateFormated() {
		String date = creationDate;
		if (date.length() == 7)
			date = date.substring(5, 7) + "." + date.substring(3, 5) + "." + date.substring(1, 3);
		return date;
	}
	
	public String getCreationTimeFormated() {
		String time = creationTime;
		if (time.length() == 6)
			time = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
		return time;
	}

	public String open(String format) {
		
		String source = "/tmp/" + getTemporaryName(format);
		String target = ISpherePlugin.getDefault().getSpooledFilesDirectory() + File.separator + getTemporaryName(format);
		
		boolean cleanUp = false;
		
		try {
			
			if (createStreamFile(format)) {
				
				cleanUp = true;
				
				uploadStreamFile(source, target);

				if (!ISpherePlugin.getDefault().getSpooledFilesProject().isOpen()) ISpherePlugin.getDefault().getSpooledFilesProject().open(null);
				IFile file = ISpherePlugin.getWorkspace().getRoot().getFileForLocation(new Path(target));
				file.refreshLocal(1, null);
				
				if (format.equals("*TEXT")) {
					
					IWorkbenchPage page = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					org.eclipse.ui.ide.IDE.openEditor(page, file);

				}
				else if (format.equals("*HTML")) {

					BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), "iSphereSpooledFiles/" + getTemporaryName(format), null, target);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "biz.isphere.internal.BrowserEditor");
					
				}
				else if (format.equals("*PDF")) {

					BrowserEditorInput editorInput = new BrowserEditorInput(getTemporaryName(format), getTemporaryName(format), "iSphereSpooledFiles/" + getTemporaryName(format), null, target);
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "biz.isphere.internal.BrowserEditor");
					
				}
				
			}
			else {
				return "Could not create stream file for spooled file on host.";
			}
			
		} 
		catch (Exception e) {
			return e.getMessage();
		}
		finally {
			
			if (cleanUp) {
				try {
					deleteStreamFile(source);
				} 
				catch (Exception e) {
					return e.getMessage();
				}
			}
			
		}
		
		return null;
		
	}
	
	private boolean createStreamFile(String format) throws Exception {
		
		IPreferenceStore store = ISpherePlugin.getDefault().getPreferenceStore();
		
		boolean _default = true;
		String conversionCommand = "";
		String conversionCommandLibrary = "";
		if (format.equals("*TEXT")) {
			if (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT").equals("*USRDEF")) {
				_default = false;
				conversionCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.COMMAND");
				conversionCommandLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_TEXT.LIBRARY");
			}
		}
		else if (format.equals("*HTML")) {
			if (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML").equals("*USRDEF")) {
				_default = false;
				conversionCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.COMMAND");
				conversionCommandLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_HTML.LIBRARY");
			}
		}
		else if (format.equals("*PDF")) {
			if (store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF").equals("*USRDEF")) {
				_default = false;
				conversionCommand = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.COMMAND");
				conversionCommandLibrary = store.getString("DE.TASKFORCE.ISPHERE.SPOOLED_FILES.CONVERSION_PDF.LIBRARY");
			}
		}
		
		String command = null;
		String library = null;
		
		if (_default) {
			command = "CVTSPLF FROMFILE(&SPLF) SPLNBR(&SPLFNBR) JOB(&JOBNBR/&JOBUSR/&JOBNAME) TOSTREAM('&STMF') TODIR('&STMFDIR') STCODPAG(&CODPAG) TOFMT(&FMT) STOPT(*REPLACE)";
			library = ISpherePlugin.getISphereLibrary();

		}
		else {
			command = conversionCommand;
			library = conversionCommandLibrary;
		}

		command = command.replaceAll("&SPLFNBR", Integer.toString(fileNumber));
		command = command.replaceAll("&SPLF", file);
		command = command.replaceAll("&JOBNBR", jobNumber);
		command = command.replaceAll("&JOBUSR", jobUser);
		command = command.replaceAll("&JOBNAME", jobName);
		command = command.replaceAll("&STMFDIR", "/tmp");
		command = command.replaceAll("&STMF", getTemporaryName(format));
		command = command.replaceAll("&CODPAG", "1252");
		command = command.replaceAll("&FMT", format);
		
		String currentLibrary = null;
		
		boolean cleanUp = false;
		
		try {

			currentLibrary = ISphereHelper.getCurrentLibrary(as400);
			
			if (currentLibrary != null) {

				if (ISphereHelper.setCurrentLibrary(as400, library)) {

					cleanUp = true;
					
					String messageId = ISphereHelper.executeCommand(as400, command);
					
					if (messageId != null && messageId.equals("")) {
						return true;
					}
					
				}
				
			}
			
		}
		finally {
			
			if (cleanUp) {
				
				ISphereHelper.setCurrentLibrary(as400, currentLibrary);
				
			}
			
		}
		
		return false;
				
	}
	
	private String uploadStreamFile(String source, String target) throws Exception {
		
		IFSFileInputStream in = null;
		FileOutputStream out = null;

		try {
			
			in = new IFSFileInputStream(as400, source);
			out = new FileOutputStream(new File(target));

			byte[] buffer = new byte[8 * 1024];
			int count = 0;
			do {
				count = in.read(buffer, 0, buffer.length);
				if (count > 0) {
				//	byte[] converted = new String(buffer, 0, count, "Cp1252").getBytes();
				//	out.write(converted, 0, converted.length);
					out.write(buffer, 0, count);
				}
			} while (count != -1);
			
		} 
		finally {
			
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			
		}
		
		return target;
		
	}

	private void deleteStreamFile(String streamFile) throws Exception {
		IFSFile file = new IFSFile(as400, streamFile);
		if (file.exists()) {
			file.delete();
		}
	}
	
	public String getAbsoluteName() {
		return file + "_" + fileNumber + "_" + jobName + "_" + jobUser + "_" + jobNumber + "_" + jobSystem + "_" + creationDate + "_" + creationTime;
	}

	public String getTemporaryName(String format) {

		String fileExtension = "";
		if (format.equals("*TEXT")) {
			fileExtension = ".txt";
		}
		else if (format.equals("*HTML")) {
			fileExtension = ".html";
		}
		else if (format.equals("*PDF")) {
			fileExtension = ".pdf";
		}
		
		return "iSphere_Spooled_File_" + getAbsoluteName() + fileExtension;
		
	}

	public String save(Shell shell, String format) {

		String fileDescription = "";
		String fileExtension = "";
		if (format.equals("*TEXT")) {
			fileDescription = "Text Files";
			fileExtension = ".txt";
		}
		else if (format.equals("*HTML")) {
			fileDescription = "HTML Files";
			fileExtension = ".html";
		}
		else if (format.equals("*PDF")) {
			fileDescription = "PDF Files";
			fileExtension = ".pdf";
		}
		
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] {fileDescription, "All Files"});
		dialog.setFilterExtensions(new String[] {fileExtension, "*.*"});
		dialog.setFilterPath("C:\\");
		dialog.setFileName("spooled_file" + fileExtension);
		String file = dialog.open();

		if (file != null) {
			
			String source = "/tmp/" + getTemporaryName(format);
			String target = file;
			
			boolean cleanUp = false;
			
			try {
				
				if (createStreamFile(format)) {
					
					cleanUp = true;
					
					uploadStreamFile(source, target);
					
				}
				else {
					return "Could not create stream file for spooled file on host.";
				}
				
			} 
			catch (Exception e) {
				return e.getMessage();
			}
			finally {
				
				if (cleanUp) {
					try {
						deleteStreamFile(source);
					} 
					catch (Exception e) {
						return e.getMessage();
					}
				}
				
			}
			
		}
		
		return null;
		
	}
	
}
