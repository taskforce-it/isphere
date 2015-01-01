/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.StatusBar;

/**
 * Abstract class that implements the basic services of a data area editor
 * delegate.
 * <p>
 * The delegate that inherits from this class is responsible for implementing
 * the specific details of a data area of a certain type.
 */
public abstract class AbstractDataSpaceEditorDelegate implements IFindReplaceTarget {

    private AbstractDataSpaceEditor dataAreaEditor;
    private StatusBar statusBar;
    private Clipboard clipboard;

    public AbstractDataSpaceEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor) {
        dataAreaEditor = aDataAreaEditor;
    }

    /**
     * Returns the data area that is edited.
     * 
     * @return data area that is currently edited
     */
    protected AbstractWrappedDataSpace getWrappedDataSpace() {
        return dataAreaEditor.getWrappedDataArea();
    }

    /**
     * Sets the status bar that displays the status of this editor delegate.
     * 
     * @param aStatusBar - status bar that displays the editor status
     */
    public void setStatusBar(StatusBar aStatusBar) {
        statusBar = aStatusBar;
    }

    /**
     * Sets a message that is shown in the status bar.
     * 
     * @param message - message text
     */
    public void setStatusMessage(String message) {
        statusBar.setMessage(message);
    }

    /**
     * Sets an info message that is shown in the status bar.
     * 
     * @param message - message text
     */
    public void setInfoMessage(String message) {
        statusBar.setInfo(message);
    }

    /**
     * Returns the status bar that displays the status of this editor.
     * 
     * @return status bar that displays the editor status
     */
    protected StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * used by the delegates to set a clipboard text.
     * 
     * @param aText - text that is put into the clipboard
     */
    protected void setClipboardText(String aText) {
        TextTransfer textTransfer = TextTransfer.getInstance();
        getClipboard().setContents(new Object[] { aText }, new Transfer[] { textTransfer });
    }

    /**
     * Returns the text that is currently in the clipboard.
     * 
     * @return text that is in the clipboard
     */
    protected String getClipboardText() {
        TextTransfer transfer = TextTransfer.getInstance();
        return (String)getClipboard().getContents(transfer);
    }

    /**
     * Returns the clipboard that is associated to the editor.
     * 
     * @return clipboard
     */
    private Clipboard getClipboard() {
        if (clipboard == null) {
            clipboard = new Clipboard(dataAreaEditor.getSite().getShell().getDisplay());
        }
        return clipboard;
    }

    /**
     * Called by the delegates to create the initial editor composite.
     * 
     * @param aParent - parent composite
     * @param aColumns - number of columns
     * @return editor area composite
     */
    protected Composite createEditorArea(Composite aParent, int aColumns) {
        Composite editorArea = new Composite(aParent, SWT.NONE);
        GridLayout editorAreaLayout = new GridLayout(aColumns, false);
        editorAreaLayout.marginTop = 0;
        editorAreaLayout.marginBottom = 10;
        editorAreaLayout.marginLeft = 0;
        editorArea.setLayout(editorAreaLayout);
        return editorArea;
    }

    /**
     * Called by the delegates to set the editor dirty.
     * <p>
     * The delegates use various listeners to detect changes on the data area
     * value.
     */
    protected void setEditorDirty() {
        dataAreaEditor.setDirty(true);
    }

    /**
     * Used by the delegates to handle failures on saving the editor data.
     * 
     * @param aMonitor
     * @param anException
     */
    protected void handleSaveResult(IProgressMonitor aMonitor, Throwable anException) {
        if (anException != null) {
            statusBar.setMessage(anException.getLocalizedMessage());
            aMonitor.setCanceled(true);
        } else {
            statusBar.setMessage(null);
            resetDirtyFlag();
        }
    }

    /**
     * Returns the FindReplaceAction that overrides the original action of the
     * editor widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return FindReplaceAction to override the original behavior
     */
    public Action getFindReplaceAction(EditorPart anEditorPart) {
        return null;
    }

    /**
     * Returns the CutAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return CutAction to override the original behavior
     */
    public Action getCutAction() {
        return null;
    }

    /**
     * Returns the CopyAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return CopyAction to override the original behavior
     */
    public Action getCopyAction() {
        return null;
    }

    /**
     * Returns the PasteAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return PasteAction to override the original behavior
     */
    public Action getPasteAction() {
        return null;
    }

    /**
     * Called by the editor part, when the parts is disposed. Overridden, to
     * dispose the clipboard.
     */
    public void dispose() {
        if (clipboard != null) {
            clipboard.dispose();
        }
    }

    /**
     * Returns whether a find operation can be performed.
     * 
     * @return whether a find operation can be performed
     */
    public boolean canPerformFind() {
        return false;
    }

    /**
     * Searches for a string starting at the given widget offset and using the
     * specified search directives. If a string has been found it is selected
     * and its start offset is returned.
     * 
     * @param aWidgetOffset - the widget offset at which searching starts
     * @param aFindString - the string which should be found
     * @param aSearchForward - <code>true</code> searches forward,
     *        <code>false</code> backwards
     * @param aCaseSensitive - <code>true</code> performs a case sensitive
     *        search, <code>false</code> an insensitive search
     * @param aWholeWord - if <code>true</code> only occurrences are reported in
     *        which the findString stands as a word by itself
     */
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {
        return -1; // not found
    }

    /**
     * Returns the currently selected range of characters as a offset and length
     * in widget coordinates.
     * 
     * @return the currently selected character range in widget coordinates
     */
    public Point getSelection() {
        return new Point(0, 0);
    }

    /**
     * Returns the currently selected characters as a string.
     * 
     * @return the currently selected characters
     */
    public String getSelectionText() {
        return "";
    }

    /**
     * Returns whether this target can be modified.
     * 
     * @return <code>true</code> if target can be modified
     */
    public boolean isEditable() {
        return false;
    }

    /**
     * Replaces the currently selected range of characters with the given text.
     * This target must be editable. Otherwise nothing happens.
     * 
     * @param aText - the substitution text
     */
    public void replaceSelection(String aText) {
        return;
    }

    /**
     * Called, when the content of the editor (data area) has to be saved.
     */
    public abstract void doSave(IProgressMonitor aMonitor);

    /**
     * Called, when the of the editor is created. This method has to create a
     * specialized editor part depending on the type of the data area.
     */
    public abstract void createPartControl(Composite anEditorParent);

    /**
     * Called, when the editor is displayed to set the initial focus.
     */
    public abstract void setInitialFocus();

    public abstract void setEnabled(boolean isEnabled);

    protected void resetDirtyFlag() {
        dataAreaEditor.setDirty(false);
    }

}
