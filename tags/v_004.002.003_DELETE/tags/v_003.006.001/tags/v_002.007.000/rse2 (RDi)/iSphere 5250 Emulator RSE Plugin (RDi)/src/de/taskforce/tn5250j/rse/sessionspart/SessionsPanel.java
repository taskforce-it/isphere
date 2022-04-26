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

package de.taskforce.tn5250j.rse.sessionspart;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.swt.widgets.Shell;
import org.tn5250j.Session5250;
import org.tn5250j.framework.tn5250.ScreenField;
import org.tn5250j.framework.tn5250.ScreenFields;

import biz.isphere.core.compareeditor.CompareAction;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.rse.compareeditor.RSECompareDialog;
import biz.isphere.rse.internal.RSEMember;
import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.sessionspart.CoreSessionsPanel;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JGUI;
import de.taskforce.tn5250j.core.tn5250jpart.TN5250JInfo;
import de.taskforce.tn5250j.rse.Messages;

public class SessionsPanel extends CoreSessionsPanel {
	
	private static final long serialVersionUID = 1L;
	
	private class OpenLpexAsync extends Thread {
		
		private String library;
		private String sourceFile;
		private String member;
		private String mode;
		private String currentLibrary;
		private String libraryList;
		
		public OpenLpexAsync(String library, String sourceFile, String member, String mode, String currentLibrary, String libraryList) {
			this.library = library;
			this.sourceFile = sourceFile;
			this.member = member;
			this.mode = mode;
			this.currentLibrary = currentLibrary;
			this.libraryList = libraryList;
		}		
		public void run() {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					
					SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();
					
					IBMiConnection iSeriesConnection = IBMiConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo.getRSEConnection());

					String command = "CHGLIBL LIBL(" + libraryList + ") CURLIB(" + currentLibrary + ")";
					
					try {
						iSeriesConnection.runCommand(command);
					} 
					catch (SystemMessageException event) {
					}
					
					try {
						IQSYSMember iseriesMember = iSeriesConnection.getMember(library, sourceFile, member, null);
						if (iseriesMember != null) {
							
							String editor = "com.ibm.etools.systems.editor";
							
							String _editor = null;
							if (iseriesMember.getType().equals("DSPF") ||
									iseriesMember.getType().equals("MNUDDS")) {
								_editor = "Screen Designer";
							}
							else if (iseriesMember.getType().equals("PRTF")) {
								_editor = "Report Designer";
							}

							if (_editor != null) {

								MessageDialog dialog = new MessageDialog(
										getShell(),
										Messages.getString("Choose_Editor"),
										null,
										Messages.getString("Please_choose_the_editor_for_the_source_member."),
										MessageDialog.INFORMATION,
										new String[] {
											_editor,
											"LPEX Editor"
										},
										0);

								final int dialogResult = dialog.open();

								if (dialogResult == 0) {

									if (iseriesMember.getType().equals("DSPF") ||
											iseriesMember.getType().equals("MNUDDS")) {
										editor = "com.ibm.etools.iseries.dds.tui.editor.ScreenDesigner";
									}
									else if (iseriesMember.getType().equals("PRTF")) {
										editor = "com.ibm.etools.iseries.dds.tui.editor.ReportDesigner";
									}
									
								}

							}
							
							QSYSEditableRemoteSourceFileMember editable = new QSYSEditableRemoteSourceFileMember(iseriesMember);
							if (mode.equals("*OPEN")) {
								editable.open(getShell(), false, editor);
							}
							else {
								editable.open(getShell(), true, editor);
							}
							
						}
					} 
					catch (SystemMessageException e) {
					} 
					catch (InterruptedException e) {
					}
				}
			});
		}
	
	}
	private class OpenCompareAsync {
		
		private String library;
		private String sourceFile;
		private String member;
		
		public OpenCompareAsync(String library, String sourceFile, String member) {
			this.library = library;
			this.sourceFile = sourceFile;
			this.member = member;
		}
		
		public void start() {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					
					SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();
					
					IBMiConnection iSeriesConnection = IBMiConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo.getRSEConnection());

					IQSYSMember _member;
					try {
						_member = iSeriesConnection.getMember(library, sourceFile, member, null);
						if (_member != null) {
							
							try {
								
								RSEMember rseLeftMember = new RSEMember(_member);
								
								RSECompareDialog dialog = new RSECompareDialog(getShell(), true, rseLeftMember);
								
								if (dialog.open() == Dialog.OK) {
									
									boolean editable = dialog.isEditable();
									boolean considerDate = dialog.isConsiderDate();
									boolean threeWay = dialog.isThreeWay();
									
									RSEMember rseAncestorMember = null;

									if (threeWay) {
										
										IQSYSMember ancestorMember = dialog.getAncestorConnection().getMember(
												dialog.getAncestorLibrary(), 
												dialog.getAncestorFile(), 
												dialog.getAncestorMember(),
												null);
										
										if (ancestorMember != null) {
											rseAncestorMember = new RSEMember(ancestorMember);
										}
										
									}

									RSEMember rseRightMember = null;
									
									IQSYSMember rightMember = dialog.getRightConnection().getMember(
											dialog.getRightLibrary(), 
											dialog.getRightFile(), 
											dialog.getRightMember(),
											null);
									
									if (rightMember != null) {
										rseRightMember = new RSEMember(rightMember);
									}

									CompareAction action = new CompareAction(editable, considerDate, threeWay, rseAncestorMember, rseLeftMember, rseRightMember, null);
									action.run();
									
								}

							} catch (Exception e) {
							}
							
						}
					} 
					catch (SystemMessageException e) {
					} 
					catch (InterruptedException e) {
					}
					
				}
			});
		}
	
	}
	
	public SessionsPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
		super(tn5250jInfo, session, shell);
	}

	public void onScreenChanged(int arg0, int arg1, int arg2, int arg3, int arg4) {
		if (arg0 == 1) {
			if (String.copyValueOf(getSession5250().getScreen().getScreenAsChars(), 2, 14).equals("TN5250J-EDITOR")) {
				String library = "";
				String sourceFile = "";
				String member = "";
				String mode = "";
				String currentLibrary = "";
				StringBuffer libraryList = new StringBuffer("");
				ScreenFields screenFields = getSessionGUI().getScreen().getScreenFields();
				ScreenField[] screenField = screenFields.getFields();
				for (int idx = 0; idx < screenField.length; idx++) {
					if (idx == 0) {
						library = screenField[idx].getString().trim();
					}
					else if (idx == 1) {
						sourceFile = screenField[idx].getString().trim();
					}
					else if (idx == 2) {
						member = screenField[idx].getString().trim();
					}
					else if (idx == 3) {
						mode = screenField[idx].getString().trim();
					}
					else if (idx == 4) {
						currentLibrary = screenField[idx].getString().trim();
					}
					else if (idx >= 5 && idx <= 25) {
						libraryList.append(screenField[idx].getString().trim() + " ");
					}
				}
				if (!library.equals("") && !sourceFile.equals("") && !member.equals("") && !mode.equals("")) {
					new OpenLpexAsync(library, sourceFile, member, mode, currentLibrary, libraryList.toString()).start();
				}
				getSessionGUI().getScreen().sendKeys("[pf3]");
			}
			else if (String.copyValueOf(getSession5250().getScreen().getScreenAsChars(), 32, 15).equals("TN5250J-COMPARE")) {
				String library = "";
				String sourceFile = "";
				String member = "";
				ScreenFields screenFields = getSessionGUI().getScreen().getScreenFields();
				ScreenField[] screenField = screenFields.getFields();
				for (int idx = 0; idx < screenField.length; idx++) {
					if (idx == 0) {
						library = screenField[idx].getString().trim();
					}
					else if (idx == 1) {
						sourceFile = screenField[idx].getString().trim();
					}
					else if (idx == 2) {
						member = screenField[idx].getString().trim();
					}
				}
				if (!library.equals("") && !sourceFile.equals("") && !member.equals("")) {
					new OpenCompareAsync(library, sourceFile, member).start();
				}
				getSessionGUI().getScreen().sendKeys("[pf3]");
			}
		}
	}
	
	public TN5250JGUI getTN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
		return new SessionsGUI(tn5250jInfo, session5250);
	}

	public String getHost() {
		SessionsInfo sessionsInfo = (SessionsInfo)getTN5250JInfo();
		IBMiConnection iSeriesConnection = IBMiConnection.getConnection(sessionsInfo.getRSEProfil(), sessionsInfo.getRSEConnection());
		if (iSeriesConnection != null) {
			return iSeriesConnection.getHostName();
		}
		return "";
	}

}
