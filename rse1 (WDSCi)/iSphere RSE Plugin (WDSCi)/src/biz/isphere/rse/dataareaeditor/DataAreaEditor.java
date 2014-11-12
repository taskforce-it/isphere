/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataareaeditor;

import biz.isphere.core.dataareaeditor.AbstractDataAreaEditor;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.rse.RemoteObject;

public class DataAreaEditor extends AbstractDataAreaEditor {

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {
        return new WrappedDataSpace(remoteObject);
    }

}
