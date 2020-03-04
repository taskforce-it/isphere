/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

@SuppressWarnings("serial")
public class DComment extends AbstractDWidget {

    public static final String SEPARATOR = "*SEPARATOR";
    public static final String NONE = "*NONE";

    DComment(String label) {
        super(label, -1, -1);
    }

}
