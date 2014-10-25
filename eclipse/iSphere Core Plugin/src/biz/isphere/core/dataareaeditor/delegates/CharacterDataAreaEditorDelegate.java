/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor.delegates;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.FindReplaceAction;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataareaeditor.DataAreaEditor;
import biz.isphere.core.dataareaeditor.controls.DataAreaText;
import biz.isphere.core.dataareaeditor.events.StatusChangedEvent;
import biz.isphere.core.dataareaeditor.events.StatusChangedListener;
import biz.isphere.core.internal.FontHelper;

/**
 * Editor delegate that edits a *CHAR data area.
 * <p>
 * The delegate implements all the specific stuff that is needed to edit a data
 * area of type *CHAR. Basically it implements these things:
 * 
 * <pre>
 * The content of the data area is displayed in lines with a fixed length
 * Cut & Paste actions had to be overridden to handle these action properly
 * The delegate uses to original Eclipse Find & Replace dialog for Find & Replace action
 * The delegate (and the DataAreaText widget) uses the FontRegistry and the ColorRegistry to paint the editor
 * </pre>
 */
public class CharacterDataAreaEditorDelegate extends AbstractDataAreaEditorDelegate {

    protected static int DEFAULT_EDITOR_WIDTH = 50; // default width on 5250
                                                    // screen
    private DataAreaText dataAreaText;

    private Action cutAction;
    private Action pasteAction;
    private Action findReplaceAction;

    private Composite editorArea;
    private ScrolledComposite editorAreaScrollable;
    private GridData dataAreaTextLayoutData;
    private Label ruler;

    public CharacterDataAreaEditorDelegate(DataAreaEditor aDataAreaEditor) {
        super(aDataAreaEditor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite aParent) {

        FontRegistry registry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getFontRegistry();
        registry.addListener(new ThemeChangedListener());

        aParent.addPaintListener(new ParentPaintListener());

        editorAreaScrollable = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.NONE);
        editorAreaScrollable.setLayout(new GridLayout(1, false));
        editorAreaScrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        editorAreaScrollable.setExpandHorizontal(true);
        editorAreaScrollable.setExpandVertical(true);

        editorArea = createEditorArea(editorAreaScrollable, 1);

        Composite rulerArea = new Composite(editorArea, SWT.NONE);
        GridLayout rulerAreaLayout = new GridLayout(1, false);
        rulerAreaLayout.marginTop = 5;
        rulerAreaLayout.marginBottom = 0;
        rulerAreaLayout.marginWidth = 0;
        rulerAreaLayout.verticalSpacing = 0;
        rulerArea.setLayout(rulerAreaLayout);
        GridData rulerAreaLayoutData = createRulerLayoutData();
        rulerArea.setLayoutData(rulerAreaLayoutData);

        ruler = new Label(rulerArea, SWT.NONE);
        ruler.setFont(FontHelper.getFixedSizeFont());
        ruler.setText(getRulerText(DEFAULT_EDITOR_WIDTH));
        GridData rulerLayoutData = new GridData();
        rulerLayoutData.horizontalIndent = 5;
        ruler.setLayoutData(rulerLayoutData);

        dataAreaText = new DataAreaText(rulerArea, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL, ruler.getText().length());
        dataAreaText.setTextLimit(getWrappedDataArea().getLength());
        dataAreaText.setFont(ruler.getFont());
        dataAreaTextLayoutData = createRulerLayoutData();
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        // Set screen value
        dataAreaText.setText(getWrappedDataArea().getStringValue());

        // Add 'status changed' listener
        dataAreaText.addStatusChangedListener(new StatusChangedListener() {
            public void statusChanged(StatusChangedEvent anEvent) {
                if (anEvent.dirty) {
                    setEditorDirty();
                }
                getStatusBar().setPosition(anEvent.position);
                if (anEvent.insertMode) {
                    getStatusBar().setInfo(Messages.Mode_Insert);
                } else {
                    getStatusBar().setInfo(Messages.Mode_Overwrite);
                }
            }
        });

        editorAreaScrollable.setContent(editorArea);
        editorAreaScrollable.setMinSize(editorArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(IProgressMonitor aMonitor) {
        Throwable exception = getWrappedDataArea().setValue(dataAreaText.getText());
        handleSaveResult(aMonitor, exception);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetDirtyFlag() {
        super.resetDirtyFlag();
        dataAreaText.resetDirtyFlag();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitialFocus() {
        dataAreaText.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action getCutAction() {
        if (cutAction == null) {
            cutAction = new CutAction();
        }
        return cutAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action getPasteAction() {
        if (pasteAction == null) {
            pasteAction = new PasteAction();
        }
        return pasteAction;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean canPerformFind() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int findAndSelect(int aWidgetOffset, String aFindString, boolean aSearchForward, boolean aCaseSensitive, boolean aWholeWord) {

        String findRegEx;
        String text;
        if (aSearchForward) {
            findRegEx = aFindString;
            text = dataAreaText.getText();
        } else {
            findRegEx = StringHelper.reverse(aFindString);
            text = StringHelper.reverse(dataAreaText.getText());
        }

        if (aWholeWord) {
            findRegEx = "\\b" + findRegEx + "\\b";
        }

        int offset;
        if (aWidgetOffset < 0) {
            // wrap
            offset = 0;
        } else {
            if (aSearchForward) {
                offset = aWidgetOffset;
            } else {
                offset = text.length() - aWidgetOffset - 1;
            }
        }

        int flags = 0;
        if (!aCaseSensitive) {
            flags = flags | Pattern.CASE_INSENSITIVE;
        }

        int index;
        Pattern pattern = Pattern.compile(findRegEx, flags);
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find(offset)) {
            return -1;
        }

        index = matcher.start();
        if (aSearchForward) {
            dataAreaText.setSelection(index, index + aFindString.length());
            return index;
        } else {
            index = dataAreaText.getText().length() - index - aFindString.length();
            dataAreaText.setSelection(index, index + aFindString.length());
            return index;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Point getSelection() {
        int start = dataAreaText.getSelection().x;
        int length = dataAreaText.getSelection().y - start;
        Point selection = new Point(start, length);
        return selection;
    }

    /**
     * {@inheritDoc}
     */
    public String getSelectionText() {
        return dataAreaText.getSelectionText();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return dataAreaText.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void replaceSelection(String aText) {
        dataAreaText.replaceTextRange(getSelection(), aText);
    }

    private GridData createRulerLayoutData() {
        GridData rulerAreaLayoutData = new GridData();
        rulerAreaLayoutData.horizontalAlignment = GridData.FILL;
        rulerAreaLayoutData.grabExcessHorizontalSpace = false;
        /*
         * for the vertical alignment (height), see: ParentPaintListener
         */
        return rulerAreaLayoutData;
    }

    /**
     * Returns the ruler text.
     * 
     * @param aLength - line length in characters
     * @return ruler text
     */
    private String getRulerText(int aLength) {
        StringBuilder ruler = new StringBuilder();
        int tensDigit = 0;
        for (int i = 1; i <= aLength; i++) {
            if (i < 5) {
                ruler.append(".");
            } else if ((i - 1) % 10 == 0) {
                ruler.append(" ");
            } else if (i % 10 == 0) {
                tensDigit++;
                ruler.append(tensDigit);
            } else if ((i + 1) % 10 == 0) {
                ruler.append(" ");
            } else if (i % 5 == 0) {
                ruler.append("+");
            } else {
                ruler.append(".");
            }
        }
        return ruler.toString();
    }

    /**
     * Repaints the editor area (<i>see: dataAreaText</i>) composite.
     */
    private void layoutEditorArea() {
        editorArea.layout();
        editorAreaScrollable.setMinSize(editorArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Class, that overrides the original "paste" action of the SWT Text widget.
     */
    private class PasteAction extends Action {
        @Override
        public void run() {
            if (dataAreaText.hasFocus()) {
                String text = getClipboardText();
                if (StringHelper.isNullOrEmpty(text)) {
                    return;
                }
                replaceSelection(text);
            }
        }
    };

    /**
     * Class, that overrides the original "cut" action of the SWT Text widget.
     */
    private class CutAction extends Action {
        @Override
        public void run() {
            if (dataAreaText.hasFocus()) {
                String text = getSelectionText();
                if (StringHelper.isNullOrEmpty(text)) {
                    return;
                }
                setClipboardText(text);
                replaceSelection("");
            }
        }
    }

    /**
     * Class, used to resize the editor area (<i>see: dataAreaText</i>) when the
     * editor part is resized.
     */
    private class ParentPaintListener implements PaintListener {
        public void paintControl(PaintEvent event) {
            Composite composite = (Composite)event.getSource();
            dataAreaTextLayoutData.heightHint = composite.getClientArea().height - 160;
            layoutEditorArea();
        }
    }

    /**
     * Class, that listens for changes on the FontRegistry in order to change
     * the editor font, when the preferences are changed.
     */
    private class ThemeChangedListener implements IPropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (FontHelper.EDITOR_FIXED_SIZE.equals(event.getProperty())) {
                if (event.getNewValue() instanceof FontData[]) {
                    FontData[] fonts = (FontData[])event.getNewValue();
                    changeFont(fonts[0]);
                }
            }
        }

        private void changeFont(FontData aFontData) {
            Font font = SWTResourceManager.getFont(aFontData.getName(), aFontData.getHeight(), aFontData.getStyle());
            ruler.setFont(font);
            dataAreaText.setFont(font);
            layoutEditorArea();
        }
    }

}
