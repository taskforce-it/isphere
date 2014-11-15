/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.EditorPart;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataareaeditor.delegates.AbstractDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.CharacterDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.DecimalDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.LogicalDataAreaEditorDelegate;
import biz.isphere.core.dataareaeditor.delegates.UnsupportedDataAreaEditorDelegate;
import biz.isphere.core.dataspace.rse.AbstractWrappedDataSpace;
import biz.isphere.core.dataspaceeditor.rse.RemoteObject;
import biz.isphere.core.internal.IEditor;

import com.ibm.as400.access.AS400;

public abstract class AbstractDataAreaEditor extends EditorPart implements IFindReplaceTarget {

    public static final String ID = "biz.isphere.core.dataareaeditor.DataAreaEditor"; //$NON-NLS-1$

    public static final int VALUE_LABEL_WIDTH_HINT = 40;
    public static final int SPACER_WIDTH_HINT = 10;

    private boolean isDirty;
    AbstractWrappedDataSpace wrappedDataArea;
    AbstractDataAreaEditorDelegate editorDelegate;

    public AbstractDataAreaEditor() {
        isDirty = false;
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

        createHeadline(header, Messages.Type_colon, getWrappedDataArea().getDataType());
        createHeadline(header, Messages.Length_colon, getWrappedDataArea().getLengthAsText());
        createHeadline(header, Messages.Text_colon, getWrappedDataArea().getText());

        aParent.getClientArea();

        editorDelegate = createEditorDelegate();
        editorDelegate.createPartControl(editorParent);
        editorDelegate.setStatusBar(new StatusBar(editorParent));

        registerDelegateActions();
    }

    private void createHeadline(Composite aHeader, String aLabel, String aValue) {
        Label lblText = new Label(aHeader, SWT.NONE);
        GridData lblTextLayoutData = new GridData();
        lblTextLayoutData.widthHint = VALUE_LABEL_WIDTH_HINT;
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
        setTitleImage(((DataAreaEditorInput)anInput).getTitleImage());

        DataAreaEditorInput input = (DataAreaEditorInput)anInput;
        try {
            wrappedDataArea = createDataSpaceWrapper(input.getRemoteObject());
        } catch (Exception e) {
            throw new PartInitException(e.getMessage(), e);
        }
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

    /**
     * Creates the editor delegate depending on the type of the data area.
     * <p>
     * If the data area is of an unsupported type, a special
     * {@link UnsupportedDataAreaEditorDelegate} is returned.
     * 
     * @return editor delegate
     */
    private AbstractDataAreaEditorDelegate createEditorDelegate() {
        if (AbstractWrappedDataSpace.CHARACTER.equals(getWrappedDataArea().getDataType())) {
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
    private void registerDelegateActions() {

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

        if (editorDelegate.getCutAction() != null) {
            IActionBars actionBars = getEditorSite().getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), editorDelegate.getCutAction());
        }

        if (editorDelegate.getCopyAction() != null) {
            IActionBars actionBars = getEditorSite().getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), editorDelegate.getCopyAction());
        }

        if (editorDelegate.getPasteAction() != null) {
            IActionBars actionBars = getEditorSite().getActionBars();
            actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), editorDelegate.getPasteAction());
        }
    }
    
    /**
     * Opens the data area editor for a given data area.
     * 
     * @param anAS400 - system that hosts the data area.
     * @param aConnection - connection used to access the system.
     * @param aLibrary - library that contains the data area
     * @param aDataArea - the data area
     * @param aMode - mode, the editor is opened for. The only allowed value is
     *        {@link IEditor#EDIT}
     */
    public static void openEditor(AS400 anAS400, RemoteObject remoteObject, String aMode) {

        try {

            DataAreaEditorInput editorInput = new DataAreaEditorInput(anAS400, remoteObject, aMode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, AbstractDataAreaEditor.ID);

        } catch (PartInitException e) {
            ISpherePlugin.logError("Failed to open data area editor", e); //$NON-NLS-1$
        }

    }

    /**
     * Overridden, to dispose resources allocated by the delegate.
     */
    @Override
    public void dispose() {
        editorDelegate.dispose();
        super.dispose();
    }

    /**
     * Returns whether a find operation can be performed.
     * 
     * @return whether a find operation can be performed
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
     */
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {
        return editorDelegate.findAndSelect(aWidgetOffset, aFindString, aSearchForward, aCaseSensitive, aWholeWord);
    }

    /**
     * Returns the currently selected range of characters as a offset and length
     * in widget coordinates.
     * 
     * @return the currently selected character range in widget coordinates
     */
    public Point getSelection() {
        return editorDelegate.getSelection();
    }

    /**
     * Returns the currently selected characters as a string.
     * 
     * @return the currently selected characters
     */
    public String getSelectionText() {
        return editorDelegate.getSelectionText();
    }

    /**
     * Returns whether this target can be modified.
     * 
     * @return <code>true</code> if target can be modified
     */
    public boolean isEditable() {
        return editorDelegate.isEditable();
    }

    /**
     * Replaces the currently selected range of characters with the given text.
     * This target must be editable. Otherwise nothing happens.
     * 
     * @param aText - the substitution text
     */
    public void replaceSelection(String aText) {
        editorDelegate.replaceSelection(aText);
    }

}
