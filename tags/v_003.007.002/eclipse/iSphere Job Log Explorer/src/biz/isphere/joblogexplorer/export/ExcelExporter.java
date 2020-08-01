/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.export;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.model.JobLogMessage;
import biz.isphere.joblogexplorer.preferences.Preferences;

public class ExcelExporter {

    public void exportToExcel(String sheetName, String suggestedFileName, JobLogMessage[] jobLogMessages) {

        String exportFolder = Preferences.getInstance().getExportFolder();

        IFileDialog dialog = WidgetFactory.getFileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
        dialog.setFilterNames(new String[] { Messages.SaveDialog_Excel_Workbook, Messages.SaveDialog_All_Files });
        dialog.setFilterExtensions(new String[] { "*.xls", "*.*" });
        dialog.setFilterPath(FileHelper.getDefaultRootDirectory());
        dialog.setFileName(suggestedFileName);
        dialog.setFilterPath(exportFolder);
        dialog.setOverwrite(true);
        String fileName = dialog.open();

        if (fileName == null) {
            return;
        }

        Preferences.getInstance().setExportFolder(dialog.getFilterPath());

        File file = new File(fileName);
        Display display = PlatformUI.getWorkbench().getDisplay();

        BusyIndicator.showWhile(display, new ExportRunnable(file, jobLogMessages, sheetName));
    }

    private class ExportRunnable implements Runnable {

        private File file;
        private JobLogMessage[] jobLogMessages;
        private String sheetName;

        public ExportRunnable(File file, JobLogMessage[] jobLogMessages, String sheetName) {
            this.file = file;
            this.jobLogMessages = jobLogMessages;
            this.sheetName = sheetName;
        }

        public void run() {

            try {

                WritableWorkbook workbook = Workbook.createWorkbook(file);

                WritableSheet sheet;

                sheet = workbook.createSheet(sheetName, 0);

                sheet.addCell(new jxl.write.Label(0, 0, Messages.Column_Date_sent));
                sheet.addCell(new jxl.write.Label(1, 0, Messages.Column_Time_sent));
                sheet.addCell(new jxl.write.Label(2, 0, Messages.Column_ID));
                sheet.addCell(new jxl.write.Label(3, 0, Messages.Column_Type));
                sheet.addCell(new jxl.write.Label(4, 0, Messages.Column_Severity));
                sheet.addCell(new jxl.write.Label(5, 0, Messages.Column_Text));
                sheet.addCell(new jxl.write.Label(6, 0, Messages.Column_From_Library));
                sheet.addCell(new jxl.write.Label(7, 0, Messages.Column_From_Program));
                sheet.addCell(new jxl.write.Label(8, 0, Messages.Column_From_Stmt));
                sheet.addCell(new jxl.write.Label(9, 0, Messages.Column_To_Library));
                sheet.addCell(new jxl.write.Label(10, 0, Messages.Column_To_Program));
                sheet.addCell(new jxl.write.Label(11, 0, Messages.Column_To_Stmt));
                sheet.addCell(new jxl.write.Label(12, 0, Messages.Column_From_Module));
                sheet.addCell(new jxl.write.Label(13, 0, Messages.Column_To_Module));
                sheet.addCell(new jxl.write.Label(14, 0, Messages.Column_From_Procedure));
                sheet.addCell(new jxl.write.Label(15, 0, Messages.Column_To_Procedure));

                int line = 1;
                for (JobLogMessage jobLogMessage : jobLogMessages) {

                    sheet.addCell(new jxl.write.Label(0, line, jobLogMessage.getDate()));
                    sheet.addCell(new jxl.write.Label(1, line, jobLogMessage.getTime()));
                    sheet.addCell(new jxl.write.Label(2, line, jobLogMessage.getId()));
                    sheet.addCell(new jxl.write.Label(3, line, jobLogMessage.getType()));
                    sheet.addCell(new jxl.write.Label(4, line, jobLogMessage.getSeverity()));
                    sheet.addCell(new jxl.write.Label(5, line, jobLogMessage.getText()));
                    sheet.addCell(new jxl.write.Label(6, line, jobLogMessage.getFromLibrary()));
                    sheet.addCell(new jxl.write.Label(7, line, jobLogMessage.getFromProgram()));
                    sheet.addCell(new jxl.write.Label(8, line, jobLogMessage.getFromStatement()));
                    sheet.addCell(new jxl.write.Label(9, line, jobLogMessage.getToLibrary()));
                    sheet.addCell(new jxl.write.Label(10, line, jobLogMessage.getToProgram()));
                    sheet.addCell(new jxl.write.Label(11, line, jobLogMessage.getToStatement()));
                    sheet.addCell(new jxl.write.Label(12, line, jobLogMessage.getFromModule()));
                    sheet.addCell(new jxl.write.Label(13, line, jobLogMessage.getToModule()));
                    sheet.addCell(new jxl.write.Label(14, line, jobLogMessage.getFromProcedure()));
                    sheet.addCell(new jxl.write.Label(15, line, jobLogMessage.getToProcedure()));

                    line++;
                }

                workbook.write();
                workbook.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }

    }

}
