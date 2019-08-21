/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.delegates;

import biz.isphere.lpex.comments.lpex.exceptions.CommentExistsException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

import com.ibm.lpex.core.LpexView;

public class CLCommentsDelegate extends AbstractCommentDelegate implements ICommentDelegate {

    private static final String START_COMMENT = "/* "; //$NON-NLS-1$ 
    private static final String END_COMMENT = " */"; //$NON-NLS-1$

    /**
     * Specifies whether the delegate is in validation mode.
     */
    private boolean validate;

    public CLCommentsDelegate(LpexView view) {
        super(view);
    }

    public void setValidationMode(boolean enable) {
        this.validate = enable;
    }

    public boolean isLineComment(String text) {
        return startsWithComment(text) && endsWithComment(text);
    }

    /**
     * Comments a complete line.
     * 
     * @parm text - line to comment
     * @throws TextLimitExceededException
     */
    public String comment(String text) throws TextLimitExceededException, CommentExistsException {

        int s = text.indexOf(START_COMMENT.trim());
        if (s >= 0) {
            s = s + START_COMMENT.trim().length();
            int e = text.indexOf(END_COMMENT.trim());
            if (e > 0) {
                s = text.indexOf(START_COMMENT.trim(), s);
                if (s >= 0 && s < e) {
                    throw new CommentExistsException();
                }
            }
        }

        StringBuilder buffer = new StringBuilder(START_COMMENT);
        buffer.append(text);

        if (startsWithComment(text)) {
            s = START_COMMENT.length();
        } else {
            s = START_COMMENT.trim().length();
        }
        while (buffer.length() > s && buffer.length() > getLineLength() && SPACE.equals(buffer.substring(s, s + 1))) {
            buffer.replace(s, s + 1, NOTHING);
        }

        if (!endsWithComment(buffer.toString())) {
            expandLine(buffer, getLineLength() - END_COMMENT.length());
            buffer.append(END_COMMENT);
        }

        s = buffer.length() - END_COMMENT.trim().length() - 1;
        while (buffer.length() > s && buffer.length() > getLineLength() && SPACE.equals(buffer.substring(s, s + 1))) {
            buffer.replace(s, s + 1, NOTHING);
            s--;
        }

        s = START_COMMENT.trim().length();
        while (buffer.length() > s && buffer.length() > getLineLength() && SPACE.equals(buffer.substring(s, s + 1))) {
            buffer.replace(s, s + 1, NOTHING);
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
    public String comment(String text, int startPos, int endPos) throws TextLimitExceededException, CommentExistsException {

        startPos--;
        endPos--;

        boolean hasStartComment = false;
        boolean hasEndComment = false;

        // Check start comment
        int p = startPos + (endPos - startPos) / 2;
        int i = p;
        while (i >= 0 && !hasStartComment) {
            if (i + START_COMMENT.trim().length() < text.length()) {
                if (START_COMMENT.trim().equals(text.substring(i, i + START_COMMENT.trim().length()))) {
                    hasStartComment = true;
                }
            }
            if (i < startPos && i + END_COMMENT.trim().length() < text.length()) {
                if (END_COMMENT.trim().equals(text.substring(i, i + END_COMMENT.trim().length()))) {
                    break;
                }
            }
            i--;
        }

        // Check end comment
        i = p;
        while (i < text.length() && !hasEndComment) {
            if (i + END_COMMENT.trim().length() < text.length()) {
                if (END_COMMENT.trim().equals(text.substring(i, i + END_COMMENT.trim().length()))) {
                    hasEndComment = true;
                }
            }
            if (i > endPos && i + START_COMMENT.trim().length() < text.length()) {
                if (START_COMMENT.trim().equals(text.substring(i, i + START_COMMENT.trim().length()))) {
                    break;
                }
            }
            i++;
        }

        if (hasStartComment || hasEndComment) {
            throw new CommentExistsException();
        }

        StringBuilder buffer = new StringBuilder(text);
        buffer.insert(startPos, START_COMMENT.trim());
        endPos = endPos + START_COMMENT.trim().length();
        buffer.insert(endPos, END_COMMENT.trim());

        while (buffer.length() > getLineLength() && endPos < buffer.length() && SPACE.equals(buffer.substring(endPos, endPos + 1))) {
            buffer.replace(endPos, endPos + 1, NOTHING);
        }

        startPos = startPos + START_COMMENT.trim().length();
        while (buffer.length() > getLineLength() && startPos < buffer.length() && SPACE.equals(buffer.substring(startPos, startPos + 1))) {
            buffer.replace(startPos, startPos + 1, NOTHING);
            endPos--;
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
     * Uncomments a complete line.
     * 
     * @parm text - line to uncomment
     */
    public String uncomment(String text) {

        if (!startsWithComment(text)) {
            return uncomment(text, getCursorPosition(), getCursorPosition());
        }

        if (!endsWithComment(text)) {
            return uncomment(text, getCursorPosition(), getCursorPosition());
        }

        if (validate) {
            return text;
        }

        StringBuilder buffer = new StringBuilder(text);

        // Remove start comment
        int startIndex = text.indexOf(START_COMMENT.trim());
        int endIndex = startIndex + START_COMMENT.trim().length();
        for (int i = startIndex; i < endIndex; i++) {
            buffer.replace(startIndex, startIndex + 1, NOTHING);
        }

        if (SPACE.equals(buffer.substring(0, 0 + 1))) {
            buffer.replace(0, 0 + 1, NOTHING);
        }

        // Remove end comment
        if (!hasStartComment(buffer.toString())) {
            startIndex = buffer.lastIndexOf(END_COMMENT.trim());
            endIndex = startIndex + END_COMMENT.trim().length();
            for (int i = startIndex; i < endIndex; i++) {
                buffer.replace(i, i + 1, SPACE);
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
    public String uncomment(String text, int startPos, int endPos) {

        if (validate) {
            return text;
        }

        if (startPos == endPos) {
            startPos--;
        } else {
            startPos = startPos + (endPos - startPos) / 2;
        }

        int s = text.substring(0, startPos + 1).lastIndexOf(START_COMMENT.trim());
        if (s < 0) {
            return text;
        }

        int e = text.indexOf(END_COMMENT.trim(), startPos);
        if (e < 0) {
            return text;
        }

        StringBuilder buffer = new StringBuilder(text);

        buffer.replace(s, s + START_COMMENT.trim().length(), NOTHING);

        e = e - START_COMMENT.trim().length();
        buffer.replace(e, e + END_COMMENT.trim().length(), NOTHING);

        return buffer.toString();
    }

    private boolean hasStartComment(String text) {
        return text.indexOf(START_COMMENT.trim()) >= 0;
    }

    private boolean startsWithComment(String text) {
        return text.trim().startsWith(START_COMMENT.trim());
    }

    private boolean endsWithComment(String text) {
        return text.trim().endsWith(END_COMMENT.trim());
    }

    private void expandLine(StringBuilder buffer, int length) {
        while (buffer.length() < length) {
            buffer.append(SPACE);
        }
    }
}
