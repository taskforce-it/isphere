/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

import biz.isphere.antcontrib.internal.FileHelper;

/**
 * This class defines the WdSaveFormat enumeration of Microsoft Word.
 * 
 * @see <a href="http://office.microsoft.com/en-us/word-help/HV080607264.aspx"
 *      >WdSaveFormat Enumeration</a>
 * @author Thomas Raddatz
 */
public enum WdSaveFormat {

    /**
     * Microsoft Word 97 document format.
     */
    DOCUMENT (0, "doc"),

    /**
     * Word template format.
     */
    TEMPLATE (1, "dot"),

    /**
     * Microsoft Windows text format.
     */
    TEXT (2, "txt"),

    /**
     * Windows text format with line breaks preserved.
     */
    TEXT_LINE_BREAKS (3, "txt"),

    /**
     * Microsoft DOS text format.
     */
    DOS_TEXT (4, "txt"),

    /**
     * Microsoft DOS text with line breaks preserved.
     */
    DOS_TEXT_LINE_BREAKS (5, "txt"),

    /**
     * Rich text format (RTF).
     */
    RTF (6, "rtf"),

    /**
     * Encoded text format.
     */
    ENCODED_TEXT (7, "txt"),

    /**
     * Standard HTML format.
     */
    HTML (8, "html"),

    /**
     * Web archive format.
     */
    WEB_ARCHIVE (9, "mht"),

    /**
     * Filtered HTML format.
     */
    FILTERED_HTML (10, "html"),

    /**
     * Extensible Markup Language (XML) format.
     */
    XML (11, "xml"),

    /**
     * XML document format.
     */
    XML_DOCUMENT (12, "xml"),

    /**
     * XML document format with macros enabled.
     */
    XML_DOCUMENT_MACRO_ENABLED (13, "xml"),

    /**
     * XML template format.
     */
    XML_TEMPLATE (14, "xml"),

    /**
     * XML template format with macros enabled.
     */
    XML_TEMPLATE_MACRO_ENABLED (15, "xml"),

    /**
     * Word default document file format. For Microsoft Office Word 2007; this
     * is the DOCX format.
     */
    DOCUMENT_DEFAULT (16, "docx"),

    /**
     * PDF format.
     */
    PDF (17, "pdf"),

    /**
     * XPS format.
     */
    XPS (18, "xps");

    private final int value;
    private final String fileExtension;

    private WdSaveFormat(int value, String defaultFileExtension) {
        this.value = value;
        this.fileExtension = defaultFileExtension;
    }

    public int getValue() {
        return value;
    }

    public String getFileExtension() {
        return fileExtension;
    }
    
    public static WdSaveFormat getDefaultSaveFormat(String fileName) {
        
        String extension = FileHelper.getFileExtension(fileName);
        
        if ("doc".equalsIgnoreCase(extension)) {
            return WdSaveFormat.DOCUMENT;
        } else if ("docx".equalsIgnoreCase(extension)) {
            return WdSaveFormat.DOCUMENT_DEFAULT;
        } else if ("pdf".equalsIgnoreCase(extension)) {
            return WdSaveFormat.PDF;
        } else if ("html".equalsIgnoreCase(extension)) {
            return WdSaveFormat.HTML;
        } else if ("mht".equalsIgnoreCase(extension)) {
            return WdSaveFormat.WEB_ARCHIVE;
        } else if ("xml".equalsIgnoreCase(extension)) {
            return WdSaveFormat.XML;
        } else {
            return null;
        }
    }
}