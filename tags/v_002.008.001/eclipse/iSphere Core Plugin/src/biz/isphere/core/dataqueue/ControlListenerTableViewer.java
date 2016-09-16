/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Resizes the message text column, when the table viewer is first displayed.
 */
public class ControlListenerTableViewer extends ControlAdapter {

    private boolean isResizing = false;

    private LabelProviderTableViewer labelProvider;

    public ControlListenerTableViewer(LabelProviderTableViewer labelProvider) {
        this.labelProvider = labelProvider;
    }

    @Override
    public void controlResized(ControlEvent event) {
        super.controlResized(event);

        if (event.getSource() instanceof Table) {

            if (!isResizing) {
                isResizing = true;

                int totalWidth = 0;

                Table table = (Table)event.getSource();
                for (int i = 0; i < table.getColumns().length; i++) {
                    TableColumn column = table.getColumns()[i];
                    if (!labelProvider.isLastColumn(i)) {
                        column.pack();
                        totalWidth = totalWidth + column.getWidth();
                    } else {
                        int width = table.getClientArea().width - totalWidth;
                        if (width < 100) {
                            column.pack();
                        } else {
                            column.setWidth(width);
                        }
                    }
                }

                isResizing = false;
                table.removeControlListener(this);
            }
        }
    }
}
