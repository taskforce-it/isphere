/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.rsemanagement;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Size;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public abstract class AbstractEntryDialog extends Dialog {

	private Shell shell;
	private Button buttonBoth;
	private Button buttonWorkspace;
	private Button buttonRepository;
	private Composite compositeWorkspace;
	private Composite compositeRepository;
	private Text textRepository;
	private StatusLineManager statusLineManager;
	private Button okButton;
	
	public AbstractEntryDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.shell = parentShell;
	}
	
	protected Control createDialogArea(Composite parent) {
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		
		final Group groupEditingArea = new Group(container, SWT.NONE);
		groupEditingArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		groupEditingArea.setText(Messages.Editing_area);
		final GridLayout gridLayoutStatus = new GridLayout();
		gridLayoutStatus.numColumns = 1;
		groupEditingArea.setLayout(gridLayoutStatus);

		buttonBoth = new Button(groupEditingArea, SWT.RADIO);
		buttonBoth.setText(Messages.Edit_workspace_and_repository + " " + getSubject());
		buttonBoth.setSelection(true);
		buttonBoth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				compositeWorkspace.setVisible(true);
				compositeRepository.setVisible(true);
			    check();
			}
		});

		buttonWorkspace = new Button(groupEditingArea, SWT.RADIO);
		buttonWorkspace.setText(Messages.Edit_only_workspace + " " + getSubject());
		buttonWorkspace.setSelection(false);
		buttonWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				compositeWorkspace.setVisible(true);
				compositeRepository.setVisible(false);
			    check();
			}
		});

		buttonRepository = new Button(groupEditingArea, SWT.RADIO);
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
		
		textRepository = new Text(compositeRepository, SWT.BORDER);
		textRepository.setText("");
		textRepository.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textRepository.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
			    check();
			}
		});
		
		Button browseRepository = new Button(compositeRepository, SWT.NONE);
		browseRepository.setText(Messages.Browse);
		browseRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(shell, SWT.OK);
                dialog.setFilterNames(new String[] {getRSESubject()});
                dialog.setFilterExtensions(new String[] { "*." + getFileExtension()});
                dialog.setFilterPath("C:\\");
                // dialog.setFileName("export.xls");
                // dialog.setOverwrite(true);
                String file = dialog.open();
                if (file != null) {
                	textRepository.setText(file);
                }
			}
		});
		
		statusLineManager = new StatusLineManager(); 
        statusLineManager.createControl(container, SWT.NONE);
        Control statusLine = statusLineManager.getControl();        
		final GridData gridDataStatusLine = new GridData(SWT.FILL, SWT.CENTER, true, false);
		statusLine.setLayoutData(gridDataStatusLine);

		check();
		
		return container;
		
	}

	private void check() {

		if (needWorkspaceArea() && (buttonBoth.getSelection() || buttonWorkspace.getSelection())) {
			
			String error = checkWorkspaceArea();
			if (error != null) {
				if (okButton != null) okButton.setEnabled(false);
			    setErrorMessage(error);
				return;
			}
			
		}
		
		if (buttonBoth.getSelection() || buttonRepository.getSelection()) {

			String fileName = textRepository.getText().trim();
			if (fileName.equals("")) {
				if (okButton != null) okButton.setEnabled(false);
			    setErrorMessage(Messages.Enter_a_file_name + ".");
			    textRepository.setFocus();
				return;
			}

			File repository = new File(fileName);
		    try {
		    	repository.getCanonicalPath();
		    }
		    catch (IOException e) {
		    	if (okButton != null) okButton.setEnabled(false);
			    setErrorMessage(Messages.Invalid_file_name + ".");
			    textRepository.setFocus();
				return;
		    }

			if (!fileName.endsWith("." + getFileExtension())) {
				if (okButton != null) okButton.setEnabled(false);
			    setErrorMessage(Messages.File_name_does_not_end_with + " ." + getFileExtension());
			    textRepository.setFocus();
				return;
			}

		}
		
		if (okButton != null) okButton.setEnabled(true);
	    setErrorMessage(null);
	    
	}

	private void setErrorMessage(String errorMessage) {
		if (errorMessage != null) {
			statusLineManager.setErrorMessage(ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_ERROR), errorMessage);
		}
		else {
			statusLineManager.setErrorMessage(null, null);
		}
	}

	protected void okPressed() {

		boolean run = true;

		if (isEditBoth() || isEditRepository()) {
			
			String repository = getRepository();
			
			File file = new File(repository);
			if (!file.exists()) {

				MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				messageBox.setMessage(Messages.Create_the_repository + "?");
				messageBox.setText(Messages.Repository_does_not_exist);
				int response = messageBox.open();

				if (response == SWT.YES) {
					if (!createEmptyRepository(file)) {
						run = false;
					}
				}
				else {
					run = false;
				}

			}
			
		}
		
		if (run) {
			run();
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
	
	protected Point getInitialSize() {
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

	protected void configureWorkspaceArea(Composite composite) {}

	protected String checkWorkspaceArea() {
		return null;
	};
	
	protected abstract String getTitle();

	protected abstract String getRSESubject();

	protected abstract String getSubject();
	
	protected abstract String getFileExtension();

	protected abstract void run();
	
	protected abstract boolean createEmptyRepository(File repository);
	
}