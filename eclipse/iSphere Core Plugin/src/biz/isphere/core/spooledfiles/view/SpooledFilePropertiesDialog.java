/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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

        ScrolledComposite scrollable = new ScrolledComposite(container, SWT.V_SCROLL | SWT.H_SCROLL | SWT.NONE);
        scrollable.setLayout(new GridLayout(1, false));
        scrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        scrollable.setExpandHorizontal(true);
        scrollable.setExpandVertical(true);
        scrollable.setContent(createMainArea(scrollable));

        setDialogTitle();

        return container;
    }

    private Composite createMainArea(Composite parent) {

        Composite mainArea = new Composite(parent, SWT.BORDER);
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

        Composite leftArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        leftArea.setLayout(layout);
        leftArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        leftArea.setBackground(ColorHelper.getDefaultBackgroundColor());

        Label lblHeadline = new Label(leftArea, SWT.SHADOW_ETCHED_OUT);
        lblHeadline.setLayoutData(new GridData(GridData.FILL_BOTH));
        lblHeadline.setText(Messages.Properties);
        lblHeadline.setFont(JFaceResources.getFontRegistry().getBold("org.eclipse.jface.dialogfont")); //$NON-NLS-1$
        lblHeadline.setBackground(ColorHelper.getDefaultBackgroundColor());
    }

    private void createRightArea(Composite parent) {

        Composite rightArea = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();

        new SpooledFileProperties(rightArea, spooledFile);

        // Set layout afterwards, because SpooledFileProperties changes the
        // layout of the parent composite.
        rightArea.setLayout(layout);
        rightArea.setLayoutData(new GridData(GridData.FILL_BOTH));
    }
}
