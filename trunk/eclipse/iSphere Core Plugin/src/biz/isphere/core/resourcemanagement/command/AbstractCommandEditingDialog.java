/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractEditingArea;
import biz.isphere.core.resourcemanagement.AbstractEditingDialog;
import biz.isphere.core.resourcemanagement.AbstractResource;

public abstract class AbstractCommandEditingDialog extends AbstractEditingDialog {

    private boolean singleCompileType;
    private RSECommand[] resourceWorkspace;
    private RSECommand[] resourceRepository;
    private RSECommandBoth[] resourceBothDifferent;
    private RSECommand[] resourceBothEqual;

    public AbstractCommandEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth,
        boolean singleCompileType, String workspace, String repository, RSECommand[] resourceWorkspace, RSECommand[] resourceRepository,
        RSECommandBoth[] resourceBothDifferent, RSECommand[] resourceBothEqual) {
        super(parentShell, editWorkspace, editRepository, editBoth, workspace, repository, resourceWorkspace, resourceRepository,
            resourceBothDifferent, resourceBothEqual);
        this.singleCompileType = singleCompileType;
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
        this.resourceBothDifferent = resourceBothDifferent;
        this.resourceBothEqual = resourceBothEqual;
    }

    @Override
    protected String getTitle() {
        return Messages.RSE_Command_Management;
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
        return new CommandEditingAreaWorkspace(container, resourceWorkspace, both, singleCompileType);
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
        return new CommandEditingAreaRepository(container, resourceRepository, both, singleCompileType);
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
        return new CommandEditingAreaBothDifferent(container, resourceBothDifferent, both, singleCompileType);
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
        return new CommandEditingAreaBothEqual(container, resourceBothEqual, both, singleCompileType);
    }

    @Override
    protected String getWorkspaceText() {
        return Messages.Compile_type;
    }

    @Override
    protected boolean saveRepository(String repository, ArrayList<AbstractResource> newRepository) {
        RSECommand[] commands = new RSECommand[newRepository.size()];
        newRepository.toArray(commands);
        return saveCommandsToXML(new File(repository), singleCompileType, commands);
    }

    protected abstract boolean saveCommandsToXML(File toFile, boolean singleCompileType, RSECommand[] commands);

}
