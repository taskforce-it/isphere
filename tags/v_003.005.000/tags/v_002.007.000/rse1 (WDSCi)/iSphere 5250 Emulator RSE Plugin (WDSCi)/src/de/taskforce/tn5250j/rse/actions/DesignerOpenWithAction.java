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
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.iseries.core.api.ISeriesJob;
import com.ibm.etools.iseries.core.api.ISeriesMember;
import com.ibm.etools.iseries.core.descriptors.ISeriesDataElementDescriptorType;
import com.ibm.etools.iseries.core.ui.actions.ISeriesSystemBaseAction;
import com.ibm.etools.systems.core.messages.SystemMessageException;
import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.actions.ISystemDynamicPopupMenuExtension;
import com.ibm.etools.systems.dstore.core.model.DataElement;

import de.taskforce.tn5250j.core.session.Session;
import de.taskforce.tn5250j.core.tn5250jeditor.TN5250JEditorInput;
import de.taskforce.tn5250j.core.tn5250jpart.DisplaySession;
import de.taskforce.tn5250j.core.tn5250jpart.ITN5250JPart;
import de.taskforce.tn5250j.rse.Messages;
import de.taskforce.tn5250j.rse.TN5250JRSEPlugin;
import de.taskforce.tn5250j.rse.designerpart.DesignerInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DesignerOpenWithAction extends ISeriesSystemBaseAction implements ISystemDynamicPopupMenuExtension {

	protected ArrayList arrayListSelection;

	public DesignerOpenWithAction() {
		super(Messages.getString("TN5250J_Designer"), "", null);
		arrayListSelection = new ArrayList();
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
		if (arrayListSelection.size() > 0) {
			for (Iterator iterMembers = arrayListSelection.iterator(); iterMembers.hasNext();) {
				DataElement dataElement = (DataElement)iterMembers.next();
				ISeriesMember member = new ISeriesMember(dataElement);
				if (member != null) {
					startDesigner(member.getISeriesConnection(), member, getMode());
				}
			}
		}
	}

	public boolean supportsSelection(IStructuredSelection selection) {

		this.arrayListSelection.clear();

		ArrayList<DataElement> arrayListSelection = new ArrayList<DataElement>();

		for (Iterator iterSelection = selection.iterator(); iterSelection.hasNext();) {
			Object objSelection = iterSelection.next();
			if (objSelection instanceof DataElement) {
				DataElement dataElement = (DataElement)objSelection;
				ISeriesDataElementDescriptorType type = ISeriesDataElementDescriptorType.getDescriptorTypeObject(dataElement);
				if (type.isSourceMember()) {
					String strType = type.getRemoteSourceType(dataElement);
					if (strType.equalsIgnoreCase("DSPF") || strType.equalsIgnoreCase("MNUDDS") || strType.equalsIgnoreCase("PRTF"))
						arrayListSelection.add(dataElement);
				}
			}
		}
		
		if (arrayListSelection.isEmpty()) {
			return false;
		}
		
		for (Iterator iterMembers = arrayListSelection.iterator(); iterMembers.hasNext();) {
			DataElement dataElement = (DataElement)iterMembers.next();
			ISeriesMember member = new ISeriesMember(dataElement);
			if (member != null) {
				if (!(new File(TN5250JRSEPlugin.getRSESessionDirectory(member.getISeriesConnection().getProfileName() + "-" + member.getISeriesConnection().getConnectionName()) + File.separator + "_DESIGNER").exists())) {
					return false;
				}
			}
		}
		
		this.arrayListSelection = arrayListSelection;
		return true;
		
	}

	protected String getMode() {
		return "*EDIT";
	}
	
	protected void startDesigner(ISeriesConnection iSeriesConnection, ISeriesMember member, String mode) {
    	try {
    		AS400 as400 = iSeriesConnection.getAS400ToolboxObject(getShell());
    		ISeriesJob iseriesJob = iSeriesConnection.getServerJob(getShell());
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
    		
    		String sessionDirectory = TN5250JRSEPlugin.getRSESessionDirectory(iSeriesConnection.getProfileName() + "-" + iSeriesConnection.getConnectionName());
    		String connection = iSeriesConnection.getProfileName() + "-" + iSeriesConnection.getConnectionName();
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
            		designerInfo.setRSEProfil(iSeriesConnection.getProfileName());
            		designerInfo.setRSEConnection(iSeriesConnection.getConnectionName());
            		designerInfo.setSession("_DESIGNER");
            		designerInfo.setLibrary(member.getLibraryName());
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