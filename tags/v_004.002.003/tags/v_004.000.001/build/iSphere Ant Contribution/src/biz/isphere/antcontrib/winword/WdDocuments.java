/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

import biz.isphere.antcontrib.logger.Logger;

import com.jacob.com.Dispatch;

/**
 * This class represents the <i>Documents</i> object of the Word Object Model.
 * 
 * @author Thomas Raddatz
 * @see <a href="http://msdn.microsoft.com/en-us/library/kw65a0we.aspx" >Word
 *      Object Model Overview</a>
 */
public class WdDocuments {

    public static final String ID = "documents";

    // Properties
    public static final String COUNT = "Count";
    public static final String ITEM = "Item";

    // Methods
    public static final String CLOSE = "Close";
    public static final String OPEN = "Open";

    // Private attributes
    private Dispatch documents;

    WdDocuments(Dispatch documents) {
        this.documents = documents;
    }

    public WdDocument open(String fileName) {
        Logger.logInfo("Opening document: " + fileName);
        Dispatch document = Dispatch.call(documents, OPEN, fileName).toDispatch();
        WdDocument wdDocument = new WdDocument(document);
        return wdDocument;
    }

    void close() {

        Logger.logInfo("Closing documents: ");
        int count = Dispatch.get(documents, COUNT).getInt();
        for (int i = 1; i <= count; i++) {
            Dispatch document = Dispatch.call(documents, ITEM, new Integer(i)).toDispatch();
            WdDocument wdDocument = new WdDocument(document);
            wdDocument.close(WdSaveOptions.PROMPT_TO_SAVE_CHANGES);
        }
    }
}
