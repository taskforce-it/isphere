/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.externalapi;

import biz.isphere.core.objectsynchronization.rse.SynchronizeMembersEditor;

/**
 * This interface specifies the configuration of a synchronize members editor.
 * 
 * @see SynchronizeMembersEditor
 */
public interface ISynchronizeMembersEditorConfiguration {

    /**
     * Specifies whether the items of the left object can be edited.
     * 
     * @return enabled
     */
    public boolean isLeftEditorEnabled();

    /**
     * Specifies whether the left object can be changed.
     * 
     * @return enabled
     */
    public boolean isLeftSelectObjectEnabled();

    /**
     * Specifies whether the items of the right object can be edited.
     * 
     * @return enabled
     */
    public boolean isRightEditorEnabled();

    /**
     * Specifies whether the right object can be changed.
     * 
     * @return enabled
     */
    public boolean isRightSelectObjectEnabled();

}
