/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor.dialog;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.internal.IntHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.base.swt.widgets.HexOnlyVerifyListener;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class GoToDialog extends XDialog {

    private static final String HEX_PREFIX = "0x"; //$NON-NLS-1$

    private static int GOTO_ID = 0;
    private static int CANCEL_ID = 1;
    private static int SHOW_LOCATION_ID = 2;

    public static String INPUT_FORMAT = "INPUT_FORMAT"; //$NON-NLS-1$
    public static String INPUT_FORMAT_HEX = "hex"; //$NON-NLS-1$
    public static String INPUT_FORMAT_DEC = "dec"; //$NON-NLS-1$

    private IGoToTarget gotoTarget;
    private long limit;
    private NumericOnlyVerifyListener numericVerifyListener;
    private HexOnlyVerifyListener hexVerifyListener;

    private Composite mainArea;
    private Label headline;
    private Text textLocation;
    private Button radioButtonHex;
    private Button radioButtonDecimal;
    private StatusLineManager statusLineManager;

    private int validLocation;
    private int actualLocation;

    public GoToDialog(Shell parentShell, IGoToTarget targetControl) {
        super(parentShell);
        this.gotoTarget = targetControl;
        this.limit = targetControl.getContentLength() - 1;
        this.numericVerifyListener = new NumericOnlyVerifyListener();
        this.hexVerifyListener = new HexOnlyVerifyListener();
        this.actualLocation = -1;
    }

    /**
     * Overridden to set the window title.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Go_to_location_headline);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        mainArea = new Composite(parent, SWT.NONE);
        mainArea.setLayout(new GridLayout(2, false));
        mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        headline = new Label(mainArea, SWT.NONE);
        headline.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 2, 1));

        Group radioButtonGroup = new Group(mainArea, SWT.NONE);
        radioButtonGroup.setLayout(new GridLayout(2, false));
        radioButtonGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        radioButtonHex = WidgetFactory.createRadioButton(radioButtonGroup);
        radioButtonHex.setText(Messages.Hex_label);
        radioButtonHex.addSelectionListener(new InputFormatSelectionListener());

        textLocation = WidgetFactory.createText(radioButtonGroup);
        textLocation.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 1, 2));
        textLocation.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent paramModifyEvent) {

                if (radioButtonHex.getSelection()) {
                    actualLocation = IntHelper.tryParseHex(textLocation.getText(), -1);
                } else {
                    actualLocation = IntHelper.tryParseInt(textLocation.getText(), -1);
                }

                if (textLocation.getText().length() > 0 && (actualLocation == -1 || actualLocation > limit)) {
                    setErrorMessage(Messages.Location_is_out_of_range_message);
                } else {
                    setErrorMessage(null);
                    if (actualLocation >= 0) {
                        validLocation = actualLocation;
                    }
                }

                setButtonEnablement();
            }
        });

        radioButtonDecimal = WidgetFactory.createRadioButton(radioButtonGroup);
        radioButtonDecimal.setText(Messages.Decimal_label);
        radioButtonDecimal.addSelectionListener(new InputFormatSelectionListener());

        createStatusLine(mainArea);

        return mainArea;
    }

    private void loadScreenValues() {

        if (INPUT_FORMAT_DEC.equals(getDialogBoundsSettings().get(INPUT_FORMAT))) {
            radioButtonDecimal.setSelection(true);
        } else {
            radioButtonHex.setSelection(true);
        }

        setLocationListeners();
    }

    private void storeScreenValues() {
        if (radioButtonDecimal.getSelection()) {
            getDialogBoundsSettings().put(INPUT_FORMAT, INPUT_FORMAT_DEC);
        } else {
            getDialogBoundsSettings().put(INPUT_FORMAT, INPUT_FORMAT_HEX);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, GOTO_ID, Messages.Go_to_location_label, true);
        createButton(parent, SHOW_LOCATION_ID, Messages.Show_location_label, false);
        createButton(parent, CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public void setEditor(IGoToTarget editor) {
        this.gotoTarget = editor;
    }

    @Override
    public void create() {
        super.create();

        loadScreenValues();
        setButtonEnablement();

        textLocation.setFocus();
    }

    @Override
    protected void buttonPressed(int buttonId) {

        if (buttonId == CANCEL_ID) {
            setReturnCode(CANCEL_ID);
            close();
        } else if (buttonId == GOTO_ID) {
            gotoTarget.selectBlock(getLocation(), getLocation());
            setReturnCode(GOTO_ID);
            close();
        } else if (buttonId == SHOW_LOCATION_ID) {
            gotoTarget.showMark(getLocation());
            setReturnCode(SHOW_LOCATION_ID);
            close();
        }

    }

    @Override
    public boolean close() {
        storeScreenValues();
        return super.close();
    }

    private int getLocation() {
        return validLocation;
    }

    private void setButtonEnablement() {

        if (!StringHelper.isNullOrEmpty(textLocation.getText()) && actualLocation >= 0) {
            setButtonEnablement(getButton(GOTO_ID), true);
            setButtonEnablement(getButton(SHOW_LOCATION_ID), true);
        } else {
            setButtonEnablement(getButton(GOTO_ID), false);
            setButtonEnablement(getButton(SHOW_LOCATION_ID), false);
        }
    }

    private void setButtonEnablement(Button button, boolean enabled) {

        if (button == null || button.isDisposed()) {
            return;
        }

        button.setEnabled(enabled);
    }

    private void setLocationListeners() {
        if (radioButtonDecimal.getSelection()) {
            textLocation.removeVerifyListener(hexVerifyListener);
            textLocation.addVerifyListener(numericVerifyListener);
        } else {
            textLocation.removeVerifyListener(numericVerifyListener);
            textLocation.addVerifyListener(hexVerifyListener);
        }
        headline.setText(getHeadline(0, limit));
        headline.update();
    }

    private String getHeadline(long start, long end) {
        return Messages.bind(Messages.Enter_location_number_between_A_and_B, new String[] { getLocationValue(start), getLocationValue(end) });
    }

    private String getLocationValue(long value) {

        if (radioButtonDecimal.getSelection()) {
            return Long.toString(value);
        }

        String text = Long.toHexString(value).toUpperCase();
        if (text.length() % 2 == 1) {
            text = "0" + text; //$NON-NLS-1$
        }
        return HEX_PREFIX + text;
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }

    private class InputFormatSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {
            setLocationListeners();
            textLocation.setFocus();

            if (actualLocation < 0) {
                textLocation.setText(Messages.EMPTY);
            } else {
                if (radioButtonDecimal.getSelection()) {
                    textLocation.setText(Integer.toString(actualLocation));
                } else {
                    textLocation.setText(Integer.toHexString(actualLocation).toUpperCase());
                }
            }
        }
    }

}
