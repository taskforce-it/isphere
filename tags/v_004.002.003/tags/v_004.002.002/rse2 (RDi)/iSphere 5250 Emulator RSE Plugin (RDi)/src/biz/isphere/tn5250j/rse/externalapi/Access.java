/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.externalapi;

import java.io.File;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.tn5250j.core.session.Session;
import biz.isphere.tn5250j.core.tn5250jeditor.TN5250JEditorInput;
import biz.isphere.tn5250j.core.tn5250jpart.DisplaySession;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.rse.TN5250JRSEPlugin;
import biz.isphere.tn5250j.rse.designerpart.DesignerInfo;
import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;
import biz.isphere.tn5250j.rse.sessionspart.handler.SetSEPAsync;

public class Access {

	public static String TN5250J_DESIGNER_EDITOR_SEU = "*SEU";
	public static String TN5250J_DESIGNER_EDITOR_SDA = "*SDA";
	public static String TN5250J_DESIGNER_EDITOR_RLU = "*RLU";

	public static boolean doesTN5250JDesignerSessionExist(String _rseProfil, String _rseConnection) {
		return new File(TN5250JRSEPlugin.getRSESessionDirectory(_rseProfil, _rseConnection) + File.separator + "_DESIGNER").exists();
	}
	
    public static void openTN5250JDesigner(String _rseProfil, String _rseConnection, String _library, String _file, String _member, String _editor, boolean _readOnly, String _currentLibrary, String _libraryList)
        throws Exception {

        String mode;
        if (_readOnly) {
            mode = "*BROWSE";
        } else {
            mode = "*EDIT";
        }
    	
		String sessionDirectory = TN5250JRSEPlugin.getRSESessionDirectory(_rseProfil, _rseConnection);
		String connection = _rseProfil + "-" + _rseConnection;
		String name = "_DESIGNER";
		
		Session session = Session.load(sessionDirectory, connection, name);
		if (session != null) {
		
			String area = session.getArea();

    		ITN5250JPart tn5250jPart = null;
			
			try {

    			if (area.equals("*VIEW")) {

    				tn5250jPart = (ITN5250JPart)(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("biz.isphere.tn5250j.rse.designerview.DesignerView"));
    				
    			}
    			else if (area.equals("*EDITOR")) {
 
    				TN5250JEditorInput editorInput = 
    					new TN5250JEditorInput(
    							"biz.isphere.tn5250j.rse.designereditor.DesignerEditor", 
    							"TN5250J Designer", 
    							"TN5250J", 
    							TN5250JRSEPlugin.getDefault().getImageRegistry().get(TN5250JRSEPlugin.IMAGE_TN5250J));
    				
    				tn5250jPart = (ITN5250JPart)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "biz.isphere.tn5250j.rse.designereditor.DesignerEditor");
    				
    			}
			
			} 
			catch (PartInitException e) {
			}
			
			if (tn5250jPart != null) {
			
        		DesignerInfo designerInfo = new DesignerInfo(tn5250jPart);
        		designerInfo.setRSEProfil(_rseProfil);
        		designerInfo.setRSEConnection(_rseConnection);
        		designerInfo.setSession("_DESIGNER");
        		designerInfo.setLibrary(_library);
        		designerInfo.setSourceFile(_file);
        		designerInfo.setMember(_member);
        		designerInfo.setEditor(_editor);
        		designerInfo.setMode(mode);
        		designerInfo.setCurrentLibrary(_currentLibrary);
        		designerInfo.setLibraryList(_libraryList);

    			DisplaySession.run(designerInfo);
				
			}
			
		}

    }

    public static void setServiceEntryPoint(Shell shell, String _rseProfil, String _rseConnection, String _library, String _object, String _objectType)
            throws Exception {

		SessionsInfo sessionInfo = new SessionsInfo(null);
		sessionInfo.setRSEProfil(_rseProfil);
		sessionInfo.setRSEConnection(_rseConnection);
			
		Runnable runnable = new SetSEPAsync(
				shell, 
				sessionInfo, 
				_library, 
				_object, 
				_objectType);
        shell.getDisplay().asyncExec(runnable);

    }
    
}
