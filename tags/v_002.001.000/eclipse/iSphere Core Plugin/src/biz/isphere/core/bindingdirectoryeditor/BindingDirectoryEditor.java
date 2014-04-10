/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.bindingdirectoryeditor;

import java.sql.Connection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;


public class BindingDirectoryEditor extends EditorPart {

	BindingDirectoryEditorInput input;
	
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout());
		
		BindingDirectoryEntryViewer _bindingDirectoryEntryViewer = 
			new BindingDirectoryEntryViewer(
					input.getLevel(),
					input.getAS400(), 
					input.getJDBCConnection(),
					input.getConnection(), 
					input.getLibrary(), 
					input.getBindingDirectory(), 
					input.getMode());

		_bindingDirectoryEntryViewer.createContents(container);
		
	}
	
	public void init(IEditorSite site, IEditorInput input) {
       setSite(site);
       setInput(input);
       setPartName(input.getName());
       setTitleImage(((BindingDirectoryEditorInput)input).getImage());
       this.input = (BindingDirectoryEditorInput)input;
    }
    
	public void setFocus() {
    }

	public void doSave(IProgressMonitor monitor) {
	}
	
	public void doSaveAs() {
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return false;
	}

	public static void openEditor(
			AS400 as400, 
			Connection jdbcConnection,
			String host,
			String library,
			String bindingDirectory,
			String mode
			) {
		
		try {
			
			BindingDirectoryEditorInput editorInput = 
				new BindingDirectoryEditorInput(
						"biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor",
						as400,
						jdbcConnection,
						host,
						library,
						bindingDirectory,
						mode,
						bindingDirectory + ".BNDDIR", 
						"\\\\" + host + "\\QSYS.LIB\\" + library + ".LIB\\" + bindingDirectory + ".BNDDIR", 
						ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_BINDING_DIRECTORY));
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "biz.isphere.core.bindingdirectoryeditor.BindingDirectoryEditor");
			
		} 
		catch (PartInitException e) {
		}

	}
	
 }
