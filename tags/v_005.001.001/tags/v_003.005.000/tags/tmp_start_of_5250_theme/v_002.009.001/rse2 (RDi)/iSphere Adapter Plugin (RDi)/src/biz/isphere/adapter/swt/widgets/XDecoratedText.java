/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.swt.widgets;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class XDecoratedText {

    private Text text;

    private ControlDecoration decTxtFileExtension;

    private FieldDecoration errorFieldIndicator;

    public XDecoratedText(Composite aParent, int aStyle) {

        text = new Text(aParent, SWT.BORDER);
        decTxtFileExtension = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        errorFieldIndicator = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        decTxtFileExtension.setImage(errorFieldIndicator.getImage());
        decTxtFileExtension.setShowOnlyOnFocus(true);
        decTxtFileExtension.setMarginWidth(5);

        hideDecoration();
    }

    public void addModifyListener(ModifyListener aListener) {
        text.addModifyListener(aListener);
    }

    public void setLayoutData(Object aLayoutData) {
        text.setLayoutData(aLayoutData);
    }

    public void showDecoration() {
        showDecoration(null);
    }

    public void showDecoration(String aDescription) {
        if (aDescription == null) {
            decTxtFileExtension.setDescriptionText(errorFieldIndicator.getDescription());
        } else {
            decTxtFileExtension.setDescriptionText(aDescription);
        }
        decTxtFileExtension.show();
    }

    public void hideDecoration() {
        decTxtFileExtension.hide();
    }

    public Text getControl() {
        return text;
    }
}
