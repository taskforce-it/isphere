/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilesearch;

import java.io.File;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.search.GenericSearchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.swt.widgets.extension.handler.WidgetFactoryContributionsHandler;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class MessageFilesToExcelExporter {

    private static final int COLUMN_WIDTH_NAME = 15;
    private static final int COLUMN_WIDTH_DESCRIPTION = 50;
    private static final int COLUMN_WIDTH_MESSAGE_ID = 12;
    private static final int COLUMN_WIDTH_MESSAGE_TEXT = 100;

    private static final int COLUMN_SEARCH_ARGUMENTS_0 = 20;
    private static final int COLUMN_SEARCH_ARGUMENTS_1 = 80;

    private Shell shell;
    private SearchOptions searchOptions;
    private SearchResult[] searchResults;

    public MessageFilesToExcelExporter(Shell shell, SearchOptions searchOptions, SearchResult[] searchResults) {

        this.shell = shell;
        this.searchOptions = searchOptions;
        this.searchResults = searchResults;
    }

    public void export() {

        WidgetFactoryContributionsHandler factory = new WidgetFactoryContributionsHandler();
        IFileDialog dialog = factory.getFileDialog(shell, SWT.SAVE);

        dialog.setFilterNames(new String[] { "Excel Files", "All Files" }); //$NON-NLS-1$
        dialog.setFilterExtensions(new String[] { "*.xls", "*.*" }); //$NON-NLS-1$
        dialog.setFilterPath(Preferences.getInstance().getMessageFileSearchExportDirectory());
        dialog.setFileName("export.xls"); //$NON-NLS-1$
        dialog.setOverwrite(true);
        String file = dialog.open();

        if (file != null) {

            Preferences.getInstance().setMessageFileSearchExportDirectory(dialog.getFilterPath());

            try {

                WritableWorkbook workbook = Workbook.createWorkbook(new File(file));

                WritableSheet sheet;

                // Add search options
                sheet = workbook.createSheet("Search arguments", 0);

                sheet.addCell(new jxl.write.Label(0, 0, Messages.Conditions_to_match_colon));
                sheet.addCell(new jxl.write.Label(1, 0, searchOptions.getMatchOption()));

                sheet.addCell(new jxl.write.Label(0, 1, Messages.Show_all_matches_colon));
                sheet.addCell(new jxl.write.Boolean(1, 1, searchOptions.isShowAllItems()));

                sheet.addCell(new jxl.write.Label(0, 2, Messages.Search_arguments_colon));

                int line = sheet.getRows() - 1;

                for (SearchArgument searchArgument : searchOptions.getSearchArguments()) {
                    sheet.addCell(new jxl.write.Label(1, line, searchArgument.toText()));
                    line++;
                }

                if (searchOptions.hasGenericOptions()) {
                    sheet.addCell(new jxl.write.Label(0, line, Messages.Additional_Options_colon));
                    for (GenericSearchOption genericOption : searchOptions.getGenericOptions()) {
                        sheet.addCell(new jxl.write.Label(1, line, genericOption.toText()));
                    }
                }

                sheet.setColumnView(0, COLUMN_SEARCH_ARGUMENTS_0);
                sheet.setColumnView(1, COLUMN_SEARCH_ARGUMENTS_1);

                // Add message files with id's
                sheet = workbook.createSheet(Messages.Files_with_Id_s, 0);

                sheet.addCell(new jxl.write.Label(0, 0, Messages.Library));
                sheet.addCell(new jxl.write.Label(1, 0, Messages.Message_file));
                sheet.addCell(new jxl.write.Label(2, 0, Messages.Description));
                sheet.addCell(new jxl.write.Label(3, 0, Messages.Message_Id));
                sheet.addCell(new jxl.write.Label(4, 0, Messages.Message));

                sheet.setColumnView(0, COLUMN_WIDTH_NAME);
                sheet.setColumnView(1, COLUMN_WIDTH_NAME);
                sheet.setColumnView(2, COLUMN_WIDTH_DESCRIPTION);
                sheet.setColumnView(3, COLUMN_WIDTH_MESSAGE_ID);
                sheet.setColumnView(4, COLUMN_WIDTH_MESSAGE_TEXT);

                line = 1;

                for (int index1 = 0; index1 < searchResults.length; index1++) {

                    SearchResultMessageId[] _messageIds = searchResults[index1].getMessageIds();

                    for (int index2 = 0; index2 < _messageIds.length; index2++) {

                        sheet.addCell(new jxl.write.Label(0, line, searchResults[index1].getLibrary()));
                        sheet.addCell(new jxl.write.Label(1, line, searchResults[index1].getMessageFile()));
                        sheet.addCell(new jxl.write.Label(2, line, searchResults[index1].getDescription()));
                        sheet.addCell(new jxl.write.Label(3, line, _messageIds[index2].getMessageId()));
                        sheet.addCell(new jxl.write.Label(4, line, _messageIds[index2].getMessage()));

                        line++;

                    }

                    line++;
                }

                // Add message files
                sheet = workbook.createSheet(Messages.Files, 0);

                sheet.addCell(new jxl.write.Label(0, 0, Messages.Library));
                sheet.addCell(new jxl.write.Label(1, 0, Messages.Message_file));
                sheet.addCell(new jxl.write.Label(2, 0, Messages.Description));

                sheet.setColumnView(0, COLUMN_WIDTH_NAME);
                sheet.setColumnView(1, COLUMN_WIDTH_NAME);
                sheet.setColumnView(2, COLUMN_WIDTH_DESCRIPTION);

                for (int index = 0; index < searchResults.length; index++) {
                    sheet.addCell(new jxl.write.Label(0, index + 1, searchResults[index].getLibrary()));
                    sheet.addCell(new jxl.write.Label(1, index + 1, searchResults[index].getMessageFile()));
                    sheet.addCell(new jxl.write.Label(2, index + 1, searchResults[index].getDescription()));
                }

                workbook.write();
                workbook.close();

            } catch (Exception e) {
                MessageDialogAsync.displayError(shell, e.getLocalizedMessage());
            }

        }

    }

}
