/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
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

public abstract class AbstractUserActionEntryDialog extends AbstractEntryDialog {

    private static final String PROFILE = "PROFILE";
    private static final String SINGLE_DOMAIN = "SINGLE_DOMAIN";
    private static final String DOMAIN = "DOMAIN";

    private static final String FILE_EXT_RSECMD = "rseuda";
    private static final String FILE_EXT_RSECMDALL = "rseudaall";

    private ComboViewer comboViewerProfile;
    private Combo comboProfile;
    private RSEProfile[] profiles;
    private boolean singleDomain;
    private Button checkBoxSingleDomain;
    private Label labelDomain;
    private ComboViewer comboViewerDomains;
    private Combo comboDomain;
    private RSEDomain[] domains;

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

    private class DomainLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((RSEDomain)element).getName();
        }

        public Image getImage(Object element) {
            return null;
        }
    }

    private class DomainContentProvider implements IStructuredContentProvider {
        public Object[] getElements(Object inputElement) {
            return domains;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    public AbstractUserActionEntryDialog(Shell parentShell) {
        super(parentShell);
        singleDomain = true;
    }

    protected String getTitle() {
        return Messages.RSE_UserAction_Management;
    }

    protected String getFileSubject() {
        return Messages.RSE_UserActions;
    }

    protected String getSubject() {
        return Messages.userActions;
    }

    protected String getFileExtension() {
        if (isEditRepository()) {
            return null;
        } else {
            if (singleDomain) {
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

            Label labelSingleDomain = new Label(compositeWorkspace, SWT.NONE);
            labelSingleDomain.setText(Messages.Single_domain + ":");
            labelSingleDomain.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            checkBoxSingleDomain = WidgetFactory.createCheckbox(compositeWorkspace);
            checkBoxSingleDomain.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    singleDomain = checkBoxSingleDomain.getSelection();
                    setDomain();
                    check();
                }
            });

            labelDomain = new Label(compositeWorkspace, SWT.NONE);
            labelDomain.setText(Messages.Domain + ":");
            labelDomain.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

            comboViewerDomains = new ComboViewer(compositeWorkspace, SWT.READ_ONLY);
            comboViewerDomains.setLabelProvider(new DomainLabelProvider());
            comboViewerDomains.setContentProvider(new DomainContentProvider());
            comboDomain = comboViewerDomains.getCombo();
            comboDomain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

            // comboViewerProfile.setSelection(new
            // StructuredSelection(profiles[0]), true);

            // setProfile(profiles[0]);

        }

    }

    protected void loadWorkspaceValues() {

        singleDomain = loadBooleanValue(SINGLE_DOMAIN, true);

        String profileName = loadValue(PROFILE, null);
        if (profileName != null) {
            for (RSEProfile profile : profiles) {
                if (profileName.equals(profile.getName())) {
                    comboViewerProfile.setSelection(new StructuredSelection(profile), true);
                    setProfile(profile);
                }
            }
        }

        if (comboViewerProfile.getSelection().isEmpty() && profiles.length > 0) {
            comboViewerProfile.setSelection(new StructuredSelection(profiles[0]), true);
            setProfile(profiles[0]);
        }

        String domainName = loadValue(DOMAIN, null);
        if (domainName != null) {
            for (RSEDomain domain : domains) {
                if (domainName.equals(domain.getName())) {
                    comboViewerDomains.setSelection(new StructuredSelection(domain), true);
                }
            }
        }

        if (comboViewerDomains.getSelection().isEmpty() && domains.length > 0) {
            comboViewerDomains.setSelection(new StructuredSelection(domains[0]), true);
        }
    }

    protected void storeWorkspaceValues() {

        ISelection selection = comboViewerProfile.getSelection();
        if (selection instanceof StructuredSelection) {
            Object element = ((StructuredSelection)selection).getFirstElement();
            if (element instanceof RSEProfile) {
                RSEProfile profile = (RSEProfile)element;
                String profileName = profile.getName();
                storeValue(PROFILE, profileName);
            }
        }

        storeValue(SINGLE_DOMAIN, singleDomain);

        selection = comboViewerDomains.getSelection();
        if (selection instanceof StructuredSelection) {
            Object element = ((StructuredSelection)selection).getFirstElement();
            if (element instanceof RSEDomain) {
                RSEDomain domain = (RSEDomain)element;
                String domainName = domain.getName();
                storeValue(DOMAIN, domainName);
            }
        }
    }

    private void setProfile(RSEProfile profile) {

        domains = getDomains(profile);
        if (domains.length == 0) {
            singleDomain = false;
            checkBoxSingleDomain.setEnabled(false);
        } else {
            checkBoxSingleDomain.setEnabled(true);
        }

        checkBoxSingleDomain.setSelection(singleDomain);
        comboViewerDomains.setInput(new Object());
        if (domains.length == 0) {
            comboViewerDomains.setSelection(null);
        } else {
            comboViewerDomains.setSelection(new StructuredSelection(domains[0]), true);
        }

        setDomain();

        check();
    }

    private void setDomain() {
        labelDomain.setVisible(singleDomain);
        comboDomain.setVisible(singleDomain);
    }

    protected String checkWorkspaceArea() {
        if (profiles.length == 0) {
            return Messages.No_domains_available + ".";
        } else if (singleDomain && domains.length == 0) {
            return Messages.No_domains_available + ".";
        } else if (singleDomain && getDomain() == null) {
            return Messages.No_domains_selected + ".";
        } else {
            return null;
        }
    };

    protected RSEProfile getProfile() {
        if (profiles.length > 0 && comboViewerProfile.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerProfile.getSelection();
            return (RSEProfile)structuredSelection.getFirstElement();
        } else {
            return null;
        }
    }

    private RSEDomain getDomain() {
        if (singleDomain && domains.length > 0 && comboViewerDomains.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerDomains.getSelection();
            return (RSEDomain)structuredSelection.getFirstElement();
        } else {
            return null;
        }
    }

    protected int run() {

        if (isEditRepository()) {
            if (getRepository().endsWith(".rsecmd")) {
                singleDomain = true;
            } else if (getRepository().endsWith(".rsecmdall")) {
                singleDomain = false;
            } else {
                return IDialogConstants.CANCEL_ID;
            }
        }

        String workspace = null;
        RSEProfile profile = null;
        RSEDomain domain = null;
        RSEUserAction[] userActionsWorkspace = null;
        if (isEditBoth() || isEditWorkspace()) {
            profile = getProfile();
            if (profile != null) {
                if (singleDomain) {
                    domain = getDomain();
                    if (domain != null) {
                        workspace = profile.getName() + ":" + domain.getName();
                        userActionsWorkspace = getUserActions(domain);
                    }
                } else {
                    workspace = profile.getName() + ":" + Messages.All_domains;
                    userActionsWorkspace = getUserActions(profile);
                }
            }
        }

        String repository = null;
        RSEUserAction[] userActionsRepository = null;
        if (isEditBoth() || isEditRepository()) {
            repository = getRepository();
            if (repository != null) {
                userActionsRepository = restoreUserActionsFromXML(new File(repository), singleDomain, profile, domain);
                if (userActionsRepository == null) {
                    return IDialogConstants.BACK_ID;
                }
            }
        }

        RSEUserAction[] resourcesWorkspace = null;
        RSEUserAction[] resourcesRepository = null;
        RSEUserActionBoth[] resourcesBothDifferent = null;
        RSEUserAction[] resourcesBothEqual = null;

        if (userActionsWorkspace != null && userActionsWorkspace.length > 0 && userActionsRepository != null && userActionsRepository.length > 0) {

            UserActionComparator comparator = new UserActionComparator(userActionsWorkspace, userActionsRepository);

            if (comparator.getResourcesWorkspace().size() > 0) {
                resourcesWorkspace = new RSEUserAction[comparator.getResourcesWorkspace().size()];
                resourcesWorkspace = comparator.getResourcesWorkspace().toArray(resourcesWorkspace);
            }

            if (comparator.getResourcesRepository().size() > 0) {
                resourcesRepository = new RSEUserAction[comparator.getResourcesRepository().size()];
                resourcesRepository = comparator.getResourcesRepository().toArray(resourcesRepository);
            }

            if (comparator.getResourcesBothDifferent().size() > 0) {
                resourcesBothDifferent = new RSEUserActionBoth[comparator.getResourcesBothDifferent().size()];
                resourcesBothDifferent = comparator.getResourcesBothDifferent().toArray(resourcesBothDifferent);
            }

            if (comparator.getResourcesBothEqual().size() > 0) {
                resourcesBothEqual = new RSEUserAction[comparator.getResourcesBothEqual().size()];
                resourcesBothEqual = comparator.getResourcesBothEqual().toArray(resourcesBothEqual);
            }

        } else if (userActionsWorkspace != null && userActionsWorkspace.length > 0) {
            resourcesWorkspace = userActionsWorkspace;
        } else if (userActionsRepository != null && userActionsRepository.length > 0) {
            resourcesRepository = userActionsRepository;
        }

        if (isEmptyResourceCollection(resourcesWorkspace) && isEmptyResourceCollection(resourcesRepository)
            && isEmptyResourceCollection(resourcesBothDifferent) && isEmptyResourceCollection(resourcesBothEqual)) {

            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, Messages.No_items_found_that_match_the_selection_criterias);

            return IDialogConstants.BACK_ID;
        }

        return openEditingDialog(getShell(), isEditWorkspace(), isEditRepository(), isEditBoth(), singleDomain, workspace, repository,
            resourcesWorkspace, resourcesRepository, resourcesBothDifferent, resourcesBothEqual);

    }

    protected boolean createEmptyRepository(File repository) {
        return saveUserActionsToXML(repository, singleDomain, new RSEUserAction[0]);
    }

    protected abstract RSEProfile[] getProfiles();

    protected abstract RSEDomain[] getDomains(RSEProfile profile);

    protected abstract RSEUserAction[] getUserActions(RSEProfile profile);

    protected abstract RSEUserAction[] getUserActions(RSEDomain domain);

    protected abstract int openEditingDialog(Shell parentShell, boolean editWorkspace, boolean editRepository, boolean editBoth,
        boolean singleDomain, String workspace, String repository, RSEUserAction[] resourceWorkspace, RSEUserAction[] resourceRepository,
        RSEUserActionBoth[] resourceBothDifferent, RSEUserAction[] resourceBothEqual);

    protected abstract boolean saveUserActionsToXML(File toFile, boolean singleDomain, RSEUserAction[] userActions);

    protected abstract RSEUserAction[] restoreUserActionsFromXML(File fromFile, boolean singleDomain, RSEProfile profile, RSEDomain domain);

}
