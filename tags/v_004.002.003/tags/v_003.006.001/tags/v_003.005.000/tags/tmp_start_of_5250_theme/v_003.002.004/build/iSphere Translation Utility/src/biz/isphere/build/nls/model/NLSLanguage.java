/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.model;

import biz.isphere.build.nls.configuration.Configuration;
import biz.isphere.build.nls.exception.JobCanceledException;

/**
 * Class to represent a language. Languages can be protected and hence text
 * entries belonging to a protected language cannot be changed in the Excel
 * sheet.
 * 
 * @author Thomas Raddatz
 */
public class NLSLanguage {

    private String fLanguageID;
    private boolean fIsProtected;
    private boolean fIsDefaultLanguage;

    public NLSLanguage(String languageID) throws JobCanceledException {
        fLanguageID = languageID;
        fIsDefaultLanguage = Configuration.getInstance().getDefaultLanguageID().equalsIgnoreCase(languageID);
        fIsProtected = fIsDefaultLanguage;
    }

    public String getLanguageID() {
        return fLanguageID;
    }

    public boolean isProtected() {
        return fIsProtected;
    }

    public boolean isDefaultLanguage() {
        return fIsDefaultLanguage;
    }

    @Override
    public String toString() {
        return fLanguageID + " (" + isProtected() + ")";
    }

}
