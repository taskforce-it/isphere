/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.swt.widgets.Composite;

import biz.isphere.core.spooledfiles.AbstractSpooledFileProperties;
import biz.isphere.core.spooledfiles.SpooledFile;

public class SpooledFileProperties extends AbstractSpooledFileProperties {

    private SpooledFile spooledFile;

    public SpooledFileProperties(Composite parent, SpooledFile spooledFile) {

        this.spooledFile = spooledFile;

        createContents(parent);
    }

    @Override
    public SpooledFile getSpooledFile() {
        return spooledFile;
    }
}
