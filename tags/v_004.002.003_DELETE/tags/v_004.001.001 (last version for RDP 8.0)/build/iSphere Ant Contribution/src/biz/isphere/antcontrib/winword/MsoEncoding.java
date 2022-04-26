/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.winword;

/**
 * This class defines the MsoEncoding enumeration of Microsoft Office.
 * 
 * @see <a href="http://msdn.microsoft.com/EN-US/library/ms250894" >MsoEncoding
 *      enumeration</a>
 * @author Thomas Raddatz
 */
public enum MsoEncoding {

    /**
     * Western.
     */
    WESTERN (0x4E4),

    /**
     * United States ASCII.
     */
    US_ASCII (0x4E9F);

    private final int value;

    private MsoEncoding(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}