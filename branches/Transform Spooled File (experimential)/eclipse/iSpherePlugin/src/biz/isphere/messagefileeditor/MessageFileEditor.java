/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagefileeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.ISpherePlugin;

import com.ibm.as400.access.AS400;


public class MessageFileEditor extends EditorPart {

	MessageFileEditorInput input;
	
	public void createPartControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FillLayout());
	
		MessageDescriptionViewer _messageDescriptionViewer = 
			new MessageDescriptionViewer(
					input.getAS400(), 
					input.getConnection(), 
					input.getLibrary(), 
					input.getMessageFile(), 
					input.getMode());

		_messageDescriptionViewer.createContents(container);
		
	}
	
	public void init(IEditorSite site, IEditorInput input) {
       setSite(site);
       setInput(input);
       setPartName(input.getName());
       setTitleImage(((MessageFileEditorInput)input).getImage());
       this.input = (MessageFileEditorInput)input;
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
			String host,
			String library,
			String messageFile,
			String mode) {
		
		try {
			 
			MessageFileEditorInput editorInput = 
				new MessageFileEditorInput(
						"biz.isphere.messagefileeditor.MessageFileEditor",
						as400,
						host,
						library,
						messageFile,
						mode,
						messageFile + ".MSGF", 
						"\\\\" + host + "\\QSYS.LIB\\" + library + ".LIB\\" + messageFile + ".MSGF", 
						ISpherePlugin.getDefault().getImageRegistry().get(ISpherePlugin.IMAGE_MESSAGE_FILE));
			
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, "biz.isphere.messagefileeditor.MessageFileEditor");
		
		} 
		catch (PartInitException e) {
		}
	
	}
	
 }
