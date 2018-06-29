/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.resourcemanagement.AbstractEntryDialog;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractCommandEntryDialog extends AbstractEntryDialog {

    private static final String FILE_EXT_RSECMD = "rsecmd";
    private static final String FILE_EXT_RSECMDALL = "rsecmdall";

    private ComboViewer comboViewerProfile;
    private Combo comboProfile;
    private RSEProfile[] profiles;
    private boolean singleCompileType;
    private Button checkBoxSingleCompileType;
    private Label labelCompileType;
    private ComboViewer comboViewerCompileTypes;
    private Combo comboCompileType;
    private RSECompileType[] compileTypes;

    private class ProfileLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((RSEProfile)element).getName();
        }

        public Image getImage(Object element) {
            return null;
        }
    }

    private class ProfileContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return profiles;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class CompileTypeLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((RSECompileType)element).getType();
        }

        public Image getImage(Object element) {
            return null;
        }
    }

    private class CompileTypeContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return compileTypes;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    public AbstractCommandEntryDialog(Shell parentShell) {
        super(parentShell);
        singleCompileType = true;
    }

    protected String getTitle() {
        return Messages.RSE_Command_Management;
    }

    protected String getFileSubject() {
        return Messages.RSE_Commands;
    }

    protected String getSubject() {
        return Messages.commands;
    }

    protected String getFileExtension() {
        if (isEditRepository()) {
            return null;
        } else {
            if (singleCompileType) {
                return FILE_EXT_RSECMD;
            } else {
                return FILE_EXT_RSECMDALL;
            }
        }
    }

    protected String[] getFileExtensions() {
        return new String[] { FILE_EXT_RSECMD, FILE_EXT_RSECMDALL };
    }

    protected boolean needWorkspaceArea() {
        return true;
    }

    protected void configureWorkspaceArea(Composite compositeWorkspace) {

        profiles = getProfiles();

        if (profiles.length == 0) {

            return;

        } else {

            compositeWorkspace.setLayout(new GridLayout(2, false));

            Label labelProfile = new Label(compositeWorkspace, SWT.NONE);
            labelProfile.setText(Messages.Profile + ":");
            labelProfile.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            comboViewerProfile = new ComboViewer(compositeWorkspace, SWT.READ_ONLY);
            comboViewerProfile.setLabelProvider(new ProfileLabelProvider());
            comboViewerProfile.setContentProvider(new ProfileContentProvider());
            comboViewerProfile.setInput(new Object());
            comboProfile = comboViewerProfile.getCombo();
            comboProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            comboProfile.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setProfile(getProfile());
                }
            });

            Label labelSingleCompileType = new Label(compositeWorkspace, SWT.NONE);
            labelSingleCompileType.setText(Messages.Single_compile_type + ":");
            labelSingleCompileType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            checkBoxSingleCompileType = WidgetFactory.createCheckbox(compositeWorkspace);
            checkBoxSingleCompileType.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    singleCompileType = checkBoxSingleCompileType.getSelection();
                    setCompileType();
                    check();
                }
            });

            labelCompileType = new Label(compositeWorkspace, SWT.NONE);
            labelCompileType.setText(Messages.Compile_type + ":");
            labelCompileType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            comboViewerCompileTypes = new ComboViewer(compositeWorkspace, SWT.READ_ONLY);
            comboViewerCompileTypes.setLabelProvider(new CompileTypeLabelProvider());
            comboViewerCompileTypes.setContentProvider(new CompileTypeContentProvider());
            comboCompileType = comboViewerCompileTypes.getCombo();
            comboCompileType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            comboViewerProfile.setSelection(new StructuredSelection(profiles[0]), true);

            setProfile(profiles[0]);

        }

    }

    private void setProfile(RSEProfile profile) {

        compileTypes = getCompileTypes(profile);
        if (compileTypes.length == 0) {
            singleCompileType = false;
            checkBoxSingleCompileType.setEnabled(false);
        } else {
            checkBoxSingleCompileType.setEnabled(true);
        }

        checkBoxSingleCompileType.setSelection(singleCompileType);
        comboViewerCompileTypes.setInput(new Object());
        for (int idx = 0; idx < compileTypes.length; idx++) {
            if (!compileTypes[idx].isDefault()) {
                comboViewerCompileTypes.setSelection(new StructuredSelection(compileTypes[idx]), true);
                break;
            }
        }

        setCompileType();
    }

    private void setCompileType() {
        labelCompileType.setVisible(singleCompileType);
        comboCompileType.setVisible(singleCompileType);
    }

    protected String checkWorkspaceArea() {
        if (profiles.length == 0) {
            return Messages.No_profiles_available + ".";
        } else if (singleCompileType && compileTypes.length == 0) {
            return Messages.No_compile_types_available + ".";
        } else if (singleCompileType && getCompileType() == null) {
            return Messages.No_compile_types_selected + ".";
        } else {
            return null;
        }
    };

    private RSEProfile getProfile() {
        if (profiles.length > 0 && comboViewerProfile.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerProfile.getSelection();
            return (RSEProfile)structuredSelection.getFirstElement();
        } else {
            return null;
        }
    }

    private RSECompileType getCompileType() {
        if (singleCompileType && compileTypes.length > 0 && comboViewerCompileTypes.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerCompileTypes.getSelection();
            return (RSECompileType)structuredSelection.getFirstElement();
        } else {
            return null;
        }
    }

    protected int run() {

        if (isEditRepository()) {
            if (getRepository().endsWith(".rsecmd")) {
                singleCompileType = true;
            } else if (getRepository().endsWith(".rsecmdall")) {
                singleCompileType = false;
            } else {
                return IDialogConstants.CANCEL_ID;
            }
        }

        String workspace = null;
        RSEProfile profile = null;
        RSECompileType compileType = null;
        RSECommand[] commandsWorkspace = null;
        if (isEditBoth() || isEditWorkspace()) {
            profile = getProfile();
            if (profile != null) {
                if (singleCompileType) {
                    compileType = getCompileType();
                    if (compileType != null) {
                        workspace = profile.getName() + ":" + compileType.getType();
                        commandsWorkspace = getCommands(compileType);
                    }
                } else {
                    workspace = profile.getName() + ":" + Messages.All_compile_types;
                    commandsWorkspace = getCommands(profile);
                }
            }
        }

        String repository = null;
        RSECommand[] commandsRepository = null;
        if (isEditBoth() || isEditRepository()) {
            repository = getRepository();
            if (repository != null) {
                commandsRepository = restoreCommandsFromXML(new File(repository), singleCompileType, profile, compileType);
                if (commandsRepository == null) {
                    return IDialogConstants.BACK_ID;
                }
            }
        }

        RSECommand[] resourcesWorkspace = null;
        RSECommand[] resourcesRepository = null;
        RSECommandBoth[] resourcesBothDifferent = null;
        RSECommand[] resourcesBothEqual = null;

        if (commandsWorkspace != null && commandsWorkspace.length > 0 && commandsRepository != null && commandsRepository.length > 0) {

            CommandComparator comparator = new CommandComparator(commandsWorkspace, commandsRepository);

            if (comparator.getResourcesWorkspace().size() > 0) {
                resourcesWorkspace = new RSECommand[comparator.getResourcesWorkspace().size()];
                resourcesWorkspace = comparator.getResourcesWorkspace().toArray(resourcesWorkspace);
            }

            if (comparator.getResourcesRepository().size() > 0) {
                resourcesRepository = new RSECommand[comparator.getResourcesRepository().size()];
                resourcesRepository = comparator.getResourcesRepository().toArray(resourcesRepository);
            }

            if (comparator.getResourcesBothDifferent().size() > 0) {
                resourcesBothDifferent = new RSECommandBoth[comparator.getResourcesBothDifferent().size()];
                resourcesBothDifferent = comparator.getResourcesBothDifferent().toArray(resourcesBothDifferent);
            }

            if (comparator.getResourcesBothEqual().size() > 0) {
                resourcesBothEqual = new RSECommand[comparator.getResourcesBothEqual().size()];
                resourcesBothEqual = comparator.getResourcesBothEqual().toArray(resourcesBothEqual);
            }

        } else if (commandsWorkspace != null && commandsWorkspace.length > 0) {
            resourcesWorkspace = commandsWorkspace;
        } else if (commandsRepository != null && commandsRepository.length > 0) {
            resourcesRepository = commandsRepository;
        }

        // Resources that are only in the repository are always editable.
        // setResourcesEditable(resourcesRepository, true);

        return openEditingDialog(getShell(), isEditWorkspace(), isEditRepository(), isEditBoth(), singleCompileType, workspace, repository,
            resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);

    }

    protected boolean createEmptyRepository(File repository) {
        return saveCommandsToXML(repository, singleCompileType, new RSECommand[0]);
    }

    protected abstract RSEProfile[] getProfiles();

    protected abstract RSECompileType[] getCompileTypes(RSEProfile profile);

    protected abstract RSECommand[] getCommands(RSEProfile profile);

    protected abstract RSECommand[] getCommands(RSECompileType compileType);

    protected abstract int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth,
        boolean singleCompileType, String workspace, String repository, RSECommand[] resourceWorkspace, RSECommand[] resourceRepository,
        RSECommandBoth[] resourceBothDifferent, RSECommand[] resourceBothEqual);

    protected abstract boolean saveCommandsToXML(File toFile, boolean singleCompileType, RSECommand[] commands);

    protected abstract RSECommand[] restoreCommandsFromXML(File fromFile, boolean singleCompileType, RSEProfile profile, RSECompileType compileType);

}
