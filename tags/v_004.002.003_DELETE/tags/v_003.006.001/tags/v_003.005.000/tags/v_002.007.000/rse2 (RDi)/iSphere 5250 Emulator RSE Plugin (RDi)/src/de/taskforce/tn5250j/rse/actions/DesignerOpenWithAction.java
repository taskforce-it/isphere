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

package de.taskforce.tn5250j.rse.actions;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.ErrorCompletingRequestException;
import com.ibm.as400.access.Job;
import com.ibm.as400.access.ObjectDoesNotExistException;
import com.ibm.etools.iseries.rse.ui.actions.QSYSSystemBaseAction;
import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSJob;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.*;

import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.tn5250jeditor.TN5250JEditorInput;
import de.taskforce.tn5250j.core.tn5250jpart.DisplaySession;
import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.designerpart.DesignerInfo;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.ui.SystemMenuManager;
import org.eclipse.rse.ui.view.ISystemRemoteElementAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DesignerOpenWithAction extends QSYSSystemBaseAction {

	private IStructuredSelection selection;

	public DesignerOpenWithAction() {
		super(Messages.getString("TN5250J_Designer"), "", null);
		setContextMenuGroup("group.openwith");
		allowOnMultipleSelection(true);
		setHelp("");
		setImageDescriptor(TN5250JRSEPlugin.getImageDescriptor(TN5250JRSEPlugin.IMAGE_EDIT_DESIGNER));
	}

	public void populateMenu(Shell shell, SystemMenuManager menu, IStructuredSelection selection, String menuGroup) {
		setShell(shell);
		menu.add("group.openwith", this);
	}

	public void run() {
		if (selection != null) {
			for (Iterator<?> iterMembers = selection.iterator(); iterMembers.hasNext();) {
				Object objSelection = iterMembers.next();
				ISystemRemoteElementAdapter adapter = (ISystemRemoteElementAdapter)((IAdaptable)objSelection).getAdapter(ISystemRemoteElementAdapter.class);
				if (adapter != null) {
					Object editable = adapter.getEditableRemoteObject(objSelection);
					if (editable != null && (editable instanceof QSYSEditableRemoteSourceFileMember)) {
						QSYSEditableRemoteSourceFileMember editableMember = (QSYSEditableRemoteSourceFileMember)editable;
						startDesigner(editableMember.getISeriesConnection(), editableMember.getMember(), getMode());
					}
				}
			}
		}
	}

	public boolean updateSelection(IStructuredSelection selection) {
		this.selection = null;
		for (Iterator<?> iterSelection = selection.iterator(); iterSelection.hasNext();) {
			Object objSelection = iterSelection.next();
			if (!(objSelection instanceof IRemoteObjectContextProvider)) {
				return false;
			}
		}
		for (Iterator<?> iterMembers = selection.iterator(); iterMembers.hasNext();) {
			Object objSelection = iterMembers.next();
			ISystemRemoteElementAdapter adapter = (ISystemRemoteElementAdapter)((IAdaptable)objSelection).getAdapter(ISystemRemoteElementAdapter.class);
			if (adapter != null) {
				Object editable = adapter.getEditableRemoteObject(objSelection);
				if (editable != null && (editable instanceof QSYSEditableRemoteSourceFileMember)) {
					QSYSEditableRemoteSourceFileMember editableMember = (QSYSEditableRemoteSourceFileMember)editable;
					if (!(new File(TN5250JRSEPlugin.getRSESessionDirectory(editableMember.getISeriesConnection().getProfileName() + "-" + editableMember.getISeriesConnection().getConnectionName()) + File.separator + "_DESIGNER").exists())) {
						return false;
					}
				}
			}
		}
		this.selection = selection;
		return true;
	}

	protected String getMode() {
		return "*EDIT";
	}
	
	protected void startDesigner(IBMiConnection ibmiConnection, IQSYSMember member, String mode) {
    	try {
    		AS400 as400 = ibmiConnection.getAS400ToolboxObject();
    		IQSYSJob iseriesJob = ibmiConnection.getServerJob(null);
    		Job job = new Job(as400, iseriesJob.getJobName(), iseriesJob.getUserName(), iseriesJob.getJobNumber());
        	String stringCurrentLibrary = "*CRTDFT";
        	String stringLibraryList = "";
    		if (job.getCurrentLibraryExistence()) {
    			stringCurrentLibrary = job.getCurrentLibrary();
    		}
    		String[] user = job.getUserLibraryList();
    		for (int y=0; y < user.length; y++) {
				stringLibraryList = stringLibraryList + " " + user[y];
    		}
			
    		String sessionDirectory = TN5250JRSEPlugin.getRSESessionDirectory(ibmiConnection.getProfileName() + "-" + ibmiConnection.getConnectionName());
    		String connection = ibmiConnection.getProfileName() + "-" + ibmiConnection.getConnectionName();
    		String name = "_DESIGNER";
    		
    		Session session = Session.load(sessionDirectory, connection, name);
    		if (session != null) {
    		
    			String area = session.getArea();
 
        		ITN5250JPart tn5250jPart = null;
        		
    			if (area.equals("*VIEW")) {

    				tn5250jPart = (ITN5250JPart)(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("de.taskforce.tn5250j.rse.designerview.DesignerView"));
    				
    			}
    			else if (area.equals("*EDITOR")) {
 
    				TN5250JEditorInput editorInput = 
    					new TN5250JEditorInput(
    							"de.taskforce.tn5250j.rse.designereditor.DesignerEditor", 
    							Messages.getString("TN5250J_Designer"), 
    							"TN5250J", 
    							TN5250JRSEPlugin.getDefault().getImageRegistry().get(TN5250JRSEPlugin.IMAGE_TN5250J));
    				
    				tn5250jPart = (ITN5250JPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "de.taskforce.tn5250j.rse.designereditor.DesignerEditor");
    				
    			}
        		
    			if (tn5250jPart != null) {

    				DesignerInfo designerInfo = new DesignerInfo(tn5250jPart);
            		designerInfo.setRSEProfil(ibmiConnection.getProfileName());
            		designerInfo.setRSEConnection(ibmiConnection.getConnectionName());
            		designerInfo.setSession("_DESIGNER");
            		designerInfo.setLibrary(member.getLibrary());
            		designerInfo.setSourceFile(member.getFile());
            		designerInfo.setMember(member.getName());
            		String editor = "*SEU";
            		if (member.getType().equals("DSPF")) {
            			editor = "*SDA";
            		}
            		else if (member.getType().equals("PRTF")) {
            			editor = "*RLU";
            		}
            		designerInfo.setEditor(editor);
            		designerInfo.setMode(mode);
            		designerInfo.setCurrentLibrary(stringCurrentLibrary);
            		designerInfo.setLibraryList(stringLibraryList);

        			DisplaySession.run(sessionDirectory, connection, name, designerInfo);
    				
    			}
    			
    		}
			
    	} 
    	catch (SystemMessageException e2) {
		} 
    	catch (AS400SecurityException e) {
		} 
    	catch (ErrorCompletingRequestException e) {
		} 
    	catch (InterruptedException e) {
		} 
    	catch (IOException e) {
		} 
    	catch (ObjectDoesNotExistException e) {
		} 
    	catch (PartInitException e) {
		}
	}
}
