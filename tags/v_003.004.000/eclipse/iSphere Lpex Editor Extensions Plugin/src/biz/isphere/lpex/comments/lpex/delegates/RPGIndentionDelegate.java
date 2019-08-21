/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.delegates;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.lpex.comments.lpex.exceptions.FixedFormatNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.MaxLeftMarginReachedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;
import biz.isphere.lpex.comments.preferences.Preferences;

import com.ibm.lpex.core.LpexView;

public class RPGIndentionDelegate extends AbstractIndentionDelegate implements IIndentionDelegate {

    private static final String FULLY_FREE = "**FREE"; //$NON-NLS-1$ 
    private static final String DIRECTIVE = "/"; //$NON-NLS-1$

    private static final String FIX_FORMAT_COMMENT = "*"; //$NON-NLS-1$ 
    private static final String FREE_FORMAT_COMMENT = "// "; //$NON-NLS-1$

    private static final int FIX_FORMAT_SPEC_POS = 6;
    private static final int FIX_FORMAT_COMMENT_POS = 7;
    private static final int FIX_FORMAT_START_OF_STMTS = 8;

    private static final String SPEC_CONTROL = "H"; //$NON-NLS-1$
    private static final String SPEC_FILE = "F"; //$NON-NLS-1$
    private static final String SPEC_DEFINITION = "D"; //$NON-NLS-1$
    private static final String SPEC_INPUT = "I"; //$NON-NLS-1$
    private static final String SPEC_CALCULATION = "C"; //$NON-NLS-1$
    private static final String SPEC_OUTPUT = "O"; //$NON-NLS-1$
    private static final String SPEC_PROCEDURE = "P"; //$NON-NLS-1$

    private static final Set<String> SPECIFICATIONS = new HashSet<String>(Arrays.asList(new String[] { SPEC_CONTROL, SPEC_FILE, SPEC_DEFINITION,
        SPEC_INPUT, SPEC_CALCULATION, SPEC_OUTPUT, SPEC_PROCEDURE }));

    /**
     * Specifies whether the delegate is in validation mode.
     */
    private boolean validate;

    public RPGIndentionDelegate(LpexView view) {
        super(view);
    }

    public void setValidationMode(boolean enable) {
        this.validate = enable;
    }

    /**
     * Indents a line.
     * 
     * @parm text - line that is shifted to the right (= indented)
     * @throws TextLimitExceededException
     */
    public String indent(String text) throws TextLimitExceededException, FixedFormatNotSupportedException {

        if (!isFullyFree()) {
            if (isFixFormat(text)) {
                throw new FixedFormatNotSupportedException();
            }
        }

        StringBuilder buffer = new StringBuilder(text);

        int startOfText = findStartOfText(buffer.toString());
        int endOfInsertion = startOfText + getCSpecIndention();

        if (isCSpecPositionEnabled()) {
            int cSpecOffset = getCSpecPosition() - 1;
            if (startOfText < cSpecOffset && endOfInsertion > cSpecOffset) {
                endOfInsertion = cSpecOffset;
            }
        }

        buffer.insert(startOfText, getIndentionString(endOfInsertion - startOfText));

        if (buffer.length() > getLineLength()) {
            throw new TextLimitExceededException();
        }

        if (validate) {
            return text;
        }

        return buffer.toString();
    }

    /**
     * Unindents a line.
     * 
     * @parm text - line that is shifted to the left (= unindented)
     */
    public String unindent(String text) throws FixedFormatNotSupportedException, MaxLeftMarginReachedException {

        int minLeftMargin;
        if (isFullyFree()) {
            minLeftMargin = 0;
        } else {
            minLeftMargin = FIX_FORMAT_START_OF_STMTS - 1;
            if (isFixFormat(text)) {
                throw new FixedFormatNotSupportedException();
            }
        }

        StringBuilder buffer = new StringBuilder(text);

        int startOfText = findStartOfText(buffer.toString());
        int startOfDeletion = startOfText;

        if (startOfDeletion <= minLeftMargin) {
            throw new MaxLeftMarginReachedException();
        }

        startOfDeletion = startOfDeletion - getCSpecIndention();
        if (startOfDeletion < minLeftMargin) {
            startOfDeletion = minLeftMargin;
        }

        if (isCSpecPositionEnabled()) {
            int cSpecOffset = getCSpecPosition() - 1;
            if (startOfDeletion < cSpecOffset && startOfText > cSpecOffset) {
                startOfDeletion = cSpecOffset;
            }
        }

        buffer.replace(startOfDeletion, startOfText, NOTHING);

        return buffer.toString();
    }

    private String getIndentionString(int length) {

        return StringHelper.getFixLength("", length);
    }

    private boolean isCSpecPositionEnabled() {
        return Preferences.getInstance().isCSpecPositionEnabled();
    }

    private int getCSpecPosition() {
        return Preferences.getInstance().getCSpecPosition();
    }

    private int getCSpecIndention() {
        return Preferences.getInstance().getCSpecIndention();
    }

    private boolean isFullyFree() {

        LpexView view = getView();
        if (view.queryInt("lines") >= 1) { //$NON-NLS-1$
            String text = view.elementText(1).toUpperCase();
            if (text.indexOf(FULLY_FREE) >= 0) {
                return true;
            }
        }

        return false;
    }

    private boolean isFixFormat(String text) {

        String spec = getFixFormatSpecChar(text).toUpperCase();
        if (SPECIFICATIONS.contains(spec)) {
            return true;
        }

        String comment = getFixFormatCommentChar(text);
        if (FIX_FORMAT_COMMENT.equals(comment)) {
            return true;
        }

        if (DIRECTIVE.equals(comment)) {
            if (text.length() > FIX_FORMAT_COMMENT_POS && text.substring(FIX_FORMAT_COMMENT_POS - 1).startsWith(FREE_FORMAT_COMMENT.trim())) {
                return false;
            }
            return true;
        }

        return false;
    }

    private String getFixFormatSpecChar(String text) {
        return text.substring(FIX_FORMAT_SPEC_POS - 1, FIX_FORMAT_SPEC_POS);
    }

    private String getFixFormatCommentChar(String text) {
        return text.substring(FIX_FORMAT_COMMENT_POS - 1, FIX_FORMAT_COMMENT_POS);
    }

    private int findStartOfText(String text) {

        int i = 0;
        while (i < text.length() && SPACE.equals(text.substring(i, i + 1))) {
            i++;
        }

        return i;
    }
}
