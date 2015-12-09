/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditor;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.widgets.Composite;

public final class StatusLineContributionItem extends ContributionItem {

    private StatusLine statusLine;

    public StatusLineContributionItem(String id) {
        super(id);
    }

    @Override
    public void fill(Composite parent) {
        createStatusPart(parent, true);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    private void createStatusPart(Composite parent, boolean b) {
        statusLine = new StatusLine(parent);
    }
}
