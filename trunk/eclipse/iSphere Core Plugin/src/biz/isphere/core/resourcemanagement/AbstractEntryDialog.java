/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;

public abstract class AbstractEntryDialog extends XDialog {

    private static final String REPOSITORY_PATH = "repositoryPath";

    private Shell shell;
    private Button buttonBoth;
    private Button buttonWorkspace;
    private Button buttonRepository;
    private Composite compositeWorkspace;
    private Composite compositeRepository;
    private Text textRepository;
    private Button okButton;

    public AbstractEntryDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.shell = parentShell;
    }

    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        final Group groupEditingArea = new Group(container, SWT.NONE);
        groupEditingArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        groupEditingArea.setText(Messages.Editing_area);
        final GridLayout gridLayoutStatus = new GridLayout();
        gridLayoutStatus.numColumns = 1;
        groupEditingArea.setLayout(gridLayoutStatus);

        buttonBoth = WidgetFactory.createRadioButton(groupEditingArea);
        buttonBoth.setText(Messages.Edit_workspace_and_repository + " " + getSubject());
        buttonBoth.setSelection(true);
        buttonBoth.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                compositeWorkspace.setVisible(true);
                compositeRepository.setVisible(true);
                check();
            }
        });

        buttonWorkspace = WidgetFactory.createRadioButton(groupEditingArea);
        buttonWorkspace.setText(Messages.Edit_only_workspace + " " + getSubject());
        buttonWorkspace.setSelection(false);
        buttonWorkspace.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                compositeWorkspace.setVisible(true);
                compositeRepository.setVisible(false);
                check();
            }
        });

        buttonRepository = WidgetFactory.createRadioButton(groupEditingArea);
        buttonRepository.setText(Messages.Edit_only_repository + " " + getSubject());
        buttonRepository.setSelection(false);
        buttonRepository.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                compositeWorkspace.setVisible(false);
                compositeRepository.setVisible(true);
                check();
            }
        });

        if (needWorkspaceArea()) {

            compositeWorkspace = new Composite(container, SWT.NONE);
            compositeWorkspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            compositeWorkspace.setVisible(true);

            configureWorkspaceArea(compositeWorkspace);

        }

        compositeRepository = new Composite(container, SWT.NONE);
        compositeRepository.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeRepository.setLayout(new GridLayout(3, false));
        compositeRepository.setVisible(true);

        Label labelRepository = new Label(compositeRepository, SWT.NONE);
        labelRepository.setText(Messages.Repository + ":");
        labelRepository.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        textRepository = WidgetFactory.createText(compositeRepository);
        textRepository.setText(Messages.EMPTY);
        textRepository.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        textRepository.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                check();
            }
        });

        Button browseRepository = WidgetFactory.createPushButton(compositeRepository);
        browseRepository.setText(Messages.Browse);
        browseRepository.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OK);
                dialog.setFilterNames(new String[] { getFileSubject() });
                String[] fileExtensions = getFileExtensionsInternal();
                StringBuffer extensions = new StringBuffer();
                for (int idx = 0; idx < fileExtensions.length; idx++) {
                    if (idx > 0) {
                        extensions.append(";");
                    }
                    extensions.append("*." + fileExtensions[idx]);
                }
                dialog.setFilterExtensions(new String[] { extensions.toString() });
                dialog.setFilterPath(loadRepositoryPath());
                // dialog.setFileName("export.xls");
                // dialog.setOverwrite(true);
                String file = dialog.open();
                if (file != null) {
                    textRepository.setText(file);
                    storeRepositoryPath(dialog.getFilterPath());
                }
            }
        });

        createStatusLine(container);

        check();

        return container;

    }

    protected void setResourcesEditable(AbstractResource[] resources, boolean editable) {

        if (resources == null) {
            return;
        }

        for (AbstractResource resource : resources) {
            resource.setEditable(editable);
        }
    }

    public void check() {

        if (needWorkspaceArea() && (buttonBoth.getSelection() || buttonWorkspace.getSelection())) {

            String error = checkWorkspaceArea();
            if (error != null) {
                if (okButton != null) okButton.setEnabled(false);
                setErrorMessage(error);
                return;
            }

        }

        if (buttonBoth.getSelection() || buttonRepository.getSelection()) {

            if (!checkRepositoryName()) {
                return;
            }

        }

        if (okButton != null) okButton.setEnabled(true);
        setErrorMessage(null);

    }

    private boolean checkRepositoryName() {

        String fileName = getRepositoryName();
        if (fileName.equals(Messages.EMPTY)) {
            if (okButton != null) okButton.setEnabled(false);
            setErrorMessage(Messages.Enter_a_file_name + ".");
            textRepository.setFocus();
            return false;
        }

        File repository = new File(fileName);
        try {
            repository.getCanonicalPath();
        } catch (IOException e) {
            if (okButton != null) okButton.setEnabled(false);
            setErrorMessage(Messages.bind(Messages.Invalid_file_name, fileName));
            textRepository.setFocus();
            return false;
        }

        if (getFileExtensionsInternal().length > 1) {
            if (!checkRepositoryNameExtension(fileName)) {
                displayRepositoryFileNameExtensionError();
                return false;
            }
        }

        return true;
    }

    private String getRepositoryName() {
        return textRepository.getText().trim();
    }

    private boolean checkRepositoryNameExtension(String fileName) {

        String[] fileExtensions = getFileExtensionsInternal();
        for (int idx = 0; idx < fileExtensions.length; idx++) {
            if (fileName.endsWith("." + fileExtensions[idx])) {
                return true;
            }
        }

        return false;
    }

    private void displayRepositoryFileNameExtensionError() {

        if (okButton != null) {
            okButton.setEnabled(false);
        }
        setErrorMessage(Messages.File_name_does_not_end_with + " " + getRepositoryNameExtensionsAsString());
        textRepository.setFocus();
    }

    private String getRepositoryNameExtensionsAsString() {

        StringBuffer extensions = new StringBuffer();
        String[] fileExtensions = getFileExtensionsInternal();
        for (int idx = 0; idx < fileExtensions.length; idx++) {
            if (idx > 0) {
                extensions.append(", ");
            }
            extensions.append("." + fileExtensions[idx]);
        }

        return extensions.toString();
    }

    protected void okPressed() {

        String repository = getRepository();

        if (!checkRepositoryNameExtension(repository)) {
            if (getFileExtensionsInternal().length == 1) {
                repository = repository + "." + getFileExtensionsInternal()[0]; //$NON-NLS-1$ 
                textRepository.setText(repository);
            } else {
                displayRepositoryFileNameExtensionError();
            }
        }

        boolean run = true;

        if (isEditBoth() || isEditRepository()) {

            File file = new File(repository);
            if (!file.exists()) {

                MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
                messageBox.setMessage(Messages.Create_the_repository + "?\n\n" + file.getAbsolutePath()); //$NON-NLS-1$
                messageBox.setText(Messages.Repository_does_not_exist);
                int response = messageBox.open();

                if (response == SWT.YES) {
                    if (!createEmptyRepository(file)) {
                        return;
                    }
                } else if (response == SWT.CANCEL) {
                    return;
                } else {
                    run = false; // SWT.NO
                }

            }

        }

        if (run) {
            if (run() == IDialogConstants.BACK_ID) {
                return;
            }
        }

        super.okPressed();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, Messages.OK, false);
        okButton.setEnabled(false);
        createButton(parent, IDialogConstants.CANCEL_ID, Messages.Cancel, false);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getTitle());
    }

    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(500), SWT.DEFAULT, true);
    }

    public String getRepository() {
        return textRepository.getText();
    }

    public boolean isEditBoth() {
        return buttonBoth.getSelection();
    }

    public boolean isEditWorkspace() {
        return buttonWorkspace.getSelection();
    }

    public boolean isEditRepository() {
        return buttonRepository.getSelection();
    }

    protected boolean needWorkspaceArea() {
        return false;
    }

    protected void configureWorkspaceArea(Composite composite) {
    }

    protected String checkWorkspaceArea() {
        return null;
    };

    protected String checkFilterPool() {
        return null;
    }

    protected abstract String getTitle();

    protected abstract String getFileSubject();

    protected abstract String getSubject();

    protected abstract String getFileExtension();

    protected String[] getFileExtensions() {
        return new String[0];
    }

    private String[] getFileExtensionsInternal() {
        String[] fileExtensions = null;
        String fileExtension = getFileExtension();
        if (fileExtension != null) {
            fileExtensions = new String[1];
            fileExtensions[0] = fileExtension;
        } else {
            fileExtensions = getFileExtensions();
        }
        return fileExtensions;
    }

    protected abstract int run();

    protected abstract boolean createEmptyRepository(File repository);

    /**
     * Restores the screen values of the last search search.
     */
    protected String loadRepositoryPath() {

        return loadValue(REPOSITORY_PATH, FileHelper.getDefaultRootDirectory());
    }

    /**
     * Stores the screen values that are preserved for the next search.
     */
    protected void storeRepositoryPath(String path) {

        storeValue(REPOSITORY_PATH, path);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

}