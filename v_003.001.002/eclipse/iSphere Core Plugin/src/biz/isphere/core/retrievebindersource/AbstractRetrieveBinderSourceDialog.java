/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.retrievebindersource;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

public abstract class AbstractRetrieveBinderSourceDialog extends XDialog {

    private static final String TEMP_SOURCE_FILE_LIBRARY = "QTEMP";
    private static final String TEMP_SOURCE_FILE = "XBNDSRCX";
    private static final String TEMP_SOURCE_MEMBER = "XBNDSRCX";

    private String connectionName;
    private String library;
    private String sourceFile;
    private String sourceMember;
    private String sourceMemberDescription;
    private boolean copyToClipboard;

    public AbstractRetrieveBinderSourceDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Retrieve_Binder_Source);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        container.setLayout(new FillLayout());

        createDialogPanel(container);

        createStatusLine(parent);
        loadScreenValues();

        return container;
    }

    protected abstract void createDialogPanel(Composite parent);

    protected abstract void loadScreenValues();

    protected abstract void saveScreenValues();

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setSourceFileLibrary(String library) {
        this.library = library;
    }

    public void setSourceFile(String file) {
        this.sourceFile = file;
    }

    public void setSourceMember(String member) {
        this.sourceMember = member;
    }

    public void setSourceMemberDescription(String description) {
        this.sourceMemberDescription = description;
    }

    public void setCopyToClipboard(boolean copyToClipboard) {
        this.copyToClipboard = copyToClipboard;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getSourceFileLibrary() {

        if (isCopyToClipboard()) {
            return TEMP_SOURCE_FILE_LIBRARY;
        }

        return library;
    }

    public String getSourceFile() {

        if (isCopyToClipboard()) {
            return TEMP_SOURCE_FILE;
        }

        return sourceFile;
    }

    public String getSourceMember() {

        if (isCopyToClipboard()) {
            return TEMP_SOURCE_MEMBER;
        }

        return sourceMember;
    }

    public String getSourceMemberDescription() {
        return sourceMemberDescription;
    }

    public boolean isCopyToClipboard() {
        return copyToClipboard;
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
        return getShell().computeSize(450, 290, true);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISpherePlugin.getDefault().getDialogSettings());
    }
}
