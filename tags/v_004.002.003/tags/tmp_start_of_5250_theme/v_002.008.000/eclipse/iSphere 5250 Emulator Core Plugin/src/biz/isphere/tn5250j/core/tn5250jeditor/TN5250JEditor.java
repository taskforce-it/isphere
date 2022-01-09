/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jeditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.tn5250j.core.tn5250jpart.ITN5250JPart;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPanel;
import biz.isphere.tn5250j.core.tn5250jpart.TN5250JPart;

public abstract class TN5250JEditor extends EditorPart implements ITN5250JPart, ISaveablePart2 {

    private TN5250JPart tn5250jPart;

    @Override
    public void createPartControl(Composite parent) {

        tn5250jPart = new TN5250JPart(this, getEditorSite().getActionBars().getToolBarManager(), this, isMultiSession());

        tn5250jPart.createPartControl(parent);

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(((TN5250JEditorInput)input).getImage());
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void dispose() {
        tn5250jPart.dispose();
        super.dispose();
    }

    public CTabFolder getTabFolderSessions() {
        return tn5250jPart.getTabFolderSessions();
    }

    public void addTN5250JPanel(TN5250JPanel tn5250jPanel) {
        tn5250jPart.addTN5250JPanel(tn5250jPanel);
    }

    public void removeTN5250JPanel(TN5250JPanel tn5250jPanel) {
        tn5250jPart.removeTN5250JPanel(tn5250jPanel);
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

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
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
