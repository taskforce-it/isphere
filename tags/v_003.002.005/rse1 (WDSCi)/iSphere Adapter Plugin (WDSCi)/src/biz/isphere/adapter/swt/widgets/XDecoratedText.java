/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.swt.widgets;

import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class XDecoratedText implements IControlCreator {

    private DecoratedField text;

    private FieldDecoration errorFieldIndicator;

    public XDecoratedText(Composite aParent, int aStyle) {

        text = new DecoratedField(aParent, aStyle, this);
        errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        text.addFieldDecoration(errorFieldIndicator, SWT.LEFT | SWT.TOP, true);

        hideDecoration();
    }

    public void addModifyListener(ModifyListener aListener) {
        getControl().addModifyListener(aListener);
    }

    public void setLayoutData(Object aLayoutData) {
        getControl().getParent().setLayoutData(aLayoutData);
    }

    public void showDecoration() {
        showDecoration(null);
    }

    public void showDecoration(String aDescription) {
        if (aDescription == null) {
            errorFieldIndicator.setDescription(errorFieldIndicator.getDescription());
        } else {
            errorFieldIndicator.setDescription(aDescription);
        }
        text.showDecoration(errorFieldIndicator);
    }

    public void hideDecoration() {
        text.hideDecoration(errorFieldIndicator);
    }

    public Text getControl() {
        return (Text)text.getControl();
    }

    public Control createControl(Composite parent, int style) {
        Text text = new Text(parent, style);
        return text;
    }
}
