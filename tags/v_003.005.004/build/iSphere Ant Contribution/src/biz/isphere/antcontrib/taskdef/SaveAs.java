/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import org.apache.tools.ant.BuildException;

import biz.isphere.antcontrib.winword.WdSaveFormat;

public class SaveAs {

    private static final String DOC = "doc";
    private static final String DOCX = "docx";
    private static final String DOCM = "docm";
    private static final String DOTX = "dotx";
    private static final String DOTM = "dotm";
    private static final String PDF = "pdf";
    private static final String MHT = "mht";
    private static final String HTM = "htm";
    private static final String RTF = "rtf";
    // private static final String TXT = "txt";

    private String toFile;
    private WdSaveFormat saveFormat;

    public SaveAs() {
    }

    public String getToFile() {
        return toFile;
    }

    public void setToFile(String toFile) {
        this.toFile = toFile;
        if (saveFormat == null) {
            this.saveFormat = WdSaveFormat.getDefaultSaveFormat(toFile);
        }
    }

    public WdSaveFormat getSaveFormat() {
        return saveFormat;
    }

    public void setSaveFormat(String format) {

        String lFormat = format.toLowerCase();
        if (DOC.equals(lFormat)) {
            saveFormat = WdSaveFormat.DOCUMENT;
        } else if (DOCX.equals(lFormat)) {
            saveFormat = WdSaveFormat.XML_DOCUMENT;
        } else if (DOCM.equals(lFormat)) {
            saveFormat = WdSaveFormat.XML_DOCUMENT_MACRO_ENABLED;
        } else if (DOTX.equals(lFormat)) {
            saveFormat = WdSaveFormat.XML_TEMPLATE;
        } else if (DOTM.equals(lFormat)) {
            saveFormat = WdSaveFormat.XML_TEMPLATE_MACRO_ENABLED;
        } else if (PDF.equals(lFormat)) {
            saveFormat = WdSaveFormat.PDF;
        } else if (MHT.equals(lFormat)) {
            saveFormat = WdSaveFormat.WEB_ARCHIVE;
        } else if (HTM.equals(lFormat)) {
            saveFormat = WdSaveFormat.HTML;
        } else if (RTF.equals(lFormat)) {
            saveFormat = WdSaveFormat.RTF;
            // } else if (TXT.equals(lFormat)) {
            // saveFormat = WdSaveFormat.TEXT;
        } else {
            throw new BuildException("Invalid attribute 'saveFormat': " + format);
        }
    }
}
