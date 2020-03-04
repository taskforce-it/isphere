/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import com.ibm.as400.access.AS400;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

import biz.isphere.base.internal.StringHelper;

public class PcmlProgramCallDocument extends ProgramCallDocument {

    private static final long serialVersionUID = 4373969780285460768L;

    public PcmlProgramCallDocument(AS400 paramAS400, String paramString, ClassLoader paramClassLoader) throws PcmlException {
        super(paramAS400, paramString, paramClassLoader);
    }

    public void setQualifiedObjectName(String aParameter, String aLibrary, String aName) throws PcmlException {
        setValue(aParameter, StringHelper.getFixLength(aName, 10) + StringHelper.getFixLength(aLibrary, 10));
    }

}
