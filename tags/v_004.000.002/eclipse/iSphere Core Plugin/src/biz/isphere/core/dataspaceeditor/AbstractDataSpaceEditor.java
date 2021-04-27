/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.core.Messages;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspace.rse.SelectDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.delegates.AbstractDataSpaceEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.CharacterDataAreaEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.DataSpaceEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.DecimalDataAreaEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.HexDataSpaceEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.LogicalDataAreaEditorDelegate;
import biz.isphere.core.dataspaceeditor.delegates.UnsupportedDataAreaEditorDelegate;
import biz.isphere.core.dataspaceeditordesigner.model.DEditor;
import biz.isphere.core.dataspaceeditordesigner.repository.DataSpaceEditorRepository;
import biz.isphere.core.internal.IEditor;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.ObjectLock;
import biz.isphere.core.internal.ObjectLockManager;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.core.objecteditor.AbstractObjectEditorInput;

public abstract class AbstractDataSpaceEditor extends EditorPart implements IFindReplaceTarget {

    public static final int VALUE_LABEL_WIDTH_HINT = 40;
    public static final int SPACER_WIDTH_HINT = 10;

    private boolean isDirty;
    private AbstractWrappedDataSpace wrappedDataArea;
    private AbstractDataSpaceEditorDelegate editorDelegate;
    private DataSpaceEditorRepository repository;
    private ObjectLockManager objectLockManager;
    private ObjectLock objectLock;
    private String mode;
    private StatusLine statusLine;

    public AbstractDataSpaceEditor() {
        isDirty = false;
        objectLockManager = new ObjectLockManager(0);
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    protected IStatusLineManager getStatusLineManager() {
        IStatusLineManager manager = getEditorSite().getActionBars().getStatusLineManager();
        return manager;
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorParent = new Composite(aParent, SWT.NONE);
        GridLayout editorParentLayout = new GridLayout(1, false);
        editorParentLayout.marginTop = 5;
        editorParentLayout.marginWidth = 10;
        editorParent.setLayout(editorParentLayout);

        Composite header = new Composite(editorParent, SWT.NONE);
        header.setLayout(new GridLayout(3, false));

        String type = "";
        String length = "0"; //$NON-NLS-1$
        String text = "";
        if ((mode.equals(IEditor.EDIT) && objectLock != null) || mode.equals(IEditor.DISPLAY)) {
            type = getWrappedDataArea().getDataType();
            length = getWrappedDataArea().getLengthAsText();
            text = getWrappedDataArea().getText();
        }

        createHeadline(header, Messages.Type_colon, type);
        createHeadline(header, Messages.Length_colon, length);
        createHeadline(header, Messages.Text_colon, text);

        editorDelegate = createEditorDelegate();
        if ((mode.equals(IEditor.EDIT) && objectLock != null) || mode.equals(IEditor.DISPLAY)) {
            editorDelegate.createPartControl(editorParent);
        }

        registerDelegateActions();

        if (mode.equals(IEditor.EDIT)) {
            if (objectLock == null) {
                editorDelegate.setEnabled(false);
                editorDelegate.setStatusMessage(getObjectLockMessage(null));
            }
        } else if (mode.equals(IEditor.DISPLAY)) {
            editorDelegate.setEnabled(false);
        }

    }

    private void createHeadline(Composite aHeader, String aLabel, String aValue) {
        Label lblText = new Label(aHeader, SWT.NONE);
        GridData lblTextLayoutData = new GridData();
        lblText.setLayoutData(lblTextLayoutData);
        lblText.setText(aLabel);

        Composite spacer = new Composite(aHeader, SWT.NONE);
        GridData spacerLayoutData = new GridData();
        spacerLayoutData.widthHint = SPACER_WIDTH_HINT;
        spacerLayoutData.heightHint = 1;
        spacer.setLayoutData(spacerLayoutData);

        Label dataAreaText = new Label(aHeader, SWT.NONE);
        GridData headlineLayoutData = new GridData();
        dataAreaText.setLayoutData(headlineLayoutData);
        dataAreaText.setText(aValue);
    }

    @Override
    public void firePropertyChange(int propertyId) {
        super.firePropertyChange(propertyId);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {

        editorDelegate.doSave(aMonitor);
    }

    @Override
    public void doSaveAs() {
        // not supported
    }

    @Override
    public void init(IEditorSite aSite, IEditorInput anInput) throws PartInitException {
        setSite(aSite);
        setInput(anInput);
        setPartName(anInput.getName());
        setTitleImage(((AbstractObjectEditorInput)anInput).getTitleImage());

        AbstractObjectEditorInput input = (AbstractObjectEditorInput)anInput;

        mode = input.getMode();

        try {

            if (mode.equals(IEditor.EDIT)) {
                objectLock = objectLockManager.setExclusiveAllowReadLock(input.getRemoteObject());
                if (objectLock == null) {
                    MessageDialog.openError(getSite().getShell(), Messages.E_R_R_O_R, getObjectLockMessage(Messages.Data_cannot_be_changed));
                }
            }

            if ((mode.equals(IEditor.EDIT) && objectLock != null) || mode.equals(IEditor.DISPLAY)) {
                wrappedDataArea = createDataSpaceWrapper(input.getRemoteObject());
            } else {
                wrappedDataArea = null;
            }

        } catch (Exception e) {
            throw new PartInitException(e.getMessage(), e);
        }

        repository = DataSpaceEditorRepository.getInstance();
    }

    protected abstract AbstractWrappedDataSpace createDataSpaceWrapper(RemoteObject remoteObject) throws Exception;

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void setFocus() {
        editorDelegate.setInitialFocus();
    }

    public AbstractWrappedDataSpace getWrappedDataArea() {
        return wrappedDataArea;
    }

    public void setDirty(boolean anIsDirty) {
        isDirty = anIsDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    private String getObjectLockMessage(String secondLeveltext) {
        String message = objectLockManager.getErrorMessage();
        if (message == null) {
            return ""; //$NON-NLS-1$
        }

        if (secondLeveltext == null) {
            return message;
        }

        return message + "\n\n" + secondLeveltext;
    }

    /**
     * Creates the editor delegate depending on the type of the data area.
     * <p>
     * If the data area is of an unsupported type, a special
     * {@link UnsupportedDataAreaEditorDelegate} is returned.
     * 
     * @return editor delegate
     */
    private AbstractDataSpaceEditorDelegate createEditorDelegate() {

        if (getWrappedDataArea() == null) {
            return new UnsupportedDataAreaEditorDelegate(this);
        }

        DEditor selectedEditor = null;
        DEditor[] dEditors = repository.getDataSpaceEditorsForObject(getWrappedDataArea().getRemoteObject());
        if (dEditors != null) {
            if (dEditors.length >= 1) {
                SelectDataSpaceEditor dialogSelector = new SelectDataSpaceEditor(getSite().getShell(), dEditors);
                if (dialogSelector.open() == Dialog.OK) {
                    selectedEditor = dialogSelector.getSelectedDialog();
                }
            }
        }

        if (selectedEditor != null) {
            return new DataSpaceEditorDelegate(this, selectedEditor);
        }

        if (ISeries.USRSPC.equals(getWrappedDataArea().getObjectType())) {
            return new HexDataSpaceEditorDelegate(this);
        } else if (AbstractWrappedDataSpace.CHARACTER.equals(getWrappedDataArea().getDataType())) {
            return new CharacterDataAreaEditorDelegate(this);
        } else if (AbstractWrappedDataSpace.DECIMAL.equals(getWrappedDataArea().getDataType())) {
            return new DecimalDataAreaEditorDelegate(this);
        } else if (AbstractWrappedDataSpace.LOGICAL.equals(getWrappedDataArea().getDataType())) {
            return new LogicalDataAreaEditorDelegate(this);
        }

        return new UnsupportedDataAreaEditorDelegate(this);
    }

    /**
     * Registers the actions, the delegate wants to override.
     * <p>
     * Some delegates, such as the {@link CharacterDataAreaEditorDelegate}, may
     * want to change the behavior of the <i>Find & Replace</i>, <i>Copy</i>,
     * <i>Cut</i> and <i>Paste</i> actions to match their specific needs.
     */
    public void registerDelegateActions() {

        final Action findReplaceAction = editorDelegate.getFindReplaceAction(this);
        if (findReplaceAction != null) {
            IHandlerService handlerService = (IHandlerService)getEditorSite().getService(IHandlerService.class);
            IHandler handler = new AbstractHandler() {
                public Object execute(ExecutionEvent event) throws ExecutionException {
                    findReplaceAction.run();
                    return null;
                }
            };
            handlerService.activateHandler("org.eclipse.ui.edit.findReplace", handler); //$NON-NLS-1$
        }
    }

    /**
     * Overridden, to dispose resources allocated by the delegate.
     */
    @Override
    public void dispose() {
        editorDelegate.dispose();
        objectLockManager.dispose();
        super.dispose();
    }

    public void doGoTo() {
        editorDelegate.doGoTo();
    }

    public boolean canGoTo() {
        return editorDelegate.canGoTo();
    }

    public void doCut() {
        editorDelegate.doCut();
    }

    public boolean canCut() {
        return editorDelegate.canCut();
    }

    public void doCopy() {
        editorDelegate.doCopy();
    }

    public boolean canCopy() {
        return editorDelegate.canCopy();
    }

    public void doPaste() {
        editorDelegate.doPaste();
    }

    public boolean canPaste() {
        return editorDelegate.canPaste();
    }

    public void doDelete() {
        editorDelegate.doDelete();
    }

    public boolean canDelete() {
        return editorDelegate.canDelete();
    }

    public void doSelectAll() {
        editorDelegate.doSelectAll();
    }

    public boolean canSelectAll() {
        return editorDelegate.canSelectAll();
    }

    public void doRedo() {
        editorDelegate.doRedo();
    }

    public boolean canRedo() {
        return editorDelegate.canRedo();
    }

    public void doUndo() {
        editorDelegate.doUndo();
    }

    public boolean canUndo() {
        return editorDelegate.canUndo();
    }

    public void updateActionsStatusAndStatusLine() {

        editorDelegate.updateActionStatus();
        editorDelegate.updateStatusLine();
    }

    /**
     * Returns whether a find operation can be performed.
     * 
     * @return whether a find operation can be performed
     * @see IFindReplaceTarget
     */
    public boolean canPerformFind() {
        return editorDelegate.canPerformFind();
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
     * @return the position of the specified string, or -1 if the string has not
     *         been found
     * @see IFindReplaceTarget
     */
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {
        return editorDelegate.findAndSelect(aWidgetOffset, aFindString, aSearchForward, aCaseSensitive, aWholeWord);
    }

    /**
     * Returns the currently selected range of characters as a offset and length
     * in widget coordinates.
     * 
     * @return the currently selected character range in widget coordinates
     * @see IFindReplaceTarget
     */
    public Point getSelection() {
        return editorDelegate.getSelection();
    }

    /**
     * Returns the currently selected characters as a string.
     * 
     * @return the currently selected characters
     * @see IFindReplaceTarget
     */
    public String getSelectionText() {
        return editorDelegate.getSelectionText();
    }

    /**
     * Returns whether this target can be modified.
     * 
     * @return <code>true</code> if target can be modified
     * @see IFindReplaceTarget
     */
    public boolean isEditable() {
        return editorDelegate.isEditable();
    }

    /**
     * Replaces the currently selected range of characters with the given text.
     * This target must be editable. Otherwise nothing happens.
     * 
     * @param aText - the substitution text
     * @see IFindReplaceTarget
     */
    public void replaceSelection(String aText) {
        editorDelegate.replaceSelection(aText);
    }
}
