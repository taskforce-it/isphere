/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.internal.RemoteObject;

public class ErrorContext {

    private RemoteObject fromObject;
    private RemoteObject toObject;
    private CopyMemberItem copyMemberItem;

    public static ErrorContext newToLibrary(String toConnectionName, String toLibrary) {
        ErrorContext errorContext = new ErrorContext();
        errorContext.setToObject(RemoteObject.newLibrary(toConnectionName, toLibrary));
        return errorContext;
    }

    public static ErrorContext newToFile(String toConnectionName, String toFile, String toLibrary) {
        ErrorContext errorContext = new ErrorContext();
        errorContext.setToObject(RemoteObject.newFile(toConnectionName, toFile, toLibrary));
        return errorContext;
    }

    public static ErrorContext newFromFile(String fromConnectionName, String fromFile, String fromLibrary) {
        ErrorContext errorContext = new ErrorContext();
        errorContext.setFromObject(RemoteObject.newFile(fromConnectionName, fromFile, fromLibrary));
        return errorContext;
    }

    public RemoteObject getFromObject() {
        return fromObject;
    }

    public void setFromObject(RemoteObject fromObject) {
        this.fromObject = fromObject;
    }

    public RemoteObject getToObject() {
        return toObject;
    }

    public void setToObject(RemoteObject toObject) {
        this.toObject = toObject;
    }

    public CopyMemberItem getCopyMemberItem() {
        return copyMemberItem;
    }

    public void setCopyMemberItem(CopyMemberItem copyMemberItem) {
        this.copyMemberItem = copyMemberItem;
    }
}
