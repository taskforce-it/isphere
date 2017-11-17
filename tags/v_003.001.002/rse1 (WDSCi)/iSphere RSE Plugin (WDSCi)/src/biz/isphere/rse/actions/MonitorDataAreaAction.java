/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.actions;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ISeries;
import biz.isphere.rse.Messages;

public class MonitorDataAreaAction extends AbstractMonitorDataSpaceAction {

    public MonitorDataAreaAction() {
        super(Messages.iSphere_Data_Area_Monitor, ISpherePlugin.IMAGE_DATA_AREA_MONITOR, ISeries.DTAARA);
    }
}