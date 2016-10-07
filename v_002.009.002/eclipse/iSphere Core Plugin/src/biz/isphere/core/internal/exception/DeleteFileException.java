/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

import java.io.File;

import biz.isphere.core.Messages;

public class DeleteFileException extends AbstractFileException {

    private static final long serialVersionUID = -5089581875863093335L;

    private static final String text = "Failed to delete file";

    private static final String localizedText = Messages.Failed_to_delete_file;

    public DeleteFileException() {
        super(text, localizedText);
    }

    public DeleteFileException(File file) {
        super(text, localizedText, file);
    }

    public DeleteFileException(Throwable aCause) {
        super(text, localizedText, aCause);
    }

    public DeleteFileException(File file, Throwable aCause) {
        super(text, localizedText, file, aCause);
    }
}
