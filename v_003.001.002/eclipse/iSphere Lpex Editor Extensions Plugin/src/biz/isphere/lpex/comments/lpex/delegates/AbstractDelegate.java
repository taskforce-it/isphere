/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.delegates;

import com.ibm.lpex.core.LpexView;

public abstract class AbstractDelegate {

    protected static final String SPACE = " "; //$NON-NLS-1$
    protected static final String NOTHING = ""; //$NON-NLS-1$
    private static int SEQUENCE_LENGTH = 12;

    private LpexView view;
    private int lineLength = -1;

    public AbstractDelegate(LpexView view) {
        this.view = view;
    }

    public int getLineLength() {

        if (lineLength < 0) {
            lineLength = view.queryInt("save.textLimit") - SEQUENCE_LENGTH; //$NON-NLS-1$
        }

        return lineLength;
    }

    protected LpexView getView() {
        return view;
    }

    public int getCursorPosition() {
        return view.queryInt("displayPosition"); //$NON-NLS-1$
    }

    protected int countLeftSpaces(String text) {

        int i = 0;
        while (i < text.length() && SPACE.equals(text.substring(i, i + 1))) {
            i++;
        }

        return i;
    }

    protected int countRightSpaces(String text) {

        int i = text.length();
        while (i > 1 && SPACE.equals(text.substring(i - 1, i))) {
            i--;
        }

        return getLineLength() - i;
    }

    protected boolean isAllEmpty(String buffer, int startIndex, int length) {

        if (startIndex + length >= buffer.length()) {
            return true;
        }

        if (buffer.substring(startIndex, startIndex + length).trim().length() == 0) {
            return true;
        }

        return false;
    }

}
