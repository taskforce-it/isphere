/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefileeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import biz.isphere.core.swt.widgets.extension.WidgetFactory;

public abstract class AbstractViewMessageDescriptionPreview extends ViewPart {

    public static final String ID = "biz.isphere.rse.messagefileeditor.ViewMessageDescriptionPreview";
    private ISelectionListener selectionListener;
    private Text tMessagePreview;

    public AbstractViewMessageDescriptionPreview() {
    }

    @Override
    public void createPartControl(Composite parent) {

        Composite tTextArea = new Composite(parent, SWT.NONE);
        tTextArea.setLayout(new FillLayout());

        tMessagePreview = WidgetFactory.createReadOnlyMultilineText(tTextArea, false, false);
        tMessagePreview.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));

        selectionListener = registerSelectionListener(tMessagePreview);
    }

    protected abstract ISelectionListener registerSelectionListener(Text aMessagePreview);

    public void deregisterSelectionListener() {
        getSite().getPage().removeSelectionListener(selectionListener);
    }

    public void dispose() {
        deregisterSelectionListener();
    }

    @Override
    public void setFocus() {
        tMessagePreview.setFocus();
    }

}
