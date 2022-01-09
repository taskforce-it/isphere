/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.spooledfiles.popupmenu.extension;

import org.eclipse.swt.graphics.Image;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributionItem;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;
import biz.isphere.joblogexplorer.jobs.rse.LoadRemoteSpooledFileJob;

public class OpenJobLogExplorerContributionItem implements ISpooledFilePopupMenuContributionItem {

    private SpooledFile[] spooledFiles;

    public String getText() {
        return Messages.Job_Log_Explorer;
    }

    public String getTooltipText() {
        return null;
    }

    public Image getImage() {
        return ISphereJobLogExplorerPlugin.getDefault().getImage(ISphereJobLogExplorerPlugin.IMAGE_JOB_LOG_EXPLORER);
    }

    public void setSelection(SpooledFile[] spooledFiles) {
        this.spooledFiles = spooledFiles;
    }

    public boolean isEnabled() {

        if (spooledFiles.length != 1) {
            return false;
        }

        for (SpooledFile spooledFile : spooledFiles) {
            if (!"QPJOBLOG".equals(spooledFile.getFile())) { //$NON-NLS-1$
                return false;
            }
        }

        return true;
    }

    public void execute() {

        if (spooledFiles == null || spooledFiles.length == 0) {
            return;
        }

        for (SpooledFile spooledFile : spooledFiles) {
            new LoadRemoteSpooledFileJob(spooledFile).run();
        }
    }
}
