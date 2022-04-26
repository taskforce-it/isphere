/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import biz.isphere.core.preferences.Preferences;

public class SourceMemberCompareEditorConfiguration extends CompareEditorConfiguration {

    public SourceMemberCompareEditorConfiguration() {
        super();

        setProperty(IGNORE_WHITESPACE, Preferences.getInstance().isSourceMemberCompareIgnoreWhiteSpaces());
    }

}
