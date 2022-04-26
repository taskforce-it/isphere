// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core.misc;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400FTP;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.FTP;

import de.taskforce.tn5250j.core.Messages;
import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class TransferTN5250JLibrary extends Shell {

	private AS400 as400;
	private CommandCall commandCall;
	private Table tableStatus;
	private Button buttonStart;
	
	public TransferTN5250JLibrary(Display display, int style) {
		super(display, style);
		createContents();
		setLayout(new GridLayout());
	}

	protected void createContents() {
		setText(Messages.getString("Transfer_TN5250J_library"));
		setSize(500, 250);

		buttonStart = new Button(this, SWT.NONE);
		buttonStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				buttonStart.setEnabled(false);
				setStatus(Messages.getString("Checking_library_TN5250J_for_existence"));
				if (!executeCommand("CHKOBJ OBJ(QSYS/TN5250J) OBJTYPE(*LIB)").equals("CPF9801")) {
					setStatus("!!!   " + Messages.getString("Library_TN5250J_does_already_exist") + "   !!!");
				}
				else {
					setStatus(Messages.getString("Checking_file_TN5250J_in_library_QGPL_for_existence"));
					if (!executeCommand("CHKOBJ OBJ(QGPL/TN5250J) OBJTYPE(*FILE)").equals("CPF9801")) {
						setStatus("!!!   " + Messages.getString("File_TN5250J_in_library_QGPL_does_already_exist") + "   !!!");
					}
					else {
						setStatus(Messages.getString("Creating_save_file_TN5250J_in_library_QGPL"));
						if (!executeCommand("CRTSAVF FILE(QGPL/TN5250J) TEXT(TN5250J)").equals("")) {
							setStatus("!!!   " + Messages.getString("Could_not_create_save_file_TN5250J_in_library_QGPL") + "   !!!");
						}
						else {
						    URL fileUrl;
							try {
								fileUrl = FileLocator.toFileURL(TN5250JCorePlugin.getInstallURL());
							} 
							catch (IOException e) {
								setStatus("!!!   Internal Error at checkpoint 1   !!!");
								fileUrl = null;
							}
							if (fileUrl != null) {
								File file = new File(fileUrl.getPath() + "Server" + File.separator + "TN5250J");
								setStatus(Messages.getString("Sending_save_file_to_host"));
								boolean ok = false;
								AS400FTP client = new AS400FTP(as400);
								try {
									client.setDataTransferType(FTP.BINARY);
									if (client.connect()) {
										client.put(file, "/QSYS.LIB/QGPL.LIB/TN5250J.FILE");
										client.disconnect();
										ok = true;
									}
								} 
								catch (IOException e) {
								}
								if (!ok) {
									setStatus("!!!   " + Messages.getString("Could_not_send_save_file_to_host") + "   !!!");
								}
								else {
									setStatus(Messages.getString("Restoring_library_TN5250J"));
									if (!executeCommand("RSTLIB SAVLIB(TN5250J) DEV(*SAVF) SAVF(QGPL/TN5250J)").equals("")) {
										setStatus("!!!   " + Messages.getString("Could_not_restore_library_TN5250J") + "   !!!");
									}
									else {
										setStatus("!!!   " + Messages.getString("Library_TN5250J_successfull_transfered") + "   !!!");
									}
								}
							}
						}
						executeCommand("DLTF FILE(QGPL/TN5250J)");
					}
				}
			}
		});
		buttonStart.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		buttonStart.setText(Messages.getString("Start_Transfer"));

		tableStatus = new Table(this, SWT.BORDER);
		final GridData gd_tableStatus = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableStatus.setLayoutData(gd_tableStatus);

		final TableColumn columnStatus = new TableColumn(tableStatus, SWT.NONE);
		columnStatus.setWidth(500);
	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void setStatus(String status) {
		TableItem itemStatus = new TableItem(tableStatus, SWT.BORDER);
		itemStatus.setText(status);
		tableStatus.update();
	}
	
	private String executeCommand(String command) {
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
			return "CPF0000";
		}
	}
	
	public boolean connect(String host) {
		buttonStart.setEnabled(false);
		SignOnDialog signOnDialog = new SignOnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), host);
		if (signOnDialog.open() == Dialog.OK) {
			as400 = signOnDialog.getAS400();
			if (as400 != null) {
				try {
					as400.connectService(AS400.COMMAND);
					commandCall = new CommandCall(as400);
					if (commandCall != null) {
						buttonStart.setEnabled(true);
						return true;
					}
				} 
				catch (AS400SecurityException e) {
				} 
				catch (IOException e) {
				}
			}
		}
		return false;
	}

}
