/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataareaeditor;

import java.math.BigDecimal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Validator;
import biz.isphere.core.objecteditor.AbstractObjectEditorInput;

import com.ibm.as400.access.AS400;

public abstract class AbstractDataAreaEditor extends EditorPart {

    public static final String ID = "biz.isphere.rse.dataareaeditor.DataAreaEditor";

    private int DEFAULT_EDITOR_WIDTH = 50; // default width on 5250 screen

    private AbstractObjectEditorInput input;
    private DataAreaDelegate dataAreaDelegate;
    private StatusBar statusBar;
    private Control editorControl;
    private boolean isDirty;

    public AbstractDataAreaEditor() {
        isDirty = false;
    }

    @Override
    public void createPartControl(Composite aParent) {

        Composite editorParent = new Composite(aParent, SWT.NONE);
        GridLayout editorParentLayout = new GridLayout(2, false);
        editorParentLayout.marginTop = 5;
        editorParent.setLayout(editorParentLayout);

        Label headline = new Label(editorParent, SWT.NONE);
        GridData headlineLayoutData = new GridData();
        headlineLayoutData.horizontalAlignment = GridData.BEGINNING;
        headlineLayoutData.horizontalSpan = 2;
        headline.setLayoutData(headlineLayoutData);
        headline.setText(getHeadlineText());

        Label valueLabel = new Label(editorParent, SWT.NONE);
        valueLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
        valueLabel.setText(Messages.Value_colon);

        if (DataAreaDelegate.CHARACTER.equals(dataAreaDelegate.getType())) {
            editorControl = createCharacterPartControl(editorParent, dataAreaDelegate.getStringValue());
        } else if (DataAreaDelegate.DECIMAL.equals(dataAreaDelegate.getType())) {
            editorControl = createDecimalPartControl(editorParent, dataAreaDelegate.getDecimalValue());
        }
        if (DataAreaDelegate.LOGICAL.equals(dataAreaDelegate.getType())) {
            editorControl = createLogicalPartControl(editorParent, dataAreaDelegate.getBooleanValue());
        }

    }

    private Control createCharacterPartControl(Composite aParent, String aValue) {

        ScrolledComposite scrollable = new ScrolledComposite(aParent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrollable.setLayout(new GridLayout(1, false));
        scrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setExpandHorizontal(true);
        scrollable.setExpandVertical(true);

        Composite editorArea = createEditorArea(scrollable);
        scrollable.setContent(editorArea);

        Label ruler = new Label(editorArea, SWT.NONE);
        ruler.setFont(getFixedSizeFont());
        ruler.setText(getRulerText(DEFAULT_EDITOR_WIDTH));
        GridData rulerLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        rulerLayoutData.horizontalIndent = 3;
        ruler.setLayoutData(rulerLayoutData);

        DataAreaText dataAreaText = new DataAreaText(editorArea, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL, ruler.getText().length());
        dataAreaText.setTextLimit(dataAreaDelegate.getLength());
        dataAreaText.setFont(getFixedSizeFont());
        dataAreaText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Add status bar
        statusBar = createStatusBar(aParent, 1, 1);

        // Add 'caret' listener
        addCaretListener(dataAreaText);

        // Set screen value
        dataAreaText.setText(aValue);

        // Add 'dirty' listener
        dataAreaText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                setDirty(true);
            }
        });

        return dataAreaText;
    }

    private Control createDecimalPartControl(Composite aParent, BigDecimal aValue) {

        Composite editorParent = createEditorArea(aParent);

        StyledText dataAreaText = new StyledText(editorParent, SWT.BORDER);
        dataAreaText.addVerifyListener(new NumericOnlyVerifyListener(true));
        dataAreaText.setTextLimit(dataAreaDelegate.getTextLimit());
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        dataAreaTextLayoutData.widthHint = 160;
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite filler = new Composite(aParent, SWT.NONE);
        GridData fillerLayoutData = new GridData();
        fillerLayoutData.grabExcessHorizontalSpace = true;
        fillerLayoutData.grabExcessVerticalSpace = true;
        fillerLayoutData.horizontalSpan = 2;
        filler.setLayoutData(fillerLayoutData);

        final Validator validator = new Validator();
        validator.setType("*DEC");
        validator.setLength(dataAreaDelegate.getLength());
        validator.setPrecision(dataAreaDelegate.getDecimalPositions());

        // Add status bar
        statusBar = createStatusBar(aParent);

        // Set screen value
        dataAreaText.setText(aValue.toString());

        // Add 'dirty' listener
        dataAreaText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent anEvent) {
                setDirty(true);
                StyledText textControl = (StyledText)anEvent.getSource();
                if (!validator.validate(textControl.getText().replaceAll(",", "."))) {
                    statusBar.setMessage(Messages.Length_or_number_of_decimal_digits_on_value_not_valid);
                } else {
                    statusBar.setMessage("");
                }
            }
        });

        return dataAreaText;
    }

    private Control createLogicalPartControl(Composite aParent, Boolean aValue) {

        Composite editorArea = createEditorArea(aParent);

        final Button dataAreaText = new Button(editorArea, SWT.CHECK);
        GridData dataAreaTextLayoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        dataAreaTextLayoutData.widthHint = 160;
        dataAreaText.setLayoutData(dataAreaTextLayoutData);

        Composite filler = new Composite(aParent, SWT.NONE);
        GridData fillerLayoutData = new GridData();
        fillerLayoutData.grabExcessHorizontalSpace = true;
        fillerLayoutData.grabExcessVerticalSpace = true;
        fillerLayoutData.horizontalSpan = 2;
        filler.setLayoutData(fillerLayoutData);

        // Add status bar
        statusBar = createStatusBar(aParent);

        // Set screen value
        dataAreaText.setSelection(aValue);

        // Add 'dirty' listener
        dataAreaText.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent arg0) {
            }

            public void widgetSelected(SelectionEvent arg0) {
                setDirty(true);
            }
        });

        return dataAreaText;
    }

    private Composite createEditorArea(Composite aParent) {
        Composite editorArea = new Composite(aParent, SWT.NONE);
        GridLayout editorAreaLayout = new GridLayout(1, false);
        editorAreaLayout.marginWidth = 10;
        editorAreaLayout.marginTop = 10;
        editorAreaLayout.marginLeft = 10;
        editorAreaLayout.marginRight = 10;
        editorAreaLayout.marginBottom = 20;
        editorArea.setLayout(editorAreaLayout);
        return editorArea;
    }

    private String getHeadlineText() {
        String text = dataAreaDelegate.getText();
        if (StringHelper.isNullOrEmpty(text)) {
            text = ":";
        }
        if (!text.endsWith(":")) {
            text = text + ":";
        }
        return text;
    }

    private String getRulerText(int aLength) {
        return "*...+....1....+....2....+....3....+....4....+....5".substring(0, aLength);
    }

    private Font getFixedSizeFont() {
        return SWTResourceManager.getFont("Courier New", 11, SWT.NORMAL);
    }

    private void setDirty(boolean anIsDirty) {
        isDirty = anIsDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public void doSave(IProgressMonitor aMonitor) {

        if (DataAreaDelegate.CHARACTER.equals(dataAreaDelegate.getType())) {
            doSaveCharacterData(aMonitor);
        } else if (DataAreaDelegate.DECIMAL.equals(dataAreaDelegate.getType())) {
            doSaveDecimalData(aMonitor);
        }
        if (DataAreaDelegate.LOGICAL.equals(dataAreaDelegate.getType())) {
            doSaveBooleanData(aMonitor);
        }

    }

    private void doSaveCharacterData(IProgressMonitor aMonitor) {
        String value = ((DataAreaText)editorControl).getText();
        Throwable exception = dataAreaDelegate.setValue(value);
        handleSaveException(aMonitor, exception);
    }

    private void doSaveDecimalData(IProgressMonitor aMonitor) {
        String value = ((StyledText)editorControl).getText();
        Throwable exception = dataAreaDelegate.setValue(new BigDecimal(value));
        handleSaveException(aMonitor, exception);
    }

    private void doSaveBooleanData(IProgressMonitor aMonitor) {
        boolean value = ((Button)editorControl).getSelection();
        Throwable exception = dataAreaDelegate.setValue(value);
        handleSaveException(aMonitor, exception);
    }

    private void handleSaveException(IProgressMonitor aMonitor, Throwable anException) {
        if (anException != null) {
            statusBar.setMessage("ERROR: " + anException.getLocalizedMessage());
            aMonitor.setCanceled(true);
        } else {
            setDirty(false);
        }
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public void init(IEditorSite aSite, IEditorInput anInput) throws PartInitException {
        setSite(aSite);
        setInput(anInput);
        setPartName(anInput.getName());
        setTitleImage(((DataAreaEditorInput)anInput).getTitleImage());
        input = (AbstractObjectEditorInput)anInput;
        dataAreaDelegate = new DataAreaDelegate(input.getAS400(), input.getObjectLibrary(), input.getObjectName());
    }

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
    }

    public static void openEditor(AS400 anAS400, String aConnection, String aLibrary, String aDataArea, String aMode) {

        try {

            DataAreaEditorInput editorInput = new DataAreaEditorInput(anAS400, aConnection, aLibrary, aDataArea, aMode);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, AbstractDataAreaEditor.ID);

        } catch (PartInitException e) {
        }

    }

    protected abstract void addCaretListener(DataAreaText aTextControl);

    protected abstract StatusBar createStatusBar(Composite aParent);

    protected abstract StatusBar createStatusBar(Composite aParent, int aRow, int aColumn);

}
