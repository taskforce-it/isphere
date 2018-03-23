/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.dataspaceeditordesigner.rse;

import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDataSpaceEditorDesigner;
import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDropDataSpaceListener;
import biz.isphere.core.dataspaceeditordesigner.rse.IDropObjectListener;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.dataspace.rse.WrappedDataSpace;

public class DataSpaceEditorDesigner extends AbstractDataSpaceEditorDesigner {

    @Override
    protected AbstractDropDataSpaceListener createEditorDropListener(IDropObjectListener editor) {
        return new DropDataSpaceListener(editor);
    }

    @Override
    protected AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception {
        return new WrappedDataSpace(remoteObject);
    }
}
