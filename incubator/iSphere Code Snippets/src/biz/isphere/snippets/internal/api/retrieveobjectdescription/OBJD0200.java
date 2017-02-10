/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.snippets.internal.api.retrieveobjectdescription;


/**
 * Format OBJD0200 of the "Retrieve Object Description (QUSROBJD)" API.
 */
public class OBJD0200 extends OBJD0100{

    private String extendedObjectAttriute;
    private String textDescription;
    private String sourceFile;
    private String sourceFileLibrary;
    private String sourceMember;
    
    public String getExtendedObjectAttriute() {
        return extendedObjectAttriute;
    }
    
    public void setExtendedObjectAttriute(String extendedObjectAttriute) {
        this.extendedObjectAttriute = extendedObjectAttriute;
    }
    
    public String getTextDescription() {
        return textDescription;
    }
    
    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }
    
    public String getSourceFile() {
        return sourceFile;
    }
    
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    public String getSourceFileLibrary() {
        return sourceFileLibrary;
    }
    
    public void setSourceFileLibrary(String libraryName) {
        this.sourceFileLibrary = libraryName;
    }
    
    public String getSourceMember() {
        return sourceMember;
    }
    
    public void setSourceMember(String sourceMember) {
        this.sourceMember = sourceMember;
        
    }
}
