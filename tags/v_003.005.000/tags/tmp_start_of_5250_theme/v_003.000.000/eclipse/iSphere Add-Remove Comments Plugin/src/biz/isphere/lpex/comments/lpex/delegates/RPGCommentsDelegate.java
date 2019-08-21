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

import biz.isphere.lpex.comments.lpex.exceptions.CommentExistsException;
import biz.isphere.lpex.comments.lpex.exceptions.OperationNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class RPGCommentsDelegate extends AbstractCommentDelegate implements ICommentDelegate {

    private static final String FULLY_FREE = "**FREE"; //$NON-NLS-1$ 
    private static final String DIRECTIVE = "/"; //$NON-NLS-1$

    private static final String FIX_FORMAT_COMMENT = "*"; //$NON-NLS-1$ 
    private static final String FREE_FORMAT_COMMENT = "// "; //$NON-NLS-1$
    private static final String FULLY_FREE_FORMAT_COMMENT = "// "; //$NON-NLS-1$

    private static final int FIX_FORMAT_SPEC_POS = 6;
    private static final int FIX_FORMAT_COMMENT_POS = 7;
    private static final int FREE_FORMAT_COMMENT_POS = 8;
    private static final int FULLY_FREE_FORMAT_COMMENT_POS = 1;

    private static final String SPEC_CONTROL = "H"; //$NON-NLS-1$
    private static final String SPEC_FILE = "F"; //$NON-NLS-1$
    private static final String SPEC_DEFINITION = "D"; //$NON-NLS-1$
    private static final String SPEC_INPUT = "I"; //$NON-NLS-1$
    private static final String SPEC_CALCULATION = "C"; //$NON-NLS-1$
    private static final String SPEC_OUTPUT = "O"; //$NON-NLS-1$
    private static final String SPEC_PROCEDURE = "P"; //$NON-NLS-1$

    private static final Set<String> SPECIFICATIONS = new HashSet<String>(Arrays.asList(new String[] { SPEC_CONTROL, SPEC_FILE, SPEC_DEFINITION,
        SPEC_INPUT, SPEC_CALCULATION, SPEC_OUTPUT, SPEC_PROCEDURE }));

    private boolean validate;

    public RPGCommentsDelegate(LpexView view) {
        super(view);
    }

    public void validate(boolean enable) {
        this.validate = enable;
    }

    public boolean isLineComment(String text) {

        if (isFixFormat(text)) {
            if (FIX_FORMAT_COMMENT.equals(getFixFormatCommentChar(text))) {
                return true;
            } else {
                return false;
            }
        } else {
            if (FREE_FORMAT_COMMENT.equals(getFreeFormatCommentChar(text))) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean isComment(String text, int index) {

        if (findCommentBackWard(text, index) >= 0) {
            return true;
        }

        return false;
    }

    /**
     * Comments a complete line.
     * 
     * @parm text - line to comment
     * @throws TextLimitExceededException
     */
    public String comment(String text) throws TextLimitExceededException, CommentExistsException {

        if (isLineComment(text)) {
            throw new CommentExistsException();
        }

        StringBuilder buffer = new StringBuilder(text);

        if (isFullyFree()) {
            buffer.insert(FULLY_FREE_FORMAT_COMMENT_POS - 1, FULLY_FREE_FORMAT_COMMENT);
        } else if (isFixFormat(text)) {
            buffer.insert(FIX_FORMAT_COMMENT_POS - 1, FIX_FORMAT_COMMENT);
        } else {
            int i = findStartOfText(buffer.toString());
            if (i >= FREE_FORMAT_COMMENT_POS - 1) {
                buffer.insert(FREE_FORMAT_COMMENT_POS - 1, FREE_FORMAT_COMMENT);
            } else {
                buffer.insert(i, FREE_FORMAT_COMMENT);
            }
        }

        if (buffer.length() > getLineLength()) {
            throw new TextLimitExceededException();
        }

        if (validate) {
            return text;
        }

        return buffer.toString();
    }

    /**
     * Comments the selected part of a line.
     * 
     * @parm text - selected text to comment
     * @parm startPos - start position of the selected text
     * @parm endPos - end position of the selected text
     */
    public String comment(String text, int startPos, int endPos) throws TextLimitExceededException, CommentExistsException,
        OperationNotSupportedException {

        startPos--;
        endPos--;

        if (isFixFormat(text)) {
            throw new OperationNotSupportedException();
        }

        if (isComment(text, startPos)) {
            throw new CommentExistsException();
        }

        if (validate) {
            return text;
        }

        StringBuilder buffer = new StringBuilder(text);

        if (isFullyFree()) {
            buffer.insert(startPos, FULLY_FREE_FORMAT_COMMENT);
        } else if (isFixFormat(text)) {
            // not supported (see above)
        } else {
            buffer.insert(startPos, FREE_FORMAT_COMMENT);
        }

        return buffer.toString();
    }

    /**
     * Uncomments a complete line.
     * 
     * @parm text - line to uncomment
     */
    public String uncomment(String text) throws OperationNotSupportedException {

        if (!isLineComment(text)) {
            return uncomment(text, getCursorPosition(), getCursorPosition());
        }

        int i;
        int length;
        int rLength;
        if (isFullyFree()) {
            i = text.indexOf(FULLY_FREE_FORMAT_COMMENT.trim());
            length = FULLY_FREE_FORMAT_COMMENT.trim().length();
            rLength = FULLY_FREE_FORMAT_COMMENT.length() - length;
        } else if (isFixFormat(text)) {
            i = text.indexOf(FIX_FORMAT_COMMENT.trim());
            length = FIX_FORMAT_COMMENT.trim().length();
            rLength = FIX_FORMAT_COMMENT.length() - length;
        } else {
            i = text.indexOf(FREE_FORMAT_COMMENT.trim());
            length = FREE_FORMAT_COMMENT.length();
            rLength = 0;
        }

        StringBuilder buffer = new StringBuilder(text);
        buffer.replace(i, i + length, NOTHING);

        if (rLength > 0) {
            if (buffer.substring(i, i + rLength).trim().length() == 0) {
                buffer.replace(i, i + rLength, NOTHING);
            }
        }

        return buffer.toString();
    }

    /**
     * Uncomments the selected part of a line.
     * 
     * @parm text - selected text to uncomment
     * @parm startPos - start position of the selected text
     * @parm endPos - end position of the selected text
     */
    public String uncomment(String text, int startPos, int endPos) throws OperationNotSupportedException {

        startPos--;
        endPos--;

        if (isFixFormat(text)) {
            throw new OperationNotSupportedException();
        }

        if (validate) {
            return text;
        }

        int i = findCommentBackWard(text, startPos);
        if (i < 0) {
            return text;
        }

        StringBuilder buffer = new StringBuilder(text);

        int length;
        int rLength;
        if (isFullyFree()) {
            length = FULLY_FREE_FORMAT_COMMENT.trim().length();
            rLength = FULLY_FREE_FORMAT_COMMENT.length() - length;
        } else if (isFixFormat(text)) {
            // not supported (see above)
            throw new RuntimeException("Should never have been reached."); //$NON-NLS-1$
        } else {
            length = FREE_FORMAT_COMMENT.trim().length();
            rLength = FREE_FORMAT_COMMENT.length() - length;
        }

        buffer.replace(i, i + length, NOTHING);

        if (rLength > 0) {
            if (buffer.substring(i, i + rLength).trim().length() == 0) {
                buffer.replace(i, i + rLength, NOTHING);
            }
        }

        return buffer.toString();
    }

    private int findCommentBackWard(String text, int index) {

        int i = -1;
        if (isFullyFree()) {
            i = text.lastIndexOf(FULLY_FREE_FORMAT_COMMENT.trim(), index + 1);
        } else if (isFixFormat(text)) {
            throw new RuntimeException("Illegal operation call."); //$NON-NLS-1$
        } else {
            i = text.lastIndexOf(FREE_FORMAT_COMMENT.trim(), index + 1);
        }

        return i;
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

    private String getFreeFormatCommentChar(String text) {

        if (text.trim().startsWith(FREE_FORMAT_COMMENT)) {
            return FREE_FORMAT_COMMENT;
        }

        return NOTHING;
    }

    private int findStartOfText(String text) {

        int i = 0;
        while (i < text.length() && SPACE.equals(text.substring(i, i + 1))) {
            i++;
        }

        return i;
    }
}
