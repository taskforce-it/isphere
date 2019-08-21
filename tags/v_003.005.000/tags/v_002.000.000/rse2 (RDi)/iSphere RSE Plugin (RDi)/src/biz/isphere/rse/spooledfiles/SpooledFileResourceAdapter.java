/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.ui.SystemMenuManager;
import org.eclipse.rse.ui.view.AbstractSystemViewAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.rse.ui.view.ISystemRemoteElementAdapter;

import biz.isphere.core.spooledfiles.SpooledFileBaseResourceAdapter;


public class SpooledFileResourceAdapter extends AbstractSystemViewAdapter implements ISystemRemoteElementAdapter {

	private SpooledFileBaseResourceAdapter base = new SpooledFileBaseResourceAdapter();

	
	public void addActions(SystemMenuManager menu, IStructuredSelection selection, Shell parent, String menuGroup) {
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getImageDescriptor(((SpooledFileResource)object).getSpooledFile());
		}
		return null;
	}
	
	public boolean handleDoubleClick(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.handleDoubleClick(((SpooledFileResource)object).getSpooledFile());
		}
		return false;
	}

	public String getText(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getText(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public String getAbsoluteName(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getAbsoluteName(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public String getType(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getType(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public Object getParent(Object object) {
		return null;
	}

	public boolean hasChildren(IAdaptable adaptable) {
		return false;
	}
	
	public boolean showRename(Object object) {
		return false;
	}
	
	public boolean showDelete(Object object) {
		return false;
	}

	public boolean showRefresh(Object object) {
		return false;
	}

	public Object[] getChildren(IAdaptable adaptable, IProgressMonitor monitor) {
		return new Object[0];
	}

	protected IPropertyDescriptor[] internalGetPropertyDescriptors() {
		return base.internalGetPropertyDescriptors();
	}

	public Object internalGetPropertyValue(Object propKey) {
		return base.internalGetPropertyValue(((SpooledFileResource)propertySourceInput).getSpooledFile(), propKey);
	}

	public String getAbsoluteParentName(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getAbsoluteParentName(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public String getSubSystemFactoryId(Object object) {
		return base.getSubSystemFactoryId();
	}

	public String getRemoteTypeCategory(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getRemoteTypeCategory(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public String getRemoteType(Object object) {
		if (object instanceof SpooledFileResource) {
			return base.getRemoteType(((SpooledFileResource)object).getSpooledFile());
		}
		return "";
	}

	public String getRemoteSubType(Object object) {
		return null;
	}

	public boolean refreshRemoteObject(Object oldElement, Object newElement) {
		SpooledFileResource oldSpooledFile = (SpooledFileResource)oldElement;
		SpooledFileResource newSpooledFile = (SpooledFileResource)newElement;
		newSpooledFile.setSpooledFile(oldSpooledFile.getSpooledFile());
		return false;
	}
	
	public Object getRemoteParent(Object object, IProgressMonitor monitor) throws Exception {
		return null;
	}

	public String[] getRemoteParentNamesInUse(Object object, IProgressMonitor monitor) throws Exception {
		return null;	
	}

	public boolean supportsUserDefinedActions(Object object) {
		return false;
	}

	public String getSubSystemConfigurationId(Object object) {
		return "biz.isphere.core.spooledfiles.subsystems.factory";
	}
	
}
