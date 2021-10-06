/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.externalapi;

import biz.isphere.core.messagefilecompare.rse.MessageFileCompareEditor;

/**
 * This interface specifies the configuration of a message file compare editor.
 * 
 * @see MessageFileCompareEditor
 */
public interface IMessageFileCompareEditorConfiguration {

    /**
     * Specifies whether the items of the left message file can be edited.
     * 
     * @return enabled
     */
    public boolean isLeftEditorEnabled();

    /**
     * Specifies whether the left message file can be changed.
     * 
     * @return enabled
     */
    public boolean isLeftSelectMessageFileEnabled();

    /**
     * Specifies whether the items of the right message file can be edited.
     * 
     * @return enabled
     */
    public boolean isRightEditorEnabled();

    /**
     * Specifies whether the right message file can be changed.
     * 
     * @return enabled
     */
    public boolean isRightSelectMessageFileEnabled();

}
