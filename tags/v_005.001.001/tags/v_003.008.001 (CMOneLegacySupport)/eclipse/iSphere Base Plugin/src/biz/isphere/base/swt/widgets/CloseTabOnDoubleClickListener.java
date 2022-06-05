/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.swt.widgets;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

/**
 * This listener closes the CTabItem that is clicked with the middle mouse
 * button.
 */
public class CloseTabOnDoubleClickListener extends MouseAdapter {

    private static final int MIDDLE_BUTTON = 2;

    public void mouseUp(MouseEvent event) {
        if (event.button == MIDDLE_BUTTON && isTabFolder(event)) {
            CTabFolder tabFolder = (CTabFolder)event.getSource();
            if (tabFolder != null && tabFolder.getItemCount() > 0) {
                CTabItem tabItem = tabFolder.getItem(new Point(event.x, event.y));
                if (tabItem != null) {
                    tabItem.dispose();
                }
            }
        }
    }

    private boolean isTabFolder(MouseEvent event) {

        if (event.getSource() instanceof CTabFolder) {
            return true;
        }
        return false;
    }
}
