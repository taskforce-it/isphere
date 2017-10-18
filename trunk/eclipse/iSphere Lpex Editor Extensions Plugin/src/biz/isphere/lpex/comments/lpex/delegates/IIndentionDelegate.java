/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.comments.lpex.delegates;

import biz.isphere.lpex.comments.lpex.exceptions.FixedFormatNotSupportedException;
import biz.isphere.lpex.comments.lpex.exceptions.MaxLeftMarginReachedException;
import biz.isphere.lpex.comments.lpex.exceptions.TextLimitExceededException;

public interface IIndentionDelegate {

    public void setValidationMode(boolean enable);

    public String indent(String text) throws FixedFormatNotSupportedException, TextLimitExceededException;

    public String unindent(String text) throws FixedFormatNotSupportedException, MaxLeftMarginReachedException;

}
