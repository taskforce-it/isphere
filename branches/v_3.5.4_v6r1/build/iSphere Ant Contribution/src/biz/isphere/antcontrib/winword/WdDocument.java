/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

import java.io.File;

import biz.isphere.antcontrib.internal.FileHelper;
import biz.isphere.antcontrib.logger.Logger;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * This class represents the <i>Document</i> object of the Word Object Model.
 * 
 * @author Thomas Raddatz
 * @see <a href="http://msdn.microsoft.com/en-us/library/kw65a0we.aspx" >Word
 *      Object Model Overview</a>
 */
public class WdDocument {

    public static final String ID = "document";

    // Properties
    public static final String APPLICATION = "Application";
    public static final String FULL_NAME = "FullName";
    public static final String NAME = "Name";
    public static final String PATH = "Path";

    // Methods
    public static final String CLOSE = "Close";
    public static final String SAVE = "Save";
    public static final String SAVE_AS = "SaveAs";

    // Private attributes
    private Dispatch document;

    WdDocument(Dispatch document) {
        this.document = document;
    }

    public String getFullName() {
        return Dispatch.get(document, FULL_NAME).toString();
    }

    public String getName() {
        return Dispatch.get(document, NAME).toString();
    }

    public String getPath() {
        return Dispatch.get(document, PATH).toString();
    }

    public void save() {
        Logger.logInfo("Saving document: " + getFullName());
        Dispatch.call(document, SAVE);
    }

    public void saveAs(String fileName, WdSaveFormat format) {

        String bareFileName = FileHelper.getBareFileName(fileName);
        String extension = FileHelper.getFileExtension(fileName);
        if (extension == null) {
            extension = format.getFileExtension();
        } else if (!extension.equalsIgnoreCase(format.getFileExtension())) {
            bareFileName = bareFileName + "." + extension;
            extension = format.getFileExtension();
        }

        File outFile = new File(bareFileName + "." + extension);
        if (outFile.getParent() == null) {
            String path = getPath();
            outFile = new File(path + File.separator + outFile);
        }

        Logger.logInfo("Saving as: " + fileName + "(" + format + ")");

        Variant filePath = new Variant(outFile.getPath());
        Variant fileFormat = new Variant(format.getValue());
        // Variant lockComments = new Variant(null, false);
        // Variant password = new Variant("");
        // Variant addToRecentFiles = new Variant(null, false);
        // Variant writePassword = new Variant("");
        // Variant readOnlyRecommended = new Variant(null, false);
        // Variant embedTrueTypeFonts = new Variant(null, false);
        // Variant saveNativePictureFormat = new Variant(null, false);
        // Variant saveFormsData = new Variant(false);
        // Variant saveAsAOCELetter = new Variant(null, false);
        // Variant encoding = new Variant(MsoEncoding.WESTERN.getValue()); //
        // 20127
        // Variant insertLineBreaks = new Variant(Boolean.FALSE);
        // Variant allowSubstitutions = new Variant(Boolean.FALSE);
        // Variant lineEnding = new Variant(WdLineEnding.CRLF.getValue());
        // Variant addBiDiMarks = new Variant(null, false);
        //
        // Dispatch.call(document, SAVE_AS, filePath, fileFormat, lockComments,
        // password, addToRecentFiles, writePassword, readOnlyRecommended,
        // embedTrueTypeFonts, saveNativePictureFormat, saveFormsData,
        // saveAsAOCELetter, encoding, insertLineBreaks, allowSubstitutions,
        // lineEnding,
        // addBiDiMarks);

        Dispatch.call(document, SAVE_AS, filePath, fileFormat);
    }

    public void close(WdSaveOptions saveOption) {
        Object[] args = { new Integer(saveOption.getValue()) };
        Logger.logInfo("Closing document: " + getFullName());
        Dispatch.call(document, "Close", args);
    }
}
