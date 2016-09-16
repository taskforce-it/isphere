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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.IFindReplaceTarget;
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

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditor.AbstractDataSpaceEditor;
import biz.isphere.core.dataspaceeditor.StatusLine;
import biz.isphere.core.dataspaceeditor.dialog.GoToDialog;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.preferences.DoNotAskMeAgain;
import biz.isphere.core.preferences.DoNotAskMeAgainDialog;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.hexeditor.HexText;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContent;
import biz.isphere.core.swt.widgets.hexeditor.internal.BinaryContentFinder;

/**
 * Editor delegate that edits a data space.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *LGL.
 */
public class HexDataSpaceEditorDelegate extends AbstractDataSpaceEditorDelegate {

    private HexText dataAreaText;

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
                updateStatusLine();
            }
        });

        dataAreaText.addLongSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateStatusLine();
                updateActionStatus();
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

    public void updateActionStatus() {

        updateActionStatus(ActionFactory.CUT.getId());
        updateActionStatus(ActionFactory.COPY.getId());
        updateActionStatus(ActionFactory.PASTE.getId());
        updateActionStatus(ActionFactory.DELETE.getId());
        updateActionStatus(ActionFactory.UNDO.getId());
        updateActionStatus(ActionFactory.REDO.getId());
        updateActionStatus(ActionFactory.SELECT_ALL.getId());

        IActionBars actionBars = getEditorSite().getActionBars();
        actionBars.updateActionBars();
    }

    public void updateActionStatus(String actionID) {

        IActionBars actionBars = getEditorSite().getActionBars();
        IAction action = actionBars.getGlobalActionHandler(actionID);
        if (action != null) {
            action.setEnabled(action.isEnabled());
        }
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
    public boolean canGoTo() {
        return true;
    }

    @Override
    public void doGoTo() {
        GoToDialog dialog = new GoToDialog(getShell(), dataAreaText);
        dialog.open();
    }

    @Override
    public boolean canCut() {
        return isHasSelectedTextAndIsModifiable();
    }

    @Override
    public void doCut() {
        dataAreaText.cut();
    }

    @Override
    public boolean canCopy() {
        return hasSelectedText();
    }

    @Override
    public void doCopy() {
        dataAreaText.copy();
    }

    @Override
    public boolean canPaste() {
        return true;
    }

    @Override
    public void doPaste() {
        dataAreaText.paste();
    }

    @Override
    public boolean canDelete() {
        return isHasSelectedTextAndIsModifiable();
    }

    @Override
    public void doDelete() {
        dataAreaText.deleteSelected();
    }

    @Override
    public boolean canSelectAll() {
        return true;
    }

    @Override
    public void doSelectAll() {
        dataAreaText.selectAll();
    }

    @Override
    public boolean canRedo() {
        return dataAreaText.canRedo();
    }

    @Override
    public void doRedo() {
        dataAreaText.redo();
    }

    @Override
    public boolean canUndo() {
        return dataAreaText.canUndo();
    }

    @Override
    public void doUndo() {
        dataAreaText.undo();
    }

    @Override
    public void setInitialFocus() {
        if (dataAreaText == null) {
            return;
        }
        dataAreaText.setFocus();
    }

    private boolean isHasSelectedTextAndIsModifiable() {
        return hasSelectedText() && !isOverwriteMode();
    }

    private boolean hasSelectedText() {
        if (getSelection().y <= 0) {
            return false;
        }
        return true;
    }

    private boolean isOverwriteMode() {
        return dataAreaText.isOverwriteMode();
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
                anEditorPart) {

                public void run() {
                    displayInformationalMessage();
                    super.run();
                }

                public void runWithEvent(Event event) {
                    displayInformationalMessage();
                    super.runWithEvent(event);
                };

                private void displayInformationalMessage() {
                    DoNotAskMeAgainDialog.openInformation(getShell(), DoNotAskMeAgain.INFORMATION_DATA_SPACE_FIND_REPLACE_INFORMATION,
                        Messages.Data_Space_Hex_Editor_search_and_replace_information);
                };
            };
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
     * @return the position of the specified string, or -1 if the string has not
     *         been found
     * @see IFindReplaceTarget
     */
    @Override
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {

        String tempFindString;
        boolean isHexString = isHexString(aFindString);
        if (isHexString) {
            tempFindString = getHexString(aFindString);
        } else {
            tempFindString = aFindString;
        }

        BinaryContentFinder.Match match;
        try {

            match = dataAreaText.findAndSelect(aWidgetOffset, tempFindString, isHexString, aSearchForward, aCaseSensitive);
            if (!match.isFound()) {
                return -1;
            }

            return (int)match.getStartPosition();

        } catch (Throwable e) {
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, ExceptionHelper.getLocalizedMessage(e));
            return -1;
        }
    }

    private boolean isHexString(String aFindString) {

        if (getHexString(aFindString) == null) {
            return false;
        }

        return true;
    }

    private String getHexString(String text) {

        if (text == null || text.length() == 0) {
            return null;
        }

        if (text.startsWith("x'") && text.endsWith("'") && (text.length() - 3) % 2 == 0) {
            return text.substring(2, 2 + text.length() - 3);
        }

        if (text.startsWith("x") && (text.length() - 1) % 2 == 0) {
            return text.substring(1);
        }

        if (text.startsWith("0x") && (text.length() - 2) % 2 == 0) {
            return text.substring(2);
        }

        return null;
    }

    /**
     * Returns the currently selected range of characters as a offset and length
     * in widget coordinates.
     * 
     * @return the currently selected character range in widget coordinates
     */
    @Override
    public Point getSelection() {

        if (dataAreaText == null) {
            return new Point(-1, -1);
        }

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
     * Returns whether override mode is enabled.
     * 
     * @return <code>true</code> if override mode is enabled
     */
    @Override
    public boolean isOverrideMode() {
        return dataAreaText.isOverwriteMode();
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
}
