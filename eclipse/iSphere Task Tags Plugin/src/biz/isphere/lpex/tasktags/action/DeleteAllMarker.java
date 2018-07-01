/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.action;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.lpex.tasktags.model.LPEXTask;

public class DeleteAllMarker implements IObjectActionDelegate {

    private Set<IResource> resources;

    public DeleteAllMarker() {
        resources = new HashSet<IResource>();
    }

    public void run(IAction action) {

        for (IResource resource : resources) {
            try {
                resource.deleteMarkers(LPEXTask.ID, false, IResource.DEPTH_INFINITE);
            } catch (CoreException e) {
                ISpherePlugin.logError("*** Could delete LPEX task tags of resource '" + resource.getFullPath() + "' ***", e); //$NON-NLS-1$//$NON-NLS-2$
            }
        }

        resources.clear();
        setActionEnablement(action, resources);
    }

    public void selectionChanged(IAction action, ISelection selection) {

        resources.clear();
        if (selection.isEmpty()) {
            return;
        }

        if (selection instanceof TreeSelection) {
            TreeSelection treeSelection = (TreeSelection)selection;
            for (Iterator<?> iterator = treeSelection.iterator(); iterator.hasNext();) {
                Object item = iterator.next();
                if (item instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable)item;
                    if (adaptable.getAdapter(IMarker.class) instanceof IMarker) {
                        IMarker marker = (IMarker)adaptable.getAdapter(IMarker.class);
                        if (isLPEXTaskTag(marker)) {
                            IResource resource = marker.getResource();
                            resources.add(resource);
                        }
                    }
                }
            }
        }

        setActionEnablement(action, resources);
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    private boolean isLPEXTaskTag(IMarker marker) {
        return LPEXTask.ID.equals(getMarkerType(marker));
    }

    private Object getMarkerType(IMarker marker) {
        try {
            return marker.getType();
        } catch (CoreException e) {
            return null;
        }
    }

    private void setActionEnablement(IAction action, Set<IResource> resources) {

        if (resources.size() > 0) {
            action.setEnabled(true);
        } else {
            action.setEnabled(false);
        }
    }
}
