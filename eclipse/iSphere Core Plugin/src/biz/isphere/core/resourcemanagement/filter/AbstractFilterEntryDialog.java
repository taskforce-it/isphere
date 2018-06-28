/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

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
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractFilterEntryDialog extends AbstractEntryDialog {

    private static final String FILE_EXT_RSEFLT = "rseflt";
    private static final String FILE_EXT_RSEFLTALL = "rsefltall";

    private ComboViewer comboViewerProfile;
    private Combo comboProfile;
    private RSEProfile[] profiles;
    private boolean singleFilterPool;
    private Button checkBoxSingleFilterPool;
    private Label labelFilterPool;
    private ComboViewer comboViewerFilterPool;
    private Combo comboFilterPool;
    private RSEFilterPool[] filterPools;

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

    private class FilterPoolLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((RSEFilterPool)element).getName();
        }

        public Image getImage(Object element) {
            return null;
        }
    }

    private class FilterPoolContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return filterPools;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    public AbstractFilterEntryDialog(Shell parentShell) {
        super(parentShell);
        singleFilterPool = true;
    }

    protected String getTitle() {
        return Messages.RSE_Filter_Management;
    }

    protected String getFileSubject() {
        return Messages.RSE_Filters;
    }

    protected String getSubject() {
        return Messages.filters;
    }

    protected String getFileExtension() {
        if (isEditRepository()) {
            return null;
        } else {
            if (singleFilterPool) {
                return FILE_EXT_RSEFLT;
            } else {
                return FILE_EXT_RSEFLTALL;
            }
        }
    }

    protected String[] getFileExtensions() {
        return new String[] { FILE_EXT_RSEFLT, FILE_EXT_RSEFLTALL };
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

            Label labelSingleFilterPool = new Label(compositeWorkspace, SWT.NONE);
            labelSingleFilterPool.setText(Messages.Single_filter_pool + ":");
            labelSingleFilterPool.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            checkBoxSingleFilterPool = WidgetFactory.createCheckbox(compositeWorkspace);
            checkBoxSingleFilterPool.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    singleFilterPool = checkBoxSingleFilterPool.getSelection();
                    setFilterPool();
                    check();
                }
            });

            labelFilterPool = new Label(compositeWorkspace, SWT.NONE);
            labelFilterPool.setText(Messages.Filter_pool + ":");
            labelFilterPool.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            comboViewerFilterPool = new ComboViewer(compositeWorkspace, SWT.READ_ONLY);
            comboViewerFilterPool.setLabelProvider(new FilterPoolLabelProvider());
            comboViewerFilterPool.setContentProvider(new FilterPoolContentProvider());
            comboFilterPool = comboViewerFilterPool.getCombo();
            comboFilterPool.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            comboViewerProfile.setSelection(new StructuredSelection(profiles[0]), true);

            setProfile(profiles[0]);

        }

    }

    private void setProfile(RSEProfile profile) {
        filterPools = getFilterPools(profile);
        if (filterPools.length == 0) {
            singleFilterPool = false;
            checkBoxSingleFilterPool.setEnabled(false);
        } else {
            checkBoxSingleFilterPool.setEnabled(true);
        }
        checkBoxSingleFilterPool.setSelection(singleFilterPool);
        comboViewerFilterPool.setInput(new Object());
        for (int idx = 0; idx < filterPools.length; idx++) {
            if (!filterPools[idx].isDefault()) {
                comboViewerFilterPool.setSelection(new StructuredSelection(filterPools[idx]), true);
                break;
            }
        }
        setFilterPool();
    }

    private void setFilterPool() {
        labelFilterPool.setVisible(singleFilterPool);
        comboFilterPool.setVisible(singleFilterPool);
    }

    protected String checkWorkspaceArea() {
        if (profiles.length == 0) {
            return Messages.No_profiles_available + ".";
        } else if (singleFilterPool && filterPools.length == 0) {
            return Messages.No_filter_pools_available + ".";
        } else if (singleFilterPool && getFilterPool() == null) {
            return Messages.No_filter_pool_selected + ".";
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

    private RSEFilterPool getFilterPool() {
        if (singleFilterPool && filterPools.length > 0 && comboViewerFilterPool.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerFilterPool.getSelection();
            return (RSEFilterPool)structuredSelection.getFirstElement();
        } else {
            return null;
        }
    }

    protected int run() {

        if (isEditRepository()) {
            if (getRepository().endsWith(".rseflt")) {
                singleFilterPool = true;
            } else if (getRepository().endsWith(".rsefltall")) {
                singleFilterPool = false;
            } else {
                return IDialogConstants.CANCEL_ID;
            }
        }

        String workspace = null;
        RSEProfile profile = null;
        RSEFilterPool filterPool = null;
        RSEFilter[] filtersWorkspace = null;
        if (isEditBoth() || isEditWorkspace()) {
            profile = getProfile();
            if (profile != null) {
                if (singleFilterPool) {
                    filterPool = getFilterPool();
                    if (filterPool != null) {
                        workspace = profile.getName() + ":" + filterPool.getName();
                        filtersWorkspace = getFilters(filterPool);
                    }
                } else {
                    workspace = profile.getName() + ":" + Messages.All_filter_pools;
                    filtersWorkspace = getFilters(profile);
                }
            }
        }

        String repository = null;
        RSEFilter[] filtersRepository = null;
        if (isEditBoth() || isEditRepository()) {
            repository = getRepository();
            if (repository != null) {
                filtersRepository = restoreFiltersFromXML(new File(repository), singleFilterPool, profile, filterPool);
                if (filtersRepository == null) {
                    return IDialogConstants.BACK_ID;
                }
            }
        }

        RSEFilter[] resourcesWorkspace = null;
        RSEFilter[] resourcesRepository = null;
        RSEFilterBoth[] resourcesBothDifferent = null;
        RSEFilter[] resourcesBothEqual = null;

        if (filtersWorkspace != null && filtersWorkspace.length > 0 && filtersRepository != null && filtersRepository.length > 0) {

            FilterComparator comparator = new FilterComparator(filtersWorkspace, filtersRepository);

            if (comparator.getResourcesWorkspace().size() > 0) {
                resourcesWorkspace = new RSEFilter[comparator.getResourcesWorkspace().size()];
                resourcesWorkspace = comparator.getResourcesWorkspace().toArray(resourcesWorkspace);
            }

            if (comparator.getResourcesRepository().size() > 0) {
                resourcesRepository = new RSEFilter[comparator.getResourcesRepository().size()];
                resourcesRepository = comparator.getResourcesRepository().toArray(resourcesRepository);
            }

            if (comparator.getResourcesBothDifferent().size() > 0) {
                resourcesBothDifferent = new RSEFilterBoth[comparator.getResourcesBothDifferent().size()];
                resourcesBothDifferent = comparator.getResourcesBothDifferent().toArray(resourcesBothDifferent);
            }

            if (comparator.getResourcesBothEqual().size() > 0) {
                resourcesBothEqual = new RSEFilter[comparator.getResourcesBothEqual().size()];
                resourcesBothEqual = comparator.getResourcesBothEqual().toArray(resourcesBothEqual);
            }

        } else if (filtersWorkspace != null && filtersWorkspace.length > 0) {
            resourcesWorkspace = filtersWorkspace;
        } else if (filtersRepository != null && filtersRepository.length > 0) {
            resourcesRepository = filtersRepository;
        }

        return openEditingDialog(getShell(), isEditWorkspace(), isEditRepository(), isEditBoth(), singleFilterPool, workspace, repository,
            resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);

    }

    protected boolean createEmptyRepository(File repository) {
        return saveFiltersToXML(repository, singleFilterPool, new RSEFilter[0]);
    }

    protected abstract RSEProfile[] getProfiles();

    protected abstract RSEFilterPool[] getFilterPools(RSEProfile profile);

    protected abstract RSEFilter[] getFilters(RSEProfile profile);

    protected abstract RSEFilter[] getFilters(RSEFilterPool filterPool);

    protected abstract int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth,
        boolean singleFilterPool, String workspace, String repository, RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository,
        RSEFilterBoth[] resourceBothDifferent, RSEFilter[] resourceBothEqual);

    protected abstract boolean saveFiltersToXML(File toFile, boolean singleFilterPool, RSEFilter[] filters);

    protected abstract RSEFilter[] restoreFiltersFromXML(File fromFile, boolean singleFilterPool, RSEProfile profile, RSEFilterPool filterPool);

}
