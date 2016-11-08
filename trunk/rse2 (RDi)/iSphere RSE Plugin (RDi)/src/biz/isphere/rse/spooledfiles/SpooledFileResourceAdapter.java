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
import org.eclipse.rse.ui.view.ISystemRemoteElementAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import biz.isphere.core.spooledfiles.ISpooledFileSubSystem;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileBaseResourceAdapter;
import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;

public class SpooledFileResourceAdapter extends AbstractSystemViewAdapter implements ISystemRemoteElementAdapter {

    private SpooledFileBaseResourceAdapter base = new SpooledFileBaseResourceAdapter();

    @Override
    public void addActions(SystemMenuManager menu, IStructuredSelection selection, Shell parent, String menuGroup) {
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getImageDescriptor(getSpooledFile(object));
        }
        return null;
    }

    @Override
    public boolean handleDoubleClick(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.handleDoubleClick(getSpooledFile(object));
        }
        return false;
    }

    public String getText(Object object) {
        if (object instanceof SpooledFileResource) {
            SpooledFile spooledFile = getSpooledFile(object);
            SpooledFileTextDecoration decorationStyle = ((ISpooledFileSubSystem)getSubSystem(object)).getDecorationTextStyle();
            return base.getText(spooledFile, decorationStyle);
        }
        return "";
    }

    public String getAbsoluteName(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getAbsoluteName(getSpooledFile(object));
        }
        return "";
    }

    @Override
    public String getType(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getType(getSpooledFile(object));
        }
        return "";
    }

    @Override
    public Object getParent(Object object) {
        return null;
    }

    @Override
    public boolean hasChildren(IAdaptable adaptable) {
        return false;
    }

    @Override
    public boolean showRename(Object object) {
        return false;
    }

    @Override
    public boolean showDelete(Object object) {
        return false;
    }

    @Override
    public boolean showRefresh(Object object) {
        return false;
    }

    @Override
    public Object[] getChildren(IAdaptable adaptable, IProgressMonitor monitor) {
        return new Object[0];
    }

    @Override
    protected IPropertyDescriptor[] internalGetPropertyDescriptors() {
        return base.internalGetPropertyDescriptors();
    }

    @Override
    public Object internalGetPropertyValue(Object propKey) {
        return base.internalGetPropertyValue(((SpooledFileResource)propertySourceInput).getSpooledFile(), propKey);
    }

    public String getAbsoluteParentName(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getAbsoluteParentName(getSpooledFile(object));
        }
        return "";
    }

    public String getSubSystemFactoryId(Object object) {
        return base.getSubSystemFactoryId();
    }

    public String getRemoteTypeCategory(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getRemoteTypeCategory(getSpooledFile(object));
        }
        return "";
    }

    public String getRemoteType(Object object) {
        if (object instanceof SpooledFileResource) {
            return base.getRemoteType(getSpooledFile(object));
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
    
    @Override
    public boolean testAttribute(Object target, String name, String value) {
        if (name != null && "biz.isphere.rse.spooledfiles.SpooledFileResource.file".equals(name)) {
            return value.equalsIgnoreCase(base.getFile(getSpooledFile(target)));
        }
        return super.testAttribute(target, name, value);
    }

    private SpooledFile getSpooledFile(Object object) {
        return ((SpooledFileResource)object).getSpooledFile();
    }
}
