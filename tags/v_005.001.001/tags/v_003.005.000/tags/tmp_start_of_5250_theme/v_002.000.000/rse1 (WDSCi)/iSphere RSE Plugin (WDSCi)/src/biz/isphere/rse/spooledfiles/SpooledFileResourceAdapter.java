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

package biz.isphere.rse.spooledfiles;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import biz.isphere.core.spooledfiles.SpooledFileBaseResourceAdapter;

import com.ibm.etools.systems.core.ui.SystemMenuManager;
import com.ibm.etools.systems.core.ui.view.AbstractSystemViewAdapter;
import com.ibm.etools.systems.core.ui.view.ISystemRemoteElementAdapter;

public class SpooledFileResourceAdapter extends AbstractSystemViewAdapter implements ISystemRemoteElementAdapter {

	private SpooledFileBaseResourceAdapter base = new SpooledFileBaseResourceAdapter();
	
	public SpooledFileResourceAdapter() {
		super();
	}

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

	public boolean hasChildren(Object object) {
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

	public Object[] getChildren(Object object) {
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

	public Object getRemoteParent(Shell shell, Object object) throws Exception {
		return null;
	}

	public String[] getRemoteParentNamesInUse(Shell shell, Object object)
		throws Exception {
		return null;	

	}
	public boolean supportsUserDefinedActions(Object object) {
		return false;
	}
	
}
