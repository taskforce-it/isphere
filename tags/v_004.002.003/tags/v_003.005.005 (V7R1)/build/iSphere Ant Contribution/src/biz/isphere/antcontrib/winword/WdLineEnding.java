/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

/**
 * This class defines the WdLineEnding enumeration of Microsoft Word.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/microsoft.office.interop.word.wdlineendingtype%28v=office.15%29.aspx"
 *      >WdLineEnding enumeration</a>
 * @author Thomas Raddatz
 */
public enum WdLineEnding {

    /**
     * Carriage return plus line feed.
     */
    CRLF (0),

    /**
     * Carriage return only.
     */
    CR_ONLY (1),

    /**
     * Line feed only.
     */
    LF_ONLY (2),
    
    /**
     * Line feed plus carriage return.
     */
    LFCR (3),
    
    /**
     * Not supported.
     */
    LSPS (4);

    private final int value;

    private WdLineEnding(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}