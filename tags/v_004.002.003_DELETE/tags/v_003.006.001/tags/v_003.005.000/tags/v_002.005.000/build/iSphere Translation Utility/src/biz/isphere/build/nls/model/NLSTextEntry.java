/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.model;

/**
 * This class represents a language text of a language properties file. Text
 * entries of the default language are flagged as "protected".
 * 
 * @author Thomas Raddatz
 */
public class NLSTextEntry {

    private String fText;
    private boolean fIsProtected;

    public NLSTextEntry(String text, boolean isProtected) {
        fText = text;
        fIsProtected = isProtected;
    }

    public String getText() {
        return fText;
    }

    public boolean isProtected() {
        return fIsProtected;
    }

    @Override
    public String toString() {
        return fText + " (" + isProtected() + ")";
    }

}
