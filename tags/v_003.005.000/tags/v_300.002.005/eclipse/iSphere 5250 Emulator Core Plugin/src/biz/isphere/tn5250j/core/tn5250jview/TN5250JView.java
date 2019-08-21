/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jview;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.tn5250j.keyboard.KeyMapper;

import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JInfo;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPart;

/**
 * This class is the TN5250 view. It uses a tab folder and tabs to arrange up to
 * 4 sessions in a tab.
 */
public abstract class TN5250JView extends ViewPart implements ITN5250JPart, ISaveablePart2 {

    private TN5250JPart tn5250jPart;

    @Override
    public void createPartControl(Composite parent) {

        checkFastCursorMappings();

        tn5250jPart = new TN5250JPart(this, getViewSite().getActionBars().getToolBarManager(), this, isMultiSession());

        tn5250jPart.createPartControl(parent);

        if (isMultiSession()) {
            setAddSession(false);
            setRemoveSession(false);
        }
    }

    private void checkFastCursorMappings() {

        if (KeyMapper.hasFastCursorMappingConflicts()) {
            UIJob job = new UIJob("") {

                @Override
                public IStatus runInUIThread(IProgressMonitor arg0) {
                    if (DoNotAskMeAgainDialog.openConfirm(getViewSite().getShell(), DoNotAskMeAgain.TN5250_FAST_CURSOR_MAPPING_CONFLICT,
                        "The 'Fast cursor up'/'Fast cursor down' keyboard mappings are in conflict with the 'Next multiple session'/'Previous multiple session' keyboard mappings. Do you want to change the 'multiple session' keyboard mappings from Alt+Up/Alt+Down to Ctrl+Right/Ctrl+Left?")) {
                        KeyMapper.resolveFastCursorMappingConflicts();
                        return Status.OK_STATUS;
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }
    }

    @Override
    public void setFocus() {
        tn5250jPart.setFocus();
    }

    @Override
    public void dispose() {
        tn5250jPart.dispose();
        super.dispose();
    }

    public CTabFolder getTabFolderSessions() {
        return tn5250jPart.getTabFolderSessions();
    }

    protected TN5250JInfo[] getSessionInfos() {
        return tn5250jPart.getSessionInfos();
    }

    public void addTN5250JPanel(TN5250JPanel tn5250jPanel) {
        tn5250jPart.addTN5250JPanel(tn5250jPanel);
    }

    public void removeTN5250JPanel(TN5250JPanel tn5250jPanel) {
        tn5250jPart.removeTN5250JPanel(tn5250jPanel);
    }

    public int findSessionTab(TN5250JInfo tn5250jInfo) {
        return tn5250jPart.findSessionTab(tn5250jInfo);
    }

    public boolean isMultiSession() {
        return true;
    }

    public void setAddSession(boolean value) {
        tn5250jPart.setAddSession(value);
    }

    public void setRemoveSession(boolean value) {
        tn5250jPart.setRemoveSession(value);
    }

    public void setBindingService(boolean value) {
        tn5250jPart.setBindingService(value);
    }

    public void doSave(IProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        return true;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public boolean isSaveOnCloseNeeded() {
        return true;
    }

    public int promptToSaveOnClose() {

        int result = tn5250jPart.closePart();

        if (result == TN5250JPart.CLOSE_PART_YES) {
            return ISaveablePart2.YES;
        } else {
            return ISaveablePart2.CANCEL;
        }

    }

}
