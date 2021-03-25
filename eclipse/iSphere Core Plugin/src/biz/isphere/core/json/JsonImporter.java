/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.json;

import java.io.File;
import java.io.FileReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

public class JsonImporter<M extends JsonSerializable> {

    private Class<M> type;

    public JsonImporter(Class<M> type) {
        this.type = type;
    }

    public M execute(Shell shell, String jsonFile) {
        return execute(shell, new File(jsonFile));
    }

    public M execute(Shell shell, File jsonFile) {

        if (jsonFile == null && shell != null) {
            IFileDialog dialog = WidgetFactory.getFileDialog(shell, SWT.SAVE);
            dialog.setFilterNames(new String[] { "Json Files", FileHelper.getAllFilesText() }); //$NON-NLS-1$
            dialog.setFilterExtensions(new String[] { "*.json", FileHelper.getAllFilesFilter() }); //$NON-NLS-1$
            dialog.setFilterPath(FileHelper.getDefaultRootDirectory());
            dialog.setFileName("export.json"); //$NON-NLS-1$
            dialog.setOverwrite(true);
            String fileName = dialog.open();
            if (!StringHelper.isNullOrEmpty(fileName)) {
                jsonFile = new File(fileName);
            }
        }

        if (jsonFile == null) {
            return null;
        }

        return performImportFromJson(jsonFile);
    }

    private M performImportFromJson(File jsonFile) {

        JsonSerializer<java.sql.Date> sqlDateSerializer = new SQLDateSerializer();

        JsonSerializer<java.sql.Time> sqlTimeSerializer = new SQLTimeSerializer();

        JsonSerializer<java.sql.Timestamp> sqlTimestampSerializer = new SQLTimestampSerializer();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(java.sql.Date.class, sqlDateSerializer);
        gsonBuilder.registerTypeAdapter(java.sql.Time.class, sqlTimeSerializer);
        gsonBuilder.registerTypeAdapter(java.sql.Timestamp.class, sqlTimestampSerializer);

        try {
            Gson gson = gsonBuilder.create();
            FileReader reader = new FileReader(jsonFile);
            M journalEntries = gson.fromJson(reader, getClazz());
            reader.close();
            return journalEntries;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<M> getClazz() {
        return this.type;
    }
}
