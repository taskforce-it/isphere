/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataareaeditor;

import biz.isphere.core.annotations.CMOneDeprecated;

@CMOneDeprecated(info="Used by CMOne prior to iSphere 4.0.0", todo="fix cmone, delete class, delete package")
public class DataAreaEditor extends biz.isphere.core.dataareaeditor.DataAreaEditor{
    public DataAreaEditor() {
        System.out.println("Using this class is deprecated: " + getClass().getName());
    }
}
