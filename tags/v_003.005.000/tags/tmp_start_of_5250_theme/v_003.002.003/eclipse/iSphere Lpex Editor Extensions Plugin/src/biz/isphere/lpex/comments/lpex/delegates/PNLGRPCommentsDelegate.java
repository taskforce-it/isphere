/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.delegates;

import biz.isphere.lpex.comments.lpex.exceptions.CommentExistsException;
import biz.isphere.lpex.comments.lpex.exceptions.CommentNotFoundException;
import biz.isphere.lpex.comments.lpex.exceptions.OperationNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class PNLGRPCommentsDelegate extends AbstractCommentDelegate implements ICommentDelegate {

    private static final String COMMENT = ".*"; //$NON-NLS-1$ 

    private static final int COMMENT_POS = 1;

    /**
     * Specifies whether the delegate is in validation mode.
     */
    private boolean validate;

    public PNLGRPCommentsDelegate(LpexView view) {
        super(view);
    }

    public void setValidationMode(boolean enable) {
        this.validate = enable;
    }

    public boolean isLineComment(String text) {

        if (COMMENT.equals(getFixFormatCommentChar(text))) {
            return true;
        } else {
            return false;
        }
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

        int offset;
        StringBuilder buffer = new StringBuilder(text);

        offset = COMMENT_POS - 1;
        while (offset >= buffer.length()) {
            buffer.append(SPACE);
        }
        buffer.insert(offset, COMMENT);

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

        throw new OperationNotSupportedException();
    }

    /**
     * Uncomments a complete line.
     * 
     * @parm text - line to uncomment
     */
    public String uncomment(String text) throws CommentNotFoundException {

        if (!isLineComment(text)) {
            throw new CommentNotFoundException();
        }

        int offset;
        StringBuilder buffer = new StringBuilder(text);

        offset = COMMENT_POS - 1;
        buffer.replace(offset, offset + COMMENT.length(), NOTHING);

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

        throw new OperationNotSupportedException();
    }

    private String getFixFormatCommentChar(String text) {
        int offset = COMMENT_POS - 1;
        if (text.length() <= offset) {
            return null;
        }
        return text.substring(offset, offset + COMMENT.length());
    }
}
