/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.sessionspart.handler;

import java.lang.reflect.Method;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.rse.sessionspart.SessionsInfo;

import com.ibm.etools.iseries.services.qsys.api.IQSYSObject;

public class SetSEPAsync extends AbstractAsyncHandler {

    private String library;
    private String object;
    private String type;

    public SetSEPAsync(Shell shell, SessionsInfo sessionsInfo, String library, String object, String objType) {

        super(shell, sessionsInfo);

        this.library = library;
        this.object = object;
        this.type = objType;
    }

    public void runInternally() {

    	Class _class = null;
    	String _classFound = null;
    	try { 
    		_class = Class.forName("com.ibm.etools.systems.as400.debug.sep.ServiceEntryPointActionDelegate");
        	if (_class != null) {
        		_classFound = "com.ibm.etools.systems.as400.debug.sep.ServiceEntryPointActionDelegate";
        	}
   		} 
    	catch (Exception e) {
    	};
    	if (_class == null) {
        	try { 
        		_class = Class.forName("com.ibm.etools.iseries.debug.internal.ui.sep.ServiceEntryPointActionDelegate");
            	if (_class != null) {
            		_classFound = "com.ibm.etools.iseries.debug.internal.ui.sep.ServiceEntryPointActionDelegate";
            	}
       		} 
        	catch (Exception e) {
        	};
    	}
    	if (_class == null) {
    		return;
    	}
    	
        try {

            IQSYSObject[] objects = getConnection().listObjects(library, object, new String[] { type }, null);
            if (objects != null && objects.length > 0) {
                IAction action = new SetSEPAction();
                IStructuredSelection selection = new StructuredSelection(objects);
    
                // import com.ibm.etools.systems.as400.debug.sep.ServiceEntryPointActionDelegate;
                // ServiceEntryPointActionDelegate delegate = new ServiceEntryPointActionDelegate();
                // delegate.selectionChanged(action, selection);
                // delegate.run(action);
                
        		ClassLoader myClassLoader = SetSEPAsync.class.getClassLoader();
        		Class myClass = myClassLoader.loadClass(_classFound);
        		Object myInstance = myClass.newInstance();
                
        		Method myMethod;
        		
    			myMethod = myInstance.getClass().getMethod("selectionChanged",
    					new Class[] { 
    						IAction.class,
    						ISelection.class
    					});
    			myMethod.invoke(myInstance,
    					new Object[] { 
    						action, 
    						selection
    					});
                
    			myMethod = myInstance.getClass().getMethod("run",
    					new Class[] { 
    						IAction.class
    					});
    			myMethod.invoke(myInstance,
    					new Object[] { 
    						action
    					});
                
            }
        } catch (Throwable e) {
        	e.printStackTrace();
        }
    }

    private class SetSEPAction extends Action {
        @Override
        public String getText() {
        	// import com.ibm.etools.systems.as400.debug.launchconfig.AS400DebugResources;
            // return AS400DebugResources.RESID_SET_SEP_NOPROMPT_MENUITEM;
        	return "Set Service Entry Point";
        }
    };
}
