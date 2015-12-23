/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.delegates;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.StatusLine;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.hexeditor.HexText;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContentFinder;

/**
 * Editor delegate that edits a *LGL data area.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class HexDataSpaceEditorDelegate extends AbstractDataSpaceEditorDelegate {

    private HexText dataAreaText;

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action undoAction;
    private Action redoAction;
    private Action deleteAction;
    private Action selectAllAction;
    private Action findReplaceAction;

    private String statusMessage;

    public HexDataSpaceEditorDelegate(AbstractDataSpaceEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    @Override
    public void createPartControl(Composite aParent) {

        FontRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getFontRegistry();
        registry.addListener(new FontChangedListener());

        ScrolledComposite editorAreaScrollable = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.NONE);
        editorAreaScrollable.setLayout(new GridLayout(1, false));
        editorAreaScrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        editorAreaScrollable.setExpandHorizontal(true);
        editorAreaScrollable.setExpandVertical(true);

        Composite editorArea = createEditorArea(editorAreaScrollable, 3);
        editorArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label lblCcsid = new Label(editorArea, SWT.NONE);
        GridData lblCcsidLayoutData = new GridData();
        lblCcsidLayoutData.widthHint = AbstractDataSpaceEditor.VALUE_LABEL_WIDTH_HINT;
        lblCcsidLayoutData.verticalAlignment = GridData.BEGINNING;
        lblCcsid.setLayoutData(lblCcsidLayoutData);
        lblCcsid.setText(Messages.Ccsid_colon);

        Composite horizontalSpacer1 = new Composite(editorArea, SWT.NONE);
        GridData horizontalSpacerLayoutData1 = new GridData();
        horizontalSpacerLayoutData1.widthHint = AbstractDataSpaceEditor.SPACER_WIDTH_HINT;
        horizontalSpacerLayoutData1.heightHint = 1;
        horizontalSpacer1.setLayoutData(horizontalSpacerLayoutData1);

        Combo comboCcsid = WidgetFactory.createReadOnlyCombo(editorArea);
        GridData comboCcsidLayoutData = new GridData();
        comboCcsidLayoutData.widthHint = AbstractDataSpaceEditor.VALUE_LABEL_WIDTH_HINT;
        comboCcsidLayoutData.verticalAlignment = GridData.BEGINNING;
        comboCcsid.setLayoutData(comboCcsidLayoutData);
        Properties ccsids = loadCcsids();
        String[] listOfCcsids = ccsids.values().toArray(new String[ccsids.size()]);
        Arrays.sort(listOfCcsids);
        comboCcsid.setItems(listOfCcsids);
        comboCcsid.setData("CCSIDS", ccsids);
        GridData comboCcsidGd = new GridData();
        comboCcsidGd.widthHint = 240;
        comboCcsid.setLayoutData(comboCcsidGd);
        comboCcsid.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                Combo combo = (Combo)event.widget;
                String ccsid = findCharset((Properties)combo.getData("CCSIDS"), combo.getText());
                dataAreaText.setCharset(ccsid);
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });

        Label lblValue = new Label(editorArea, SWT.NONE);
        GridData lblValueLayoutData = new GridData();
        lblValueLayoutData.widthHint = AbstractDataSpaceEditor.VALUE_LABEL_WIDTH_HINT;
        lblValueLayoutData.verticalAlignment = GridData.BEGINNING;
        lblValue.setLayoutData(lblValueLayoutData);
        lblValue.setText(Messages.Value_colon);

        Composite horizontalSpacer2 = new Composite(editorArea, SWT.NONE);
        GridData horizontalSpacerLayoutData2 = new GridData();
        horizontalSpacerLayoutData2.widthHint = AbstractDataSpaceEditor.SPACER_WIDTH_HINT;
        horizontalSpacerLayoutData2.heightHint = 1;
        horizontalSpacer2.setLayoutData(horizontalSpacerLayoutData2);

        dataAreaText = WidgetFactory.createHexText(editorArea);
        dataAreaText.setFont(getEditorFont());
        dataAreaText.setModes(HexText.OVERWRITE);
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true);
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite verticalSpacer = new Composite(editorAreaScrollable, SWT.NONE);
        GridData verticalSpacerLayoutData = new GridData();
        verticalSpacerLayoutData.grabExcessHorizontalSpace = true;
        verticalSpacerLayoutData.grabExcessVerticalSpace = true;
        verticalSpacerLayoutData.horizontalSpan = 2;
        verticalSpacer.setLayoutData(verticalSpacerLayoutData);

        editorAreaScrollable.setContent(editorArea);
        editorAreaScrollable.setMinSize(editorArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Load user space data
        BinaryContent binaryContent = null;
        try {

            String ccsid = getWrappedDataSpace().getCCSIDEncoding();
            if (ccsid.startsWith("Cp")) { //$NON-NLS-1$
                ccsid = ccsid.substring(2);
            }

            int index = -1;
            String key = ccsid + ":"; //$NON-NLS-1$
            String[] items = comboCcsid.getItems();
            for (int i = 0; i < items.length; i++) {
                if (items[i].startsWith(key)) {
                    index = i;
                }
            }

            if (index >= 0) {
                comboCcsid.select(index);
            } else {
                comboCcsid.select(0);
            }

            binaryContent = new BinaryContent(getWrappedDataSpace().getBytes());

        } catch (Throwable e) {
        }

        // Set screen value
        dataAreaText.setContentProvider(binaryContent);
        dataAreaText.setCharset(findCharset(ccsids, comboCcsid.getText()));

        // Add 'dirty' listener
        dataAreaText.addListener(SWT.Modify, new Listener() {
            public void handleEvent(Event paramEvent) {
                setEditorDirty();
            }
        });

        dataAreaText.addStateChangeListener(new HexText.StateChangeListener() {
            public void changed(HexText.StateChangeEvent event) {
                updateActionsStatus();
                updateStatusLine();
            }
        });

        dataAreaText.addLongSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateActionsStatus();
                updateStatusLine();
            }
        });
    }

    private Properties loadCcsids() {

        Properties ccsids = new Properties();

        try {
            InputStream inStream = getClass().getClassLoader().getResourceAsStream(
                "/biz/isphere/core/swt/widgets/hexeditor/internal/ccsid.properties");
            ccsids.load(inStream);
            return ccsids;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        String ccsid = getSystemCharset();
        ccsids.put(ccsid, "*SYSTEM");

        return ccsids;
    }

    private String findCharset(Properties ccsids, String findValue) {

        Set<Entry<Object, Object>> entries = ccsids.entrySet();
        for (Entry<Object, Object> entry : entries) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (findValue.equals(value)) {
                return key;
            }
        }

        return null;
    }

    private String getSystemCharset() {
        return System.getProperty("file.encoding", "utf-8");
    }

    @Override
    public void setStatusMessage(String message) {
        statusMessage = message;
    }

    /**
     * Updates the status of actions: enables/disables them depending on whether
     * there is text selected and whether inserting or overwriting is active.
     * Undo/redo actions are enabled/disabled as well.
     */
    @Override
    public void updateActionsStatus() {

        boolean textSelected = dataAreaText != null && dataAreaText.isSelected();
        boolean lengthModifiable = textSelected && !dataAreaText.isOverwriteMode();

        IAction action;
        IActionBars bars = getEditorSite().getActionBars();

        action = bars.getGlobalActionHandler(ActionFactory.CUT.getId());
        if (action != null) {
            action.setEnabled(lengthModifiable);
        }

        action = bars.getGlobalActionHandler(ActionFactory.COPY.getId());
        if (action != null) {
            action.setEnabled(textSelected);
        }

        action = bars.getGlobalActionHandler(ActionFactory.PASTE.getId());
        if (action != null) {
            action.setEnabled(true);
        }

        action = bars.getGlobalActionHandler(ActionFactory.UNDO.getId());
        if (action != null) {
            action.setEnabled(dataAreaText != null && dataAreaText.canUndo());
        }

        action = bars.getGlobalActionHandler(ActionFactory.REDO.getId());
        if (action != null) {
            action.setEnabled(dataAreaText != null && dataAreaText.canRedo());
        }

        action = bars.getGlobalActionHandler(ActionFactory.DELETE.getId());
        if (action != null) {
            action.setEnabled(lengthModifiable);
        }

        action = bars.getGlobalActionHandler(ActionFactory.SELECT_ALL.getId());
        if (action != null) {
            action.setEnabled(true);
        }

        bars.updateActionBars();
    }

    @Override
    public void updateStatusLine() {

        StatusLine statusLine = getStatusLine();
        if (statusLine == null) {
            return;
        }

        statusLine.setShowMode(true);
        statusLine.setShowPosition(true);
        statusLine.setShowValue(true);
        statusLine.setShowMessage(true);

        if (statusLine != null) {
            statusLine.setMessage(statusMessage);
            statusMessage = null;
            if (dataAreaText != null) {
                if (dataAreaText.isSelected()) {
                    statusLine.setSelection(dataAreaText.getSelection(), dataAreaText.getActualValue());
                } else {
                    statusLine.setPosition(dataAreaText.getCaretPosition(), dataAreaText.getActualValue());
                }
                statusLine.setInsertMode(!dataAreaText.isOverwriteMode());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean isEnabled) {
        if (dataAreaText == null) {
            return;
        }
        dataAreaText.setEnabled(isEnabled);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate((int)dataAreaText.getContent().length());
            dataAreaText.getContent().get(buffer, 0);
            handleSaveResult(aMonitor, null);
        } catch (Throwable e) {
            handleSaveResult(aMonitor, e);
        }
    }

    @Override
    public void setInitialFocus() {
        if (dataAreaText == null) {
            return;
        }
        dataAreaText.setFocus();
    }

    /**
     * Returns the font used for the ruler, offset column and editor.
     * 
     * @return font for ruler, offset column and editor
     */
    private Font getEditorFont() {
        return FontHelper.getFixedSizeFont();
    }

    /**
     * Returns the CutAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return CutAction to override the original behavior
     */
    @Override
    public Action getCutAction() {
        if (cutAction == null) {
            cutAction = new EditorAction(dataAreaText, ActionFactory.CUT.getId());
        }
        return cutAction;
    }

    /**
     * Returns the CopyAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return CopyAction to override the original behavior
     */
    @Override
    public Action getCopyAction() {
        if (copyAction == null) {
            copyAction = new EditorAction(dataAreaText, ActionFactory.COPY.getId());
        }
        return copyAction;
    }

    /**
     * Returns the PasteAction that overrides the original action of the editor
     * widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return PasteAction to override the original behavior
     */
    @Override
    public Action getPasteAction() {
        if (pasteAction == null) {
            pasteAction = new EditorAction(dataAreaText, ActionFactory.PASTE.getId());
        }
        return pasteAction;
    }

    @Override
    public Action getUndoAction() {
        if (undoAction == null) {
            undoAction = new EditorAction(dataAreaText, ActionFactory.UNDO.getId());
        }
        return undoAction;
    }

    @Override
    public Action getRedoAction() {
        if (redoAction == null) {
            redoAction = new EditorAction(dataAreaText, ActionFactory.REDO.getId());
        }
        return redoAction;
    }

    @Override
    public Action getDeleteAction() {
        if (deleteAction == null) {
            deleteAction = new EditorAction(dataAreaText, ActionFactory.DELETE.getId());
        }
        return deleteAction;
    }

    @Override
    public Action getSelectAllAction() {
        if (selectAllAction == null) {
            selectAllAction = new EditorAction(dataAreaText, ActionFactory.SELECT_ALL.getId());
        }
        return selectAllAction;
    }

    /**
     * Returns the FindReplaceAction that overrides the original action of the
     * editor widget.
     * 
     * @param anEditorPart - the editor part, that contains this editor delegate
     * @return FindReplaceAction to override the original behavior
     */
    @Override
    public Action getFindReplaceAction(EditorPart anEditorPart) {
        if (findReplaceAction == null) {
            findReplaceAction = new FindReplaceAction(ResourceBundle.getBundle("org.eclipse.ui.texteditor.ConstructedTextEditorMessages"), null,
                anEditorPart);
        }
        return findReplaceAction;
    }

    /**
     * Returns whether a find operation can be performed.
     * 
     * @return whether a find operation can be performed
     */
    @Override
    public boolean canPerformFind() {
        return true;
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
    @Override
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {
        BinaryContentFinder.Match match = dataAreaText.findAndSelect(aWidgetOffset, aFindString, false, aSearchForward, aCaseSensitive);
        if (!match.isFound()) {
            return -1;
        }
        return (int)match.getStartPosition();
    }

    /**
     * Returns the currently selected range of characters as a offset and length
     * in widget coordinates.
     * 
     * @return the currently selected character range in widget coordinates
     */
    @Override
    public Point getSelection() {
        Point selection = dataAreaText.getSelection();
        return new Point(selection.x, selection.y - selection.x);
    }

    /**
     * Returns the currently selected characters as a string.
     * 
     * @return the currently selected characters
     */
    @Override
    public String getSelectionText() {
        return "";
    }

    /**
     * Returns whether this target can be modified.
     * 
     * @return <code>true</code> if target can be modified
     */
    @Override
    public boolean isEditable() {
        return dataAreaText.isEditable();
    }

    /**
     * Replaces the currently selected range of characters with the given text.
     * This target must be editable. Otherwise nothing happens.
     * 
     * @param aText - the substitution text
     */
    @Override
    public void replaceSelection(String aText) {
        return;
    }

    /**
     * Class, that listens for changes on the FontRegistry in order to change
     * the editor font, when the preferences are changed.
     */
    private class FontChangedListener implements IPropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (FontHelper.EDITOR_FIXED_SIZE.equals(event.getProperty())) {
                if (event.getNewValue() instanceof FontData[]) {
                    FontData[] fontDataArray = (FontData[])event.getNewValue();
                    changeFont(fontDataArray[0]);
                }
            }
        }

        private void changeFont(FontData aFontData) {
            Font font = SWTResourceManager.getFont(aFontData.getName(), aFontData.getHeight(), aFontData.getStyle());
            dataAreaText.setFont(font);
        }
    }

    private class EditorAction extends Action {
        private HexText myControl;
        private String myId;

        public EditorAction(HexText control, String id) {
            if (control == null) {
                throw new IllegalArgumentException("Parameter 'control' must not be null.");
            }
            if (id == null) {
                throw new IllegalArgumentException("Parameter 'id' must not be null."); //$NON-NLS-1$
            }
            this.myControl = control;
            this.myId = id;
        }

        @Override
        public void run() {
            if (myControl == null) {
                return;
            }

            if (myId.equals(ActionFactory.UNDO.getId())) {
                myControl.undo();
            } else if (myId.equals(ActionFactory.REDO.getId())) {
                myControl.redo();
            } else if (myId.equals(ActionFactory.CUT.getId())) {
                myControl.cut();
            } else if (myId.equals(ActionFactory.COPY.getId())) {
                myControl.copy();
            } else if (myId.equals(ActionFactory.PASTE.getId())) {
                myControl.paste();
            } else if (myId.equals(ActionFactory.DELETE.getId())) {
                myControl.deleteSelected();
            } else if (myId.equals(ActionFactory.SELECT_ALL.getId())) {
                myControl.selectAll();
            }
        }
    }
}
