/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.export;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.ibm.as400.access.Record;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.internals.JoesdParser;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.MetaColumn;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;
import biz.isphere.journalexplorer.core.ui.model.JournalEntryColumn;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporter {

    public String export(Shell shell, JournalEntryColumn[] columns, JournalEntry[] journalEntries, String file) throws Exception {

        if (file == null) {
            IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.SAVE);
            dialog.setFilterNames(new String[] { "Excel Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$ //$NON-NLS-2$
            dialog.setFilterExtensions(new String[] { "*.xls", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$ //$NON-NLS-2$
            dialog.setFilterPath(FileHelper.getDefaultRootDirectory());
            dialog.setFileName("export.xls"); //$NON-NLS-1$
            dialog.setOverwrite(true);
            file = dialog.open();
        }

        if (file == null) {
            return null;
        }

        return performExportToExcel(columns, journalEntries, file);
    }

    private String performExportToExcel(JournalEntryColumn[] columns, JournalEntry[] journalEntries, String file) throws Exception {

        WritableWorkbook workbook = Workbook.createWorkbook(new File(file));

        WritableSheet sheet;

        sheet = workbook.createSheet(Messages.ExcelExport_Headline, 0);

        int line = 0;
        int col = 0;

        // Read meta data of journaled file for displaying the column headings
        MetaTable metaData = null;
        if (Preferences.getInstance().isExportColumnHeadings() && displayColumnHeadings(journalEntries)) {
            metaData = MetaDataCache.INSTANCE.retrieveMetaData(journalEntries[0]);
        }

        // Add headline
        for (JournalEntryColumn column : columns) {
            if (!ColumnsDAO.JOESD.equals(column.getName())) {
                sheet.addCell(new jxl.write.Label(col, line, column.getColumnHeading()));
                col++;
            } else {
                if (metaData != null && !metaData.hasColumns()) {
                    sheet.addCell(new jxl.write.Label(col, line, column.getColumnHeading()));
                    col++;
                }
            }
        }

        if (metaData != null) {
            MetaColumn[] journaledObjectColumns = metaData.getColumns();
            for (MetaColumn metaColumn : journaledObjectColumns) {
                sheet.addCell(new jxl.write.Label(col, line, metaColumn.getName()));
                col++;
            }
        }

        line++;

        // Add data
        for (JournalEntry journalEntry : journalEntries) {

            MetaTable metatable = MetaDataCache.INSTANCE.retrieveMetaData(journalEntry);
            Record parsedJOESD = new JoesdParser(metatable).execute(journalEntry);

            col = 0;
            for (JournalEntryColumn column : columns) {
                if (!ColumnsDAO.JOESD.equals(column.getName())) {
                    sheet.addCell(new jxl.write.Label(col, line, journalEntry.getValueForUi(column.getName())));
                    col++;
                } else {
                    if (metatable != null && !metatable.hasColumns()) {
                        sheet.addCell(new jxl.write.Label(col, line, journalEntry.getValueForUi(column.getName())));
                        col++;
                    }
                }
            }

            for (MetaColumn column : metatable.getColumns()) {
                JOESDProperty property = new JOESDProperty("", "", null, journalEntry);
                property.executeParsing();
                property.toPropertyArray();
                if (journalEntry.isRecordEntryType()) {
                    sheet.addCell(new jxl.write.Label(col, line, parsedJOESD.getField(column.getName()).toString()));
                } else {
                    sheet.addCell(new jxl.write.Label(col, line, Messages.Error_No_record_level_operation));
                    break;
                }
                col++;
            }

            line++;
        }

        workbook.write();
        workbook.close();

        return file;
    }

    private boolean displayColumnHeadings(JournalEntry[] journalEntries) {

        boolean allSameFile = false;
        boolean hasRecordEntries = false;

        String previousObject = null;

        for (JournalEntry journalEntry : journalEntries) {
            if (journalEntry.isRecordEntryType()) {
                hasRecordEntries = true;
                if (previousObject == null) {
                    allSameFile = true;
                    previousObject = journalEntry.getQualifiedObjectName();
                } else {
                    if (!previousObject.equals(journalEntry.getQualifiedObjectName())) {
                        allSameFile = false;
                    }
                }
            }
        }

        return hasRecordEntries && allSameFile;
    }
}
