/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action.rse;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.editor.JobLogExplorerEditor;
import biz.isphere.joblogexplorer.export.ExcelExporter;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class ExportToExcelAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.rse.ExportToExcelAction.ExportToExcelAction()"; //$NON-NLS-1$

    private JobLogExplorerEditor editor;

    public ExportToExcelAction() {

        super(Messages.Exort_to_Excel, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Exort_to_Excel);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageDescriptor(ISphereJobLogExplorerPlugin.EXCEL));
        setId(ID);
    }

    @Override
    public void runWithEvent(Event event) {

        JobLogMessage[] messages = editor.getItems();
        if (messages == null || messages.length == 0) {
            return;
        }

        String partName = editor.getPartName();
        String sheetName = partName.replaceAll("/", "_").replaceAll(":", "_");
        String suggestedFileName = sheetName + ".xls";

        ExcelExporter exporter = new ExcelExporter();
        exporter.exportToExcel(sheetName, suggestedFileName, messages);
    }

    public void setActiveEditor(JobLogExplorerEditor editor) {
        this.editor = editor;
    }

}
