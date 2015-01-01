/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.swt.widgets.AutoScrollbarsListener;
import biz.isphere.base.swt.widgets.NumericOnlyVerifyListener;
import biz.isphere.base.swt.widgets.SelectAllFocusListener;

/**
 * Factory for creating SWT widgets.
 * 
 * @author Thomas Raddatz
 */
public final class WidgetFactory {

    /**
     * The instance of this Singleton class.
     */
    private static WidgetFactory instance;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private WidgetFactory() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static WidgetFactory getInstance() {
        if (instance == null) {
            instance = new WidgetFactory();
        }
        return instance;
    }

    /**
     * Produces a single line text field with a border.
     * 
     * @param parent - parent composite
     * @return text field
     */
    public static Text createText(Composite parent) {
        return WidgetFactory.getInstance().produceText(parent, SWT.NONE, true);
    }

    /**
     * Produces a read-only single line text field.
     * 
     * @param parent - parent composite
     * @return read-only text field
     */
    public static Text createReadOnlyText(Composite parent) {

        Text text = WidgetFactory.getInstance().produceText(parent, SWT.NONE, false);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a password field with a border.
     * 
     * @param parent - parent composite
     * @return password field
     */
    public static Text createPassword(Composite parent) {
        return WidgetFactory.getInstance().produceText(parent, SWT.PASSWORD, true);
    }

    /**
     * Produces a multi-line line text field with a border. If the text does not
     * fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent - parent composite
     * @return multi-line text field
     */
    public static Text createMultilineText(Composite parent) {
        return WidgetFactory.getInstance().produceMultilineText(parent, SWT.NONE, false);
    }

    /**
     * Produces a multi-line line text field with a border. If the text does not
     * fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent - parent composite
     * @param wordWrap - <code>true</code>, to enable word wrap
     * @param autoSelect - <code>true</code>, to select the field content when
     *        entering the field wit the cursor
     * @return multi-line text field
     */
    public static Text createMultilineText(Composite parent, boolean wordWrap, boolean autoSelect) {
        int style;
        if (wordWrap) {
            style = SWT.WRAP;
        } else {
            style = SWT.NONE;
        }
        return WidgetFactory.getInstance().produceMultilineText(parent, style, autoSelect);
    }

    /**
     * Produces a read-only multiline line text field. If the text does not fit
     * into the field, a vertical scroll bar is displayed.
     * 
     * @param parent - parent composite
     * @return read-only multi-line text field
     */
    public static Text createReadOnlyMultilineText(Composite parent) {

        Text text = WidgetFactory.createMultilineText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a read-only multiline line text field. If the text does not fit
     * into the field, a vertical scroll bar is displayed.
     * 
     * @param parent - parent composite
     * @param wordWrap - <code>true</code>, to enable word wrap
     * @param autoSelect - <code>true</code>, to select the field content when
     *        entering the field wit the cursor
     * @return read-only multi-line text field
     */
    public static Text createReadOnlyMultilineText(Composite parent, boolean wordWrap, boolean autoSelect) {

        Text text = WidgetFactory.createMultilineText(parent, wordWrap, autoSelect);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces an integer text field with a border. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent - parent composite
     * @return integer text field
     */
    public static Text createIntegerText(Composite parent) {

        Text text = WidgetFactory.createText(parent);
        text.addVerifyListener(new NumericOnlyVerifyListener());

        return text;
    }

    /**
     * Produces a read-only integer text field. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent - parent composite
     * @return read-only integer text field
     */
    public static Text createReadOnlyIntegerText(Composite parent) {

        Text text = WidgetFactory.createIntegerText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a decimal text field with a border. Only the character 0-9 and
     * comma are allowed to be entered.
     * 
     * @param parent - parent composite
     * @return decimal text field
     */
    public static Text createDecimalText(Composite parent) {

        Text text = WidgetFactory.createText(parent);
        text.addVerifyListener(new NumericOnlyVerifyListener(true));

        return text;
    }

    /**
     * Produces a read-only decimal text field with a border. Only the character
     * 0-9 and comma are allowed to be entered.
     * 
     * @param parent - parent composite
     * @param border - specifies whether or not to add a border
     * @return read-only decimal text field
     */
    public static Text createReadOnlyDecimalText(Composite parent, boolean border) {

        Text text = WidgetFactory.createDecimalText(parent);
        text.setEditable(false);

        return text;
    }

    /**
     * Produces a combo field.
     * 
     * @param parent - parent composite
     * @return combo field
     */
    public static Combo createCombo(Composite parent) {
        return WidgetFactory.getInstance().produceComboField(parent, SWT.NONE);
    }

    /**
     * Produces a read-only combo field.
     * 
     * @param parent - parent composite
     * @return read-only combo field
     */
    public static Combo createReadOnlyCombo(Composite parent) {
        return WidgetFactory.getInstance().produceComboField(parent, SWT.READ_ONLY);
    }

    /**
     * Produces a spinner field.
     * 
     * @param parent - parent composite
     * @return spinner field
     */
    public static Spinner createSpinner(Composite parent) {
        return WidgetFactory.getInstance().produceSpinnerField(parent, SWT.BORDER);
    }

    /**
     * Produces a read-only spinner field.
     * 
     * @param parent - parent composite
     * @return read-only spinner field
     */
    public static Spinner createReadOnlySpinner(Composite parent) {
        return WidgetFactory.getInstance().produceSpinnerField(parent, SWT.BORDER | SWT.READ_ONLY);
    }

    /**
     * Produces a checkbox field.
     * 
     * @param parent - parent composite
     * @return checkbox field
     */
    public static Button createCheckbox(Composite parent) {
        return WidgetFactory.getInstance().produceCheckboxField(parent);
    }

    /**
     * Produces a read-only checkbox field.
     * 
     * @param parent - parent composite
     * @return read-only checkbox field
     */
    public static Button createReadOnlyCheckbox(Composite parent) {

        Button checkBox = WidgetFactory.createCheckbox(parent);
        checkBox.setEnabled(false);

        return checkBox;
    }

    /**
     * Produces a push button field.
     * 
     * @param parent - parent composite
     * @return push button field
     */
    public static Button createPushButton(Composite parent) {
        return WidgetFactory.getInstance().producePushButton(parent);
    }

    /**
     * Produces a read-only push button field.
     * 
     * @param parent - parent composite
     * @return read-only push button field
     */
    public static Button createReadOnlyPushButton(Composite parent) {

        Button pushButton = WidgetFactory.createPushButton(parent);
        pushButton.setEnabled(false);

        return pushButton;
    }

    /**
     * Produces a radio button field.
     * 
     * @param parent - parent composite
     * @return radio button field
     */
    public static Button createRadioButton(Composite parent) {
        return WidgetFactory.getInstance().produceRadioButton(parent);
    }

    /**
     * Produces a read-only radio button field.
     * 
     * @param parent - parent composite
     * @return read-only radio button field
     */
    public static Button createReadOnlyRadioButton(Composite parent) {

        Button radioButton = WidgetFactory.createRadioButton(parent);
        radioButton.setEnabled(false);

        return radioButton;
    }

    /*
     * Private worker procedures, doing the actual work.
     */
    private Text produceText(Composite parent, int style, boolean autoSelect) {

        Text text = new Text(parent, style | SWT.BORDER);
        if (autoSelect) {
            text.addFocusListener(new SelectAllFocusListener());
        }

        return text;
    }

    private Text produceMultilineText(Composite parent, int style, boolean autoSelect) {

        Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | style);

        Listener scrollBarListener = new AutoScrollbarsListener();
        text.addListener(SWT.Resize, scrollBarListener);
        text.addListener(SWT.Modify, scrollBarListener);

        if (autoSelect) {
            text.addFocusListener(new SelectAllFocusListener());
        }

        return text;
    }

    private Combo produceComboField(Composite parent, int style) {

        Combo combo = new Combo(parent, style | SWT.DROP_DOWN);

        return combo;
    }

    private Spinner produceSpinnerField(Composite parent, int style) {

        Spinner spinner = new Spinner(parent, style);

        return spinner;
    }

    private Button produceCheckboxField(Composite parent) {

        Button checkBox = new Button(parent, SWT.CHECK);

        return checkBox;
    }

    private Button producePushButton(Composite parent) {

        Button pushButton = new Button(parent, SWT.PUSH);

        return pushButton;
    }

    private Button produceRadioButton(Composite parent) {

        Button radioButton = new Button(parent, SWT.RADIO);

        return radioButton;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }
}
