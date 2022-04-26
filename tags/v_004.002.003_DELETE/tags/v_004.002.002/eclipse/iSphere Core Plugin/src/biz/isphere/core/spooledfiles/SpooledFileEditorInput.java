/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;

public class SpooledFileEditorInput extends FileEditorInput {

    public SpooledFileEditorInput(IFile file) {
        super(file);
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public String getFactoryId() {
        return "";
    }
}
