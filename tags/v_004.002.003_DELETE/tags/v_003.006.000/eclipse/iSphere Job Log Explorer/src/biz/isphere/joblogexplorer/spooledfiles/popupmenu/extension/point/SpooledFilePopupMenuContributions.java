/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.spooledfiles.popupmenu.extension.point;

import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributionItem;
import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributions;
import biz.isphere.joblogexplorer.spooledfiles.popupmenu.extension.OpenJobLogExplorerContributionItem;

public class SpooledFilePopupMenuContributions implements ISpooledFilePopupMenuContributions {

    public ISpooledFilePopupMenuContributionItem[] getContributionItems() {
        return new ISpooledFilePopupMenuContributionItem[] { new OpenJobLogExplorerContributionItem() };
    }

}
