/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.files.ui.resources.SystemEditableRemoteFile;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.IStreamFileEditor;
import biz.isphere.rse.connection.ConnectionManager;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileServiceSubSystem;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.systems.editor.SystemTextEditor;

public class StreamFileEditor implements IStreamFileEditor {

    public void openEditor(String connectionName, String directory, String streamFile, int statement, String mode) {

        IBMiConnection _connection = ConnectionManager.getIBMiConnection(connectionName);

        try {
            
            IRemoteFile remoteFile = null;
            
            ISubSystem[] sses = RSECorePlugin.getTheSystemRegistry().getSubSystems(_connection.getHost());
            
            for (int i = 0; i < sses.length; i++) {
                
                if ((sses[i] instanceof IFSFileServiceSubSystem)) {
                    
                    IFSFileServiceSubSystem fileServiceSubSystem = (IFSFileServiceSubSystem)sses[i];
                    
                    NullProgressMonitor monitor = new NullProgressMonitor();
                    
                    try {
                        
                        remoteFile = fileServiceSubSystem.getRemoteFileObject(directory + "/" + streamFile, monitor);
                        
                    } 
                    catch (SystemMessageException e) {
                    } 
                    catch (Exception e) {
                    }
                    
                    break;
                    
                }
                
            }
            
            if (remoteFile != null) {
                
                String editor = "com.ibm.etools.systems.editor";
                
                IEditorDescriptor _editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(editor);
                
                if (_editorDescriptor != null) {
            
                    SystemEditableRemoteFile editableRemoteFile = new SystemEditableRemoteFile(remoteFile, _editorDescriptor);

                    if (editableRemoteFile != null) {
                        
                        if (mode.equals(IStreamFileEditor.EDIT)) {
                            editableRemoteFile.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), false);
                        } else if (mode.equals(IStreamFileEditor.DISPLAY)) {
                            editableRemoteFile.open(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true);
                        }
                        
                        if (statement != 0) {
                            SystemTextEditor systemTextEditor = (SystemTextEditor)editableRemoteFile.getEditor();
                            if (systemTextEditor != null) {
                                systemTextEditor.gotoLine(statement);
                            }
                        }

                    }
                    
                }

            }

        }

        catch (Throwable e) {
            ISpherePlugin.logError("Failed to open Lpex editor.", e); //$NON-NLS-1$
        }

    }

    private boolean isOpenInEditor(SystemEditableRemoteFile editableRemoteFile) throws CoreException {
        return !(editableRemoteFile.checkOpenInEditor() == -1);
    }

    private IEditorPart findEditorPart(SystemEditableRemoteFile editableRemoteFile) {

        IFile localFileResource = editableRemoteFile.getLocalResource();
        if (localFileResource == null) {
            return null;
        }

        // See:
        // http://stackoverflow.com/questions/516704/enumerating-all-my-eclipse-editors
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference editor : page.getEditorReferences()) {
                    IEditorPart part = editor.getEditor(false);
                    if (part != null) {
                        IEditorInput input = part.getEditorInput();
                        if (input instanceof IFileEditorInput) {
                            IFileEditorInput fileInput = (IFileEditorInput)input;
                            if (localFileResource.equals(fileInput.getFile())) {
                                return part;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

}
