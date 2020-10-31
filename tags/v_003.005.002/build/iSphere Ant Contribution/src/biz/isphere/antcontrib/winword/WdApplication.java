/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * This class represents the <i>Application</i> object of the Word Object Model.
 * 
 * @author Thomas Raddatz
 * @see <a href="http://msdn.microsoft.com/en-us/library/kw65a0we.aspx" >Word
 *      Object Model Overview</a>
 */
public class WdApplication {

    public static final String ID = "application";

    // ActiveX Identifier of Microsoft Word.
    private static final String WORD_APPLICATION = "Word.Application";

    // Properties
    public static final String DOCUMENTS = WdDocuments.ID;
    public static final String VISIBLE = "Visible";

    // Methods
    public static final String QUIT = "Quit";

    // Private attributes
    private ActiveXComponent winword;
    private WdDocuments documents;

    public WdApplication(boolean isVisible) {

        winword = loadWinword(isVisible);
    }

    public WdDocuments getDocuments() {
        if (documents == null) {
            documents = new WdDocuments(getProperty(DOCUMENTS));
        }
        return documents;
    }

    public void quit() {

        documents.close();
        winword.invoke(QUIT, new Variant[0]);
    }

    Dispatch getProperty(String property) {
        return winword.getProperty(property).toDispatch();
    }

    private ActiveXComponent loadWinword(boolean isVisible) {

        winword = new ActiveXComponent(WORD_APPLICATION);
        winword.setProperty(VISIBLE, isVisible);

        return winword;
    }
}
