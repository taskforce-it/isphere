/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.jobloganalyzer.editor.JobLogAnalyzerEditor;
import biz.isphere.jobloganalyzer.editor.JobLogAnalyzerEditorInput;

public class OpenJobLogAnalyzerHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
     * ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            JobLogAnalyzerEditorInput editorInput = new JobLogAnalyzerEditorInput();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, JobLogAnalyzerEditor.ID);

        } catch (PartInitException e) {
            ISpherePlugin.logError("Failed to open job log analyzer editor", e);
        }
        return null;
    }

}
