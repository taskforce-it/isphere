/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.ColorHelper;
import biz.isphere.core.spooledfiles.SpooledFile;

public class SpooledFilePropertiesDialog extends Dialog {

    private SpooledFile spooledFile;

    public SpooledFilePropertiesDialog(Shell parentShell) {
        super(parentShell);
    }

    public SpooledFilePropertiesDialog(Shell parentShell, SpooledFile spooledFile) {
        super(parentShell);

        setSpooledFile(spooledFile);
    }

    private void setDialogTitle() {
        if (spooledFile == null) {
            getShell().setText(Messages.EMPTY);
        } else {
            getShell().setText(Messages.bind(Messages.Properties_for_A_B, spooledFile.getFile(), spooledFile.getStatus()));
        }
    }

    public void setSpooledFile(SpooledFile spooledFile) {
        this.spooledFile = spooledFile;
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        createMainArea(container);

        setDialogTitle();

        return container;
    }

    private Composite createMainArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        mainArea.setLayout(layout);
        mainArea.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

        createLeftArea(mainArea);

        createRightArea(mainArea);

        return mainArea;
    }

    private void createLeftArea(Composite parent) {

        Composite leftArea = new Composite(parent, SWT.BORDER);
        GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 25;
        layout.marginRight = 60;
        leftArea.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.verticalIndent = 0;
        gridData.horizontalIndent = -1;
        leftArea.setLayoutData(gridData);
        leftArea.setBackground(ColorHelper.getDefaultBackgroundColor());

        Label lblHeadline = new Label(leftArea, SWT.SHADOW_ETCHED_OUT);
        lblHeadline.setLayoutData(new GridData(GridData.FILL_BOTH));
        lblHeadline.setText(Messages.Properties);
        lblHeadline.setBackground(ColorHelper.getDefaultBackgroundColor());
    }

    private void createRightArea(Composite parent) {

        Composite rightArea = new Composite(parent, SWT.BORDER);

        new SpooledFileProperties(rightArea, spooledFile);

        // Set layout afterwards, because SpooledFileProperties changes the
        // layout of the parent composite.
        GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginBottom = 50;
        layout.marginLeft = 15;
        layout.marginRight = 100;
        rightArea.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.verticalIndent = 0;
        gridData.horizontalIndent = -1;
        rightArea.setLayoutData(gridData);
    }
}
