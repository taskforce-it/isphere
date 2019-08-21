/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;

import biz.isphere.core.spooledfiles.SpooledFileTextDecoration;
import biz.isphere.rse.Messages;

public class SpooledFileDecorateWithUserDefinedAction extends AbstractSpooledFileDecorateWithAction {

    protected ArrayList arrayListSelection;

    public SpooledFileDecorateWithUserDefinedAction() {
        super(SpooledFileTextDecoration.USER_DEFINED.getKey(), Messages.SplfDecoration_UserDefined);
    }
    
}
