/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.internal.UIHelper;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.CustomExpandBar;
import biz.isphere.core.swt.widgets.CustomExpandItem;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractEditingDialog extends Dialog {

    private boolean editWorkspace;
    private boolean editRepository;
    private boolean editBoth;
    private String workspace;
    private String repository;
    private AbstractResource[] resourceWorkspace;
    private AbstractResource[] resourceRepository;
    private AbstractResourceBoth<? extends AbstractResource>[] resourceBothDifferent;
    private AbstractResource[] resourceBothEqual;
    private ArrayList<AbstractResource> newRepository;

    public AbstractEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth, String workspace,
        String repository, AbstractResource[] resourceWorkspace, AbstractResource[] resourceRepository,
        AbstractResourceBoth<? extends AbstractResource>[] resourceBothDifferent, AbstractResource[] resourceBothEqual) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.editWorkspace = editWorkspace;
        this.editRepository = editRepository;
        this.editBoth = editBoth;
        this.workspace = workspace;
        this.repository = repository;
        this.resourceWorkspace = resourceWorkspace;
        this.resourceRepository = resourceRepository;
        this.resourceBothDifferent = resourceBothDifferent;
        this.resourceBothEqual = resourceBothEqual;
        if (editRepository || editBoth) {
            newRepository = new ArrayList<AbstractResource>();
        }
    }

    protected Control createContents(Composite parent) {

        // changed for dark mode
        // create the top level composite for the dialog
        Composite composite;
        if (!UIHelper.isDarkMode()) {
            composite = new Composite(parent, 0);
        } else {
            composite = parent.getShell();
        }

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);

        // initialize the dialog units
        initializeDialogUnits(composite);

        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);
        buttonBar = createButtonBar(composite);

        return composite;
    }

    protected Control createDialogArea(Composite parent) {

        // changed for dark mode
        Composite container;
        if (!UIHelper.isDarkMode()) {
            container = (Composite)super.createDialogArea(parent);
        } else {
            container = parent;
        }
        container.setLayout(new GridLayout(1, false));

        Composite compositeHeader = new Composite(container, SWT.NONE);
        compositeHeader.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        compositeHeader.setLayout(new GridLayout(2, false));

        if (editWorkspace || editBoth) {

            Text textWorkspace = createTextWithLabel(compositeHeader, getWorkspaceText() + ":");
            textWorkspace.setText(workspace);

        }

        if (editRepository || editBoth) {

            Text textRepository = createTextWithLabel(compositeHeader, Messages.Repository + ":");
            textRepository.setText(repository);

        }

        CustomExpandBar expandBar = new CustomExpandBar(container, SWT.V_SCROLL);
        expandBar.setLayoutData(new GridData(GridData.FILL_BOTH));
        expandBar.setSpacing(5);

        if (isEditingAreaWorkspace()) {
            CustomExpandItem expandItemWorkspace = new CustomExpandItem(expandBar, SWT.NONE,
                CustomExpandItem.TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT, 0);
            expandItemWorkspace.setExpanded(true);
            AbstractEditingArea areaWorkspace = getEditingAreaWorkspace(expandBar, editBoth);
            expandItemWorkspace.setText(areaWorkspace.getTitle());
            expandItemWorkspace.setControl(areaWorkspace);
            areaWorkspace.setInput();
        }

        if (isEditingAreaRepository()) {
            CustomExpandItem expandItemRepository = new CustomExpandItem(expandBar, SWT.NONE,
                CustomExpandItem.TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT, 0);
            expandItemRepository.setExpanded(true);
            AbstractEditingArea areaRepository = getEditingAreaRepository(expandBar, editBoth);
            expandItemRepository.setText(areaRepository.getTitle());
            expandItemRepository.setControl(areaRepository);
            areaRepository.setInput();
        }

        if (isEditingAreaBothDifferent()) {
            CustomExpandItem expandItemBothDifferent = new CustomExpandItem(expandBar, SWT.NONE,
                CustomExpandItem.TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT, 0);
            expandItemBothDifferent.setExpanded(true);
            AbstractEditingArea areaBothDifferent = getEditingAreaBothDifferent(expandBar, editBoth);
            expandItemBothDifferent.setText(areaBothDifferent.getTitle());
            expandItemBothDifferent.setControl(areaBothDifferent);
            areaBothDifferent.setInput();
        }

        if (isEditingAreaBothEqual()) {
            CustomExpandItem expandItemBothEqual = new CustomExpandItem(expandBar, SWT.NONE,
                CustomExpandItem.TYPE_GRAB_EXCESS_HEIGHT_WITH_MINIMUM_PACK_HEIGHT, 0);
            expandItemBothEqual.setExpanded(true);
            AbstractEditingArea areaBothEqual = getEditingAreaBothEqual(expandBar, editBoth);
            expandItemBothEqual.setText(areaBothEqual.getTitle());
            expandItemBothEqual.setControl(areaBothEqual);
            areaBothEqual.setInput();
        }

        expandBar.alignHeight();

        return container;

    }

    private Text createTextWithLabel(Composite compositeHeader, String string) {

        Label label = new Label(compositeHeader, SWT.NONE);
        label.setText(getWorkspaceText() + ":");
        label.setLayoutData(new GridData(GridData.BEGINNING));

        Text text = WidgetFactory.createReadOnlyText(compositeHeader);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return text;
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.BACK_ID) {
            backPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    protected void backPressed() {

        setReturnCode(IDialogConstants.BACK_ID);
        close();
    }

    protected void okPressed() {

        if (!hasActions(resourceWorkspace, resourceRepository, resourceBothDifferent, resourceBothEqual)) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.No_actions_selected);
            return;
        }

        MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
        messageBox.setMessage(Messages.Are_you_sure + "?");
        messageBox.setText(Messages.Perform_actions);
        int response = messageBox.open();

        if (response == SWT.NO) {
            return;
        } else if (response == SWT.YES) {

            boolean repositoryAction = false;

            if (resourceWorkspace != null) {
                for (int idx = 0; idx < resourceWorkspace.length; idx++) {
                    if (resourceWorkspace[idx].getAction() != null) {
                        if (resourceWorkspace[idx].getAction().equals(AbstractResource.DELETE_FROM_WORKSPACE)) {
                            deleteFromWorkspace(resourceWorkspace[idx]);
                        } else if (resourceWorkspace[idx].getAction().equals(AbstractResource.PUSH_TO_REPOSITORY)) {
                            repositoryAction = true;
                            pushToRepository(resourceWorkspace[idx]);
                        }
                    }
                }
            }

            if (resourceRepository != null) {
                for (int idx = 0; idx < resourceRepository.length; idx++) {
                    if (resourceRepository[idx].getAction() != null) {
                        if (resourceRepository[idx].getAction().equals(AbstractResource.DELETE_FROM_REPOSITORY)) {
                            repositoryAction = true;
                            continue; // Do nothing for delete from repository
                        } else if (resourceRepository[idx].getAction().equals(AbstractResource.PUSH_TO_WORKSPACE)) {
                            pushToWorkspace(resourceRepository[idx]);
                            pushToRepository(resourceRepository[idx]);
                        }
                    } else {
                        pushToRepository(resourceRepository[idx]);
                    }
                }
            }

            if (resourceBothDifferent != null) {
                for (int idx = 0; idx < resourceBothDifferent.length; idx++) {
                    if (resourceBothDifferent[idx].getAction() != null) {
                        if (resourceBothDifferent[idx].getAction().equals(AbstractResource.DELETE_FROM_WORKSPACE)) {
                            deleteFromWorkspace(resourceBothDifferent[idx].getResourceWorkspace());
                            pushToRepository(resourceBothDifferent[idx].getResourceRepository());
                        } else if (resourceBothDifferent[idx].getAction().equals(AbstractResource.PUSH_TO_REPOSITORY)) {
                            repositoryAction = true;
                            pushToRepository(resourceBothDifferent[idx].getResourceWorkspace());
                        } else if (resourceBothDifferent[idx].getAction().equals(AbstractResource.DELETE_FROM_REPOSITORY)) {
                            repositoryAction = true;
                            continue; // Do nothing for delete from repository
                        } else if (resourceBothDifferent[idx].getAction().equals(AbstractResource.PUSH_TO_WORKSPACE)) {
                            updateWorkspace(resourceBothDifferent[idx].getResourceWorkspace(), resourceBothDifferent[idx].getResourceRepository());
                            pushToRepository(resourceBothDifferent[idx].getResourceRepository());
                        } else if (resourceBothDifferent[idx].getAction().equals(AbstractResource.DELETE_FROM_BOTH)) {
                            repositoryAction = true;
                            deleteFromWorkspace(resourceBothDifferent[idx].getResourceWorkspace());
                            continue; // Do nothing for delete from repository
                        }
                    } else {
                        pushToRepository(resourceBothDifferent[idx].getResourceRepository());
                    }
                }
            }

            if (resourceBothEqual != null) {
                for (int idx = 0; idx < resourceBothEqual.length; idx++) {
                    if (resourceBothEqual[idx].getAction() != null) {
                        if (resourceBothEqual[idx].getAction().equals(AbstractResource.DELETE_FROM_WORKSPACE)) {
                            deleteFromWorkspace(resourceBothEqual[idx]);
                            pushToRepository(resourceBothEqual[idx]);
                        } else if (resourceBothEqual[idx].getAction().equals(AbstractResource.DELETE_FROM_REPOSITORY)) {
                            repositoryAction = true;
                            continue; // Do nothing for delete from repository
                        } else if (resourceBothEqual[idx].getAction().equals(AbstractResource.DELETE_FROM_BOTH)) {
                            repositoryAction = true;
                            deleteFromWorkspace(resourceBothEqual[idx]);
                            continue; // Do nothing for delete from repository
                        }
                    } else {
                        pushToRepository(resourceBothEqual[idx]);
                    }
                }
            }

            if (repositoryAction) {
                saveRepository(repository, newRepository);
            }

        }

        super.okPressed();
    }

    private boolean hasActions(AbstractResource[] resourceWorkspace, AbstractResource[] resourceRepository,
        AbstractResourceBoth<? extends AbstractResource>[] resourceBothDifferent, AbstractResource[] resourceBothEqual) {

        if (hasActions(resourceWorkspace)) {
            return true;
        }

        if (hasActions(resourceRepository)) {
            return true;
        }

        if (hasActions(resourceBothDifferent)) {
            return true;
        }

        if (hasActions(resourceBothEqual)) {
            return true;
        }

        return false;
    }

    private boolean hasActions(AbstractResource[] resourceWorkspace) {

        if (resourceWorkspace != null) {
            for (AbstractResource resource : resourceWorkspace) {
                if (!StringHelper.isNullOrEmpty(resource.getAction())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void pushToRepository(AbstractResource resource) {
        newRepository.add(resource);
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.BACK_ID, Messages.Back, true);
        createButton(parent, IDialogConstants.OK_ID, Messages.Perform_actions, true);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getTitle());
    }

    protected Point getInitialSize() {
        return getShell().computeSize(Size.getSize(1250), Size.getSize(750), true);
    }

    protected abstract String getTitle();

    protected abstract boolean isEditingAreaWorkspace();

    protected abstract AbstractEditingArea getEditingAreaWorkspace(Composite container, boolean both);

    protected abstract boolean isEditingAreaRepository();

    protected abstract AbstractEditingArea getEditingAreaRepository(Composite container, boolean both);

    protected abstract boolean isEditingAreaBothDifferent();

    protected abstract AbstractEditingArea getEditingAreaBothDifferent(Composite container, boolean both);

    protected abstract boolean isEditingAreaBothEqual();

    protected abstract AbstractEditingArea getEditingAreaBothEqual(Composite container, boolean both);

    protected abstract String getWorkspaceText();

    protected abstract void pushToWorkspace(AbstractResource resource);

    protected abstract void deleteFromWorkspace(AbstractResource resource);

    protected void updateWorkspace(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
        deleteFromWorkspace(resourceWorkspace);
        pushToWorkspace(resourceRepository);
    }

    protected abstract boolean saveRepository(String repository, ArrayList<AbstractResource> newRepository);

}