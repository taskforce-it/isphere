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
    private NLSLanguage fLanguage;

    public NLSTextEntry(String text, NLSLanguage language) {
        fText = text;
        fLanguage = language;
    }

    public String getText() {
        return fText;
    }

    public boolean isProtected() {
        return fLanguage.isProtected();
    }

    public boolean isDefaultLanguage() {
        return fLanguage.isDefaultLanguage();
    }

    @Override
    public String toString() {
        return fText + " (" + isProtected() + ")";
    }

}
