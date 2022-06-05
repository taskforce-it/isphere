/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.actions;

import biz.isphere.core.internal.ISeries;

public class MonitorUserSpaceAction extends AbstractMonitorDataSpaceAction {

    public MonitorUserSpaceAction() {
        super(ISeries.USRSPC);
    }
}
