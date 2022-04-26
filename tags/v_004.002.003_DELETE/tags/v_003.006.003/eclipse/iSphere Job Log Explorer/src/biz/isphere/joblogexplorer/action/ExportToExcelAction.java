/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;

import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.export.ExcelExporter;
import biz.isphere.joblogexplorer.model.JobLogMessage;

public class ExportToExcelAction extends Action {

    public static final String ID = "biz.isphere.joblogexplorer.action.ExportToExcelAction"; //$NON-NLS-1$
    private static final String IMAGE = ISphereJobLogExplorerPlugin.EXCEL;

    private String title;
    private JobLogMessage[] selectedItems;

    public ExportToExcelAction() {
        super(Messages.Exort_to_Excel, Action.AS_PUSH_BUTTON);
        setToolTipText(Messages.Exort_to_Excel);
        setImageDescriptor(ISphereJobLogExplorerPlugin.getDefault().getImageDescriptor(IMAGE));
        setId(ID);
    }

    public Image getImage() {
        return ISphereJobLogExplorerPlugin.getDefault().getImage(IMAGE);
    }

    @Override
    public void run() {

        if (selectedItems == null || selectedItems.length == 0) {
            return;
        }

        String sheetName = title.replaceAll("/", "_").replaceAll(":", "_");
        String suggestedFileName = sheetName + ".xls";

        ExcelExporter exporter = new ExcelExporter();
        exporter.exportToExcel(sheetName, suggestedFileName, selectedItems);
    }

    public void setSelectedItems(StructuredSelection selection) {

        if (selection == null) {
            this.selectedItems = null;
            return;
        }

        List<JobLogMessage> selectedItems = new ArrayList<JobLogMessage>();
        for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (object instanceof JobLogMessage) {
                JobLogMessage journalEntry = (JobLogMessage)object;
                selectedItems.add(journalEntry);
            }
        }

        this.selectedItems = selectedItems.toArray(new JobLogMessage[selectedItems.size()]);
    }

    public void setTabTitle(String title) {
        this.title = title;
    }
}
