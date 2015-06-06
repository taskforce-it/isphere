/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

public class DTemplateReferencedObject {

    private String name;
    private String library;
    private String type;

    public DTemplateReferencedObject(String name, String library, String type) {
        this.name = name;
        this.library = library;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getLibrary() {
        return library;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + "/" + library + "(" + type + ")";
    }
}
