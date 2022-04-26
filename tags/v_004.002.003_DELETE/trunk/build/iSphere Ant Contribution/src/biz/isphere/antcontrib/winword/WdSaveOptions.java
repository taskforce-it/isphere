/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

/**
 * This class defines the WdSaveOptions enumeration of Microsoft Word.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/microsoft.office.interop.word.wdsaveoptions%28v=office.15%29.aspx"
 *      >WdSaveOptions enumeration</a>
 * @author Thomas Raddatz
 */
public enum WdSaveOptions {

    /**
     * Do not save pending changes.
     */
    DO_NOT_SAVE_CHANGES (0),

    /**
     * Save pending changes automatically without prompting the user.
     */
    SAVE_CHANGES (-1),

    /**
     * Prompt the user to save pending changes.
     */
    PROMPT_TO_SAVE_CHANGES (-2);

    private final int value;

    private WdSaveOptions(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}