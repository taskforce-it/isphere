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

public interface ICommentDelegate {

    public void validate(boolean enable);

    public boolean isLineComment(String text);

    public String comment(String text) throws TextLimitExceededException, CommentExistsException;

    public String comment(String text, int startPos, int endPos) throws TextLimitExceededException, CommentExistsException,
        OperationNotSupportedException;

    public String uncomment(String text) throws OperationNotSupportedException, CommentNotFoundException;

    public String uncomment(String text, int startPos, int endPos) throws OperationNotSupportedException;

}
