/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractEditingDialog;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractFilterEditingDialog extends AbstractEditingDialog {

    private boolean singleFilterPool;
    private RSEFilter[] resourceWorkspace;
    private RSEFilter[] resourceRepository;
    private RSEFilterBoth[] resourceBothDifferent;
    private RSEFilter[] resourceBothEqual;

    public AbstractFilterEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleFilterPool,
        String workspace, String repository, RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository, RSEFilterBoth[] resourceBothDifferent,
        RSEFilter[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
        this.singleFilterPool = singleFilterPool;
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
        this.resourceBothDifferent = resourceBothDifferent;
        this.resourceBothEqual = resourceBothEqual;
    }

    @Override
    protected String getTitle() {
        return Messages.RSE_Filter_Management;
    }

    @Override
    protected boolean isEditingAreaWorkspace() {
        if (resourceWorkspace != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected AbstractEditingArea getEditingAreaWorkspace(Composite container, boolean both) {
        return new FilterEditingAreaWorkspace(container, resourceWorkspace, both, singleFilterPool);
    }

    @Override
    protected boolean isEditingAreaRepository() {
        if (resourceRepository != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected AbstractEditingArea getEditingAreaRepository(Composite container, boolean both) {
        return new FilterEditingAreaRepository(container, resourceRepository, both, singleFilterPool);
    }

    @Override
    protected boolean isEditingAreaBothDifferent() {
        if (resourceBothDifferent != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected AbstractEditingArea getEditingAreaBothDifferent(Composite container, boolean both) {
        return new FilterEditingAreaBothDifferent(container, resourceBothDifferent, both, singleFilterPool);
    }

    @Override
    protected boolean isEditingAreaBothEqual() {
        if (resourceBothEqual != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected AbstractEditingArea getEditingAreaBothEqual(Composite container, boolean both) {
        return new FilterEditingAreaBothEqual(container, resourceBothEqual, both, singleFilterPool);
    }

    @Override
    protected String getWorkspaceText() {
        return Messages.Filter_pool;
    }

    @Override
    protected boolean saveRepository(String repository, ArrayList<AbstractResource> newRepository) {
        RSEFilter[] filters = new RSEFilter[newRepository.size()];
        newRepository.toArray(filters);
        return saveFiltersToXML(new File(repository), singleFilterPool, filters);
    }

    protected abstract boolean saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters);

}
