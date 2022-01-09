/*******************************************************************************
 * Copyright (c) 2007, 2015 Adam Neal and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Thanks to:
 *   Snippet031TableViewerCustomTooltipsMultiSelection ()
 * Contributors:
 *   Adam Neal - initial API and implementation
 *   Lars Vogel <Lars.Vogel@vogella.com> - Bug 414565, 475361
 *   Jeanderson Candido <http://jeandersonbc.github.io> - Bug 414565
 *   iSphere Project Owners
 *******************************************************************************/

package biz.isphere.core.swt.widgets.tableviewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * The listener that gets added to the table. This listener is responsible for
 * creating the tooltips when hovering over a cell item. This listener will
 * listen for the following events:
 * 
 * <li>SWT.KeyDown - to remove the tooltip</li>
 * <li>SWT.Dispose - to remove the tooltip</li>
 * <li>SWT.MouseMove - to remove the tooltip</li>
 * <li>SWT.MouseHover - to set the tooltip</li>
 * 
 * The tooltip text is provided by a label provider that implements interface
 * <code>biz.isphere.core.swt.widgets.tableviewer.TooltipProvider</code>.
 */
public class TooltipListener implements Listener {

    private Table table;
    private TooltipLabelListener tooltipLabelListener;

    private Shell tooltip = null;
    private Label label = null;
    private TooltipProvider labelProvider = null;

    public TooltipListener(TableViewer tableViewer) {

        this.table = tableViewer.getTable();
        this.tooltipLabelListener = new TooltipLabelListener();

        if (tableViewer.getLabelProvider() instanceof TooltipProvider) {
            this.labelProvider = (TooltipProvider)tableViewer.getLabelProvider();
        }
    }

    public void handleEvent(Event event) {

        if (labelProvider == null) {
            return;
        }

        switch (event.type) {
        case SWT.KeyDown:
        case SWT.Dispose:
        case SWT.MouseMove: {
            if (tooltip == null) break;
            tooltip.dispose();
            tooltip = null;
            label = null;
            break;
        }
        case SWT.MouseHover: {
            Point coords = new Point(event.x, event.y);
            TableItem item = table.getItem(coords);
            if (item != null) {
                int columnCount = table.getColumnCount();
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    if (item.getBounds(columnIndex).contains(coords)) {

                        /* Dispose of the old tooltip (if one exists */
                        if (tooltip != null && !tooltip.isDisposed()) {
                            tooltip.dispose();
                        }

                        String tooltipText = labelProvider.getTooltipText(item.getData(), columnIndex);
                        if (tooltipText != null) {

                            /* Create a new Tooltip */
                            tooltip = new Shell(table.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
                            tooltip.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                            FillLayout layout = new FillLayout();
                            layout.marginWidth = 2;
                            tooltip.setLayout(layout);
                            label = new Label(tooltip, SWT.NONE);
                            label.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                            label.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

                            /*
                             * Store the TableItem with the label so we can pass
                             * the mouse event later
                             */
                            label.setData("_TableItem_", item);

                            /* Set the tooltip text */
                            // label.setText("Tooltip: " + item.getData() + " :
                            // " + columnIndex);
                            label.setText(tooltipText);

                            /*
                             * Setup Listeners to remove the tooltip and
                             * transfer the received mouse events
                             */
                            label.addListener(SWT.MouseExit, tooltipLabelListener);
                            label.addListener(SWT.MouseDown, tooltipLabelListener);

                            /* Set the size and position of the tooltip */
                            Point size = tooltip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                            Rectangle rect = item.getBounds(columnIndex);
                            Point pt = table.toDisplay(rect.x, rect.y);
                            tooltip.setBounds(pt.x, pt.y, size.x, size.y);

                            /* Show it */
                            tooltip.setVisible(true);
                        }
                        break;
                    }
                }
            }
        }
        }
    }

}
