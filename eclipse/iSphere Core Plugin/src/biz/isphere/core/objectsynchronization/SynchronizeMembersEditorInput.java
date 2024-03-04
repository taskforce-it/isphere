/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.externalapi.ISynchronizeMembersEditorConfiguration;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objectsynchronization.rse.SynchronizeMembersEditorConfiguration;

public class SynchronizeMembersEditorInput implements IEditorInput {

    private RemoteObject leftRemoteObject;
    private RemoteObject rightRemoteObject;
    private Image titleImage;

    private ISynchronizeMembersEditorConfiguration configuration;
    private MemberDescription[] leftMemberDescriptions;
    private MemberDescription[] rightMemberDescriptions;

    public SynchronizeMembersEditorInput(RemoteObject leftRemoteObject, RemoteObject rightRemoteObject) {
        this(leftRemoteObject, rightRemoteObject, new SynchronizeMembersEditorConfiguration());
    }

    public SynchronizeMembersEditorInput(RemoteObject leftRemoteObject, RemoteObject rightRemoteObject,
        ISynchronizeMembersEditorConfiguration configuration) {

        this.configuration = configuration;
        this.leftRemoteObject = leftRemoteObject;
        this.rightRemoteObject = rightRemoteObject;
        this.titleImage = ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_SYNCHRONIZE_MEMBERS);
    }

    public boolean exists() {
        return false;
    }

    public boolean areSameObjects() {

        if (leftRemoteObject == null && rightRemoteObject == null) {
            return true;
        }

        if (leftRemoteObject != null && rightRemoteObject == null) {
            return false;
        }

        if (!leftRemoteObject.getSystem().getSystemName().equals(rightRemoteObject.getSystem().getSystemName())) {
            return false;
        }

        return leftRemoteObject.getObjectPathName().getPath().equals(rightRemoteObject.getObjectPathName().getPath());
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public ISynchronizeMembersEditorConfiguration getConfiguration() {
        return configuration;
    }

    public boolean isFileSynchronization() {
        if (ISeries.FILE.equals(leftRemoteObject.getObjectType())) {
            return true;
        }
        return false;
    }

    public boolean isLibrarySynchronization() {
        return !isFileSynchronization();
    }

    public RemoteObject getLeftObject() {
        return leftRemoteObject;
    }

    public void setLeftObject(RemoteObject object) {
        this.leftRemoteObject = object;
    }

    public RemoteObject getRightObject() {
        return rightRemoteObject;
    }

    public void setRightObject(RemoteObject object) {
        this.rightRemoteObject = object;
    }

    public String getLeftObjectName() {
        if (leftRemoteObject == null) {
            return ""; //$NON-NLS-1$
        }
        return leftRemoteObject.getAbsoluteName();
    }

    public String getRightObjectName() {
        if (rightRemoteObject == null) {
            return ""; //$NON-NLS-1$
        }
        return rightRemoteObject.getAbsoluteName();
    }

    public MemberDescription[] getLeftMemberDescriptions() {
        return this.leftMemberDescriptions;
    }

    public void setLeftMemberDescriptions(MemberDescription[] leftMemberDescriptions) {
        this.leftMemberDescriptions = leftMemberDescriptions;
    }

    public MemberDescription[] getRightMemberDescriptions() {
        return this.rightMemberDescriptions;
    }

    public void setRightMemberDescriptions(MemberDescription[] rightMemberDescriptions) {
        this.rightMemberDescriptions = rightMemberDescriptions;
    }

    public String getName() {

        if (StringHelper.isNullOrEmpty(getLeftObjectName())) {
            return getRightObjectName();
        } else if (StringHelper.isNullOrEmpty(getRightObjectName())) {
            return getLeftObjectName();
        } else {
            return getLeftObjectName() + " - " + getRightObjectName(); //$NON-NLS-1$
        }
    }

    public String getToolTipText() {
        return getName();
    }

    public Image getTitleImage() {
        return titleImage;
    }

    public SynchronizeMembersEditorInput clearAll() {

        leftMemberDescriptions = new MemberDescription[0];
        rightMemberDescriptions = new MemberDescription[0];

        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((leftRemoteObject == null) ? 0 : leftRemoteObject.hashCode());
        result = prime * result + ((leftRemoteObject.getConnectionName() == null) ? 0 : leftRemoteObject.getConnectionName().hashCode());
        result = prime * result + ((leftRemoteObject.getLibrary() == null) ? 0 : leftRemoteObject.getLibrary().hashCode());
        result = prime * result + ((leftRemoteObject.getName() == null) ? 0 : leftRemoteObject.getName().hashCode());
        result = prime * result + ((rightRemoteObject == null) ? 0 : rightRemoteObject.hashCode());
        result = prime * result + ((rightRemoteObject.getConnectionName() == null) ? 0 : rightRemoteObject.getConnectionName().hashCode());
        result = prime * result + ((rightRemoteObject.getLibrary() == null) ? 0 : rightRemoteObject.getLibrary().hashCode());
        result = prime * result + ((rightRemoteObject.getName() == null) ? 0 : rightRemoteObject.getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        SynchronizeMembersEditorInput other = (SynchronizeMembersEditorInput)obj;

        if (leftRemoteObject == null) {
            if (other.leftRemoteObject != null) return false;
        } else if (other.leftRemoteObject != null) {
            if (leftRemoteObject.getConnectionName() == null) {
                if (other.leftRemoteObject.getConnectionName() != null) return false;
            } else if (!leftRemoteObject.getConnectionName().equals(other.leftRemoteObject.getConnectionName())) return false;

            if (leftRemoteObject.getLibrary() == null) {
                if (other.leftRemoteObject.getLibrary() != null) return false;
            } else if (!leftRemoteObject.getLibrary().equals(other.leftRemoteObject.getLibrary())) return false;

            if (leftRemoteObject.getName() == null) {
                if (other.leftRemoteObject.getName() != null) return false;
            } else if (!leftRemoteObject.getName().equals(other.leftRemoteObject.getName())) return false;
        }

        if (rightRemoteObject == null) {
            if (other.rightRemoteObject != null) return false;
        } else if (other.rightRemoteObject != null) {
            if (rightRemoteObject.getConnectionName() == null) {
                if (other.rightRemoteObject.getConnectionName() != null) return false;
            } else if (!rightRemoteObject.getConnectionName().equals(other.rightRemoteObject.getConnectionName())) return false;

            if (rightRemoteObject.getLibrary() == null) {
                if (other.rightRemoteObject.getLibrary() != null) return false;
            } else if (!rightRemoteObject.getLibrary().equals(other.rightRemoteObject.getLibrary())) return false;

            if (rightRemoteObject.getName() == null) {
                if (other.rightRemoteObject.getName() != null) return false;
            } else if (!rightRemoteObject.getName().equals(other.rightRemoteObject.getName())) return false;
        }

        return true;
    }
}
