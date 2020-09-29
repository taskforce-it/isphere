/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.export.json;

import java.io.File;
import java.io.FileReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.journalexplorer.core.model.JournalEntries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class JsonImporter {

    public JsonImporter() {
    }

    public JournalEntries execute(Shell shell, String file) {

        if (file == null) {
            IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.SAVE);
            dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$
            dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$
            dialog.setFilterPath(FileHelper.getDefaultRootDirectory());
            dialog.setFileName("export.json"); //$NON-NLS-1$
            dialog.setOverwrite(true);
            file = dialog.open();
        }

        if (file == null) {
            return null;
        }

        System.out.println(new File(file).getPath());

        return performImportFromJson(file);
    }

    private JournalEntries performImportFromJson(String file) {

        JsonSerializer<java.sql.Date> sqlDateSerializer = new SQLDateSerializer();

        JsonSerializer<java.sql.Time> sqlTimeSerializer = new SQLTimeSerializer();

        JsonSerializer<java.sql.Timestamp> sqlTimestampSerializer = new SQLTimestampSerializer();

        GsonBuilder gsonBuilder = new GsonBuilder();
        // gsonBuilder.setDateFormat("yyyy-MM-dd-hh.mm.ss.S");
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, sqlDateSerializer);
        gsonBuilder.registerTypeAdapter(java.sql.Time.class, sqlTimeSerializer);
        gsonBuilder.registerTypeAdapter(java.sql.Timestamp.class, sqlTimestampSerializer);
        // gsonBuilder.registerTypeAdapter(java.sql.Date.class, sqlDes);
        // gsonBuilder.setPrettyPrinting();

        try {
            Gson gson = gsonBuilder.create();
            FileReader reader = new FileReader(file);
            JournalEntries journalEntries = gson.fromJson(reader, JournalEntries.class);
            reader.close();
            return journalEntries;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
