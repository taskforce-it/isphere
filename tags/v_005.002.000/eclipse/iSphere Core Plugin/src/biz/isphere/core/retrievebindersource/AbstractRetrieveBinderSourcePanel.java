/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.retrievebindersource;

import org.eclipse.swt.widgets.Control;

import biz.isphere.base.internal.DialogSettingsManager;

public abstract class AbstractRetrieveBinderSourcePanel {

    private static final String CONNECTION = "CONNECTION";
    private static final String SOURCE_LIBRARY = "SOURCE_LIBRARY";
    private static final String SOURCE_FILE = "SOURCE_FILE";
    private static final String COPY_TO_CLIPBOARD = "COPY_TO_CLIPBOARD";

    private String errorMessage;
    private Control errorControl;

    public void loadScreenValues(DialogSettingsManager settingsManager) {

        // String connectionName = settingsManager.loadValue(CONNECTION,
        // getConnectionName());
        String sourceFileLibraryName = settingsManager.loadValue(SOURCE_LIBRARY, getSourceFileLibrary());
        String sourceFileName = settingsManager.loadValue(SOURCE_FILE, getSourceFile());
        boolean isCopyToClipboard = settingsManager.loadBooleanValue(COPY_TO_CLIPBOARD, false);

        // setConnectionName(connectionName);
        setSourceLibrary(sourceFileLibraryName);
        setSourceFile(sourceFileName);
        setCopyToClipboard(isCopyToClipboard);
    }

    public void storeScreenValues(DialogSettingsManager settingsManager) {

        settingsManager.storeValue(CONNECTION, getConnectionName());
        settingsManager.storeValue(SOURCE_LIBRARY, getSourceFileLibrary());
        settingsManager.storeValue(SOURCE_FILE, getSourceFile());
        settingsManager.storeValue(COPY_TO_CLIPBOARD, isCopyToClipboard());
    }

    public abstract String getConnectionName();

    public abstract void setConnectionName(String connectionName);

    public abstract String getSourceFileLibrary();

    public abstract void setSourceLibrary(String library);

    public abstract String getSourceFile();

    public abstract void setSourceFile(String file);

    public abstract String getSourceMember();

    public abstract void setSourceMember(String member);

    public abstract boolean isCopyToClipboard();

    public abstract void setCopyToClipboard(boolean copyToClipboard);

    public boolean isError() {

        if (errorMessage != null) {
            return true;
        }

        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setFocus() {

        if (getErrorControl() != null) {
            getErrorControl().setFocus();
            return;
        }
    }

    protected Control getErrorControl() {
        return errorControl;
    }

    protected void setErrorMessage(String message, Control control) {

        errorMessage = message;
        errorControl = control;
    }

    protected void clearErrorMessage() {

        errorMessage = null;
        errorControl = null;
    }

}
