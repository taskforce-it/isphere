/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractEditingDialog;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractUserActionEditingDialog extends AbstractEditingDialog {

    private boolean singleDomain;
    private RSEUserAction[] resourceWorkspace;
    private RSEUserAction[] resourceRepository;
    private RSEUserActionBoth[] resourceBothDifferent;
    private RSEUserAction[] resourceBothEqual;

    public AbstractUserActionEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, boolean singleDomain,
        String workspace, String repository, RSEUserAction[] resourceWorkspace, RSEUserAction[] resourceRepository,
        RSEUserActionBoth[] resourceBothDifferent, RSEUserAction[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
        this.singleDomain = singleDomain;
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
        this.resourceBothDifferent = resourceBothDifferent;
        this.resourceBothEqual = resourceBothEqual;
    }

    @Override
    protected String getTitle() {
        return Messages.RSE_UserAction_Management;
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
        return new UserActionEditingAreaWorkspace(container, resourceWorkspace, both, singleDomain);
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
        return new UserActionEditingAreaRepository(container, resourceRepository, both, singleDomain);
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
        return new UserActionEditingAreaBothDifferent(container, resourceBothDifferent, both, singleDomain);
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
        return new UserActionEditingAreaBothEqual(container, resourceBothEqual, both, singleDomain);
    }

    @Override
    protected String getWorkspaceText() {
        return Messages.Domain;
    }

    @Override
    protected boolean saveRepository(String repository, ArrayList<AbstractResource> newRepository) {
        RSEUserAction[] userActions = new RSEUserAction[newRepository.size()];
        newRepository.toArray(userActions);
        return saveUserActionsToXML(new File(repository), singleDomain, userActions);
    }

    protected abstract boolean saveUserActionsToXML(File toFile, boolean singleDomain, RSEUserAction[] commands);

}
