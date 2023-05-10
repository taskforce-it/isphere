/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import biz.isphere.core.preferencepages.IPreferences;

public class SpooledFilesSaveToDirectoryAsHTMLAction extends AbstractSpooledFilesSaveToDirectoryAction {

    public static final String ID = "biz.isphere.rse.spooledfiles.SpooledFilesSaveToDirectoryAsHTMLAction";

    public SpooledFilesSaveToDirectoryAsHTMLAction() {
        super(IPreferences.OUTPUT_FORMAT_HTML);
    }

}
