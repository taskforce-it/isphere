/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension;

import org.eclipse.swt.SWT;
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
     * Produces a single line text field with a border.
     * 
     * @param parent - parent composite
     * @return text field
     */
    public static Text createText(Composite parent) {
        return createText(parent, true);
    }

    /**
     * Produces a single line text field.
     * 
     * @param parent - parent composite
     * @param border - specifies whether or not to add a border
     * @return text field
     */
    public static Text createText(Composite parent, boolean border) {

        int style = SWT.NONE;
        if (border) {
            style = style | SWT.BORDER;
        }

        Text text = new Text(parent, style);
        text.addFocusListener(new SelectAllFocusListener());

        return text;
    }

    /**
     * Produces a multiline line text field with a border. The text is wrapped
     * and if the text does not fit into the field, a vertical scroll bar is
     * displayed.
     * 
     * @param parent - parent composite
     * @return text field
     */
    public static Text createMultilineText(Composite parent) {
        return createMultilineText(parent, true);
    }

    /**
     * Produces a multiline line text field. The text is wrapped and if the text
     * does not fit into the field, a vertical scroll bar is displayed.
     * 
     * @param parent - parent composite
     * @param border - specifies whether or not to add a border
     * @return text field
     */
    public static Text createMultilineText(Composite parent, boolean border) {

        int style = SWT.MULTI | SWT.WRAP | SWT.V_SCROLL;
        if (border) {
            style = style | SWT.BORDER;
        }

        Text text = new Text(parent, style);

        Listener scrollBarListener = new AutoScrollbarsListener();
        text.addListener(SWT.Resize, scrollBarListener);
        text.addListener(SWT.Modify, scrollBarListener);
        text.addFocusListener(new SelectAllFocusListener());

        return text;
    }

    /**
     * Produces an integer text field with a border. Only the character 0-9 are
     * allowed to be entered.
     * 
     * @param parent - parent composite
     * @return text field
     */
    public static Text createIntegerText(Composite parent) {
        return createIntegerText(parent, true);
    }

    /**
     * Produces an integer text field. Only the character 0-9 are allowed to be
     * entered.
     * 
     * @param parent - parent composite
     * @param border - specifies whether or not to add a border
     * @return text field
     */
    public static Text createIntegerText(Composite parent, boolean border) {

        Text text = createText(parent, border);
        text.addVerifyListener(new NumericOnlyVerifyListener());

        return text;
    }

    /**
     * Produces a decimal text field with a border. Only the character 0-9 and
     * comma are allowed to be entered.
     * 
     * @param parent - parent composite
     * @return text field
     */
    public static Text createDecimalText(Composite parent) {
        return createDecimalText(parent, true);
    }

    /**
     * Produces a decimal text field with a border. Only the character 0-9 and
     * comma are allowed to be entered.
     * 
     * @param parent - parent composite
     * @param border - specifies whether or not to add a border
     * @return text field
     */
    public static Text createDecimalText(Composite parent, boolean border) {

        Text text = createText(parent, border);
        text.addVerifyListener(new NumericOnlyVerifyListener(true));

        return text;
    }

    /**
     * Produces a combo field.
     * 
     * @param parent - parent composite
     * @return writable combo field
     */
    public static Combo createComboField(Composite parent) {
        
        Combo combo = new Combo(parent, SWT.NONE);
        
        return combo;
    }
    
    /**
     * Produces a read-only combo field.
     * 
     * @param parent - parent composite
     * @return read-only combo field
     */
    public static Combo createReadOnlyComboField(Composite parent) {
        
        Combo combo = new Combo(parent, SWT.READ_ONLY);
        
        return combo;
    }

    /**
     * Produces a spinner field.
     * 
     * @param parent - parent composite
     * @return writable spinner field
     */
    public static Spinner createSpinnerField(Composite parent) {
        
        Spinner spinner = new Spinner(parent, SWT.BORDER);
        
        return spinner;
    }
    
    /**
     * Produces a read-only spinner field.
     * 
     * @param parent - parent composite
     * @return read-only spinner field
     */
    public static Spinner createReadOnlySpinnerField(Composite parent) {
        
        Spinner spinner = new Spinner(parent, SWT.BORDER | SWT.READ_ONLY);
        
        return spinner;
    }
}
