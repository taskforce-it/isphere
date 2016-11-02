/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue.viewer;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.ByteHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.FontHelper;
import biz.isphere.core.internal.Size;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class SendMessageDialog extends XDialog {

    private static final String HEX_MODE_KEY = "HEX_MODE_KEY"; //$NON-NLS-1$
    private static final String HEX_MODE_DATA = "HEX_MODE_DATA"; //$NON-NLS-1$
    private static final String SASH_WEIGHTS_1 = "SASH_WEIGHTS_1"; //$NON-NLS-1$
    private static final String SASH_WEIGHTS_2 = "SASH_WEIGHTS_2"; //$NON-NLS-1$

    private String dataQueue;
    private String library;
    private boolean displayKeyInputField;
    private int keyLen;
    private int maxDataLen;

    private Text textKey;
    private Button checkboxKeyHexMode;

    private Text textData;
    private Button checkboxDataHexMode;

    private String key;
    private boolean isHexKey;

    private String data;
    private boolean isHexData;

    private SashForm sashForm;
    private int[] sashWeights;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public SendMessageDialog(Shell parent, String dataQueue, String library, boolean displayKeyInputField, int keyLen, int maxDataLen) {
        super(parent);

        this.dataQueue = dataQueue;
        this.library = library;
        this.displayKeyInputField = displayKeyInputField;
        this.keyLen = keyLen;
        this.maxDataLen = maxDataLen;

        this.sashWeights = new int[] { 2, 4 };
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Send_Data_Queue_Entry);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(1, false));
        mainArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        createHeader(mainArea);

        createMain(mainArea);

        createStatusLine(mainArea);

        loadScreenValues();

        return mainArea;
    }

    private void createMain(Composite parent) {

        sashForm = new SashForm(parent, SWT.VERTICAL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (displayKeyInputField) {
            createKeyInputField(sashForm);
        }

        createDataInputField(sashForm);

        if (displayKeyInputField) {
            sashForm.setWeights(sashWeights);
        }
    }

    private void createHeader(Composite parent) {

        Composite labelArea = new Composite(parent, SWT.NONE);
        GridLayout layout = createGridLayoutSimple(4);
        labelArea.setLayout(layout);
        labelArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

        createLabel(labelArea, Messages.Object_colon, dataQueue);
        createLabel(labelArea, Messages.Library_colon, library);
    }

    private Label createLabel(Composite parent, String label, String value) {

        Label labelLabel = new Label(parent, SWT.NONE);
        labelLabel.setText(label);

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value);

        return valueLabel;
    }

    private GridLayout createGridLayoutSimple(int columns) {

        GridLayout layout = new GridLayout(columns, false);
        layout.marginHeight = 5;
        layout.horizontalSpacing = 10;

        return layout;
    }

    private void createKeyInputField(Composite parent) {

        Composite inputArea = new Composite(parent, SWT.NONE);
        inputArea.setLayout(new GridLayout(2, false));
        inputArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label textKeyLabel = new Label(inputArea, SWT.NONE);
        textKeyLabel.setText(Messages.Key_colon);
        textKeyLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        textKey = WidgetFactory.createMultilineText(inputArea, false, false);
        textKey.setFont(getEditorFont());
        GridData dataAreaTextLayoutData = new GridData(GridData.FILL_BOTH);
        textKey.setLayoutData(dataAreaTextLayoutData);

        new Label(inputArea, SWT.NONE).setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false)); // Dummy

        checkboxKeyHexMode = WidgetFactory.createCheckbox(inputArea);
        checkboxKeyHexMode.setText(Messages.Hex_key_input);
        checkboxKeyHexMode.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Point selection = textKey.getSelection();
                textKey.setFocus();
                if (selection != null) {
                    textKey.setSelection(selection);
                }
            }
        });

        textKey.addVerifyListener(new DataInputVerifier(checkboxKeyHexMode));
    }

    private void createDataInputField(Composite parent) {

        Composite inputArea = new Composite(parent, SWT.NONE);
        inputArea.setLayout(new GridLayout(2, false));
        inputArea.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label textDataLabel = new Label(inputArea, SWT.NONE);
        textDataLabel.setText(Messages.Data_colon);
        textDataLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

        textData = WidgetFactory.createMultilineText(inputArea, false, false);
        textData.setFont(getEditorFont());
        GridData dataAreaTextLayoutData = new GridData(GridData.FILL_BOTH);
        textData.setLayoutData(dataAreaTextLayoutData);

        new Label(inputArea, SWT.NONE).setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false)); // Dummy

        checkboxDataHexMode = WidgetFactory.createCheckbox(inputArea);
        checkboxDataHexMode.setText(Messages.Hex_data_input);
        checkboxDataHexMode.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Point selection = textData.getSelection();
                textData.setFocus();
                if (selection != null) {
                    textData.setSelection(selection);
                }
            }
        });

        textData.addVerifyListener(new DataInputVerifier(checkboxDataHexMode));
    }

    @Override
    protected void okPressed() {

        if (!validated(textKey, checkboxKeyHexMode.getSelection(), keyLen, true)) {
            return;
        }

        if (!validated(textData, checkboxDataHexMode.getSelection(), maxDataLen, false)) {
            return;
        }

        data = textData.getText();
        isHexData = checkboxDataHexMode.getSelection();

        if (displayKeyInputField) {
            key = textKey.getText();
            isHexKey = checkboxKeyHexMode.getSelection();
        }

        super.okPressed();
    }

    public boolean validated(Text data, boolean isHexData, int maxLength, boolean fixed) {

        int length;

        if (isHexData) {

            if (data.getText().length() % 2 != 0) {
                setErrorMessage(Messages.Invalid_length_Length_must_be_a_multiple_of_2_for_hex_data);
                data.setFocus();
                return false;
            }

            if (!data.getText().matches("[0-9a-fA-F]+")) { //$NON-NLS-1$
                setErrorMessage(Messages.Invalid_characters_Only_characters_0_9_and_A_F_are_allowed_for_hex_data);
                data.setFocus();
            }

            length = data.getText().length() / 2;
        } else {
            length = data.getText().length();
        }

        if (length > maxLength) {
            setErrorMessage(Messages.bind(Messages.Invalid_length_Length_must_not_exceed_A_characters, new Integer(maxLength)));
            data.setFocus();
            return false;
        }

        if (fixed) {
            if (length != maxLength) {
                setErrorMessage(Messages.bind(Messages.Invalid_length_Length_must_exxactly_match_A_characters, new Integer(maxLength)));
                data.setFocus();
                return false;
            }
        } else {
            if (maxLength > 0 && length <= 0) {
                setErrorMessage(Messages.Invalid_or_missing_value);
                data.setFocus();
                return false;
            }
        }

        setErrorMessage(null);

        return true;
    }

    public Object getKey() {

        if (!displayKeyInputField) {
            return null;
        }

        if (isHexKey) {
            return ByteHelper.getByteArray(key);
        } else {
            return key;
        }
    }

    public Object getData() {

        if (isHexData) {
            return ByteHelper.getByteArray(data);
        } else {
            return data;
        }
    }

    private void refreshButtonEnablement() {

    }

    private void loadScreenValues() {

        checkboxKeyHexMode.setSelection(loadBooleanValue(HEX_MODE_KEY, false));
        checkboxDataHexMode.setSelection(loadBooleanValue(HEX_MODE_DATA, false));

        sashWeights[0] = loadIntValue(SASH_WEIGHTS_1, 2);
        sashWeights[1] = loadIntValue(SASH_WEIGHTS_2, 4);

        if (displayKeyInputField) {
            sashForm.setWeights(sashWeights);
        }

        refreshButtonEnablement();
    }

    private void storeScreenValues() {

        storeValue(HEX_MODE_KEY, checkboxKeyHexMode.getSelection());
        storeValue(HEX_MODE_DATA, checkboxDataHexMode.getSelection());

        sashWeights = sashForm.getWeights();
        storeValue(SASH_WEIGHTS_1, sashWeights[0]);
        storeValue(SASH_WEIGHTS_2, sashWeights[1]);
    }

    /**
     * Returns the font used for the ruler, offset column and editor.
     * 
     * @return font for ruler, offset column and editor
     */
    private Font getEditorFont() {
        return FontHelper.getFixedSizeFont();
    }

    @Override
    public boolean close() {

        storeScreenValues();

        return super.close();
    }

    /**
     * Overridden to make this dialog resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(Size.getSize(400), Size.getSize(300), true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class DataInputVerifier implements VerifyListener {

        private Button checkboxHexMode;

        public DataInputVerifier(Button buttonHexMode) {
            this.checkboxHexMode = buttonHexMode;
        }

        public void verifyText(VerifyEvent event) {

            if (!checkboxHexMode.getSelection()) {
                return;
            }

            if (event.text == null || event.text.length() == 0) {
                return;
            }

            event.character = (char)event.text.toUpperCase().getBytes()[0];

            if ((event.character >= 'A' && event.character <= 'F') || (event.character >= '0' && event.character <= '9')) {
                event.doit = true;
            } else {
                event.doit = false;
            }
        }

    }

}
