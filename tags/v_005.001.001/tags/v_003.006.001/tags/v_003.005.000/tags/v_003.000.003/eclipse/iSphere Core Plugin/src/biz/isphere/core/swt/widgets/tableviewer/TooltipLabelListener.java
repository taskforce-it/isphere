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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * This listener is added to the tooltip so that it can either dispose itself if
 * the mouse exits the tooltip or so it can pass the selection event through to
 * the table.
 */
final class TooltipLabelListener implements Listener {

    private boolean isCTRLDown(Event e) {
        return (e.stateMask & SWT.CTRL) != 0;
    }

    public void handleEvent(Event event) {

        Label label = (Label)event.widget;
        Shell shell = label.getShell();

        switch (event.type) {
        case SWT.MouseDown: /* Handle a user Click */

            /* Extract our Data */
            Event e = new Event();
            e.item = (TableItem)label.getData("_TableItem_");
            Table table = ((TableItem)e.item).getParent();

            /* Construct the new Selection[] to show */
            TableItem[] newSelection = null;
            if (isCTRLDown(event)) {
                /*
                 * We have 2 scenario's. 1) We are selecting an already selected
                 * element - so remove it from the selected indices 2) We are
                 * selecting a non-selected element - so add it to the selected
                 * indices
                 */
                TableItem[] sel = table.getSelection();
                for (int i = 0; i < sel.length; ++i) {
                    if (e.item.equals(sel[i])) {
                        // We are de-selecting this element
                        newSelection = new TableItem[sel.length - 1];
                        System.arraycopy(sel, 0, newSelection, 0, i);
                        System.arraycopy(sel, i + 1, newSelection, i, sel.length - i - 1);
                        break;
                    }
                }

                /*
                 * If we haven't created the newSelection[] yet, than we are
                 * adding the newly selected element into the list of selected
                 * indicies
                 */
                if (newSelection == null) {
                    newSelection = new TableItem[sel.length + 1];
                    System.arraycopy(sel, 0, newSelection, 0, sel.length);
                    newSelection[sel.length] = (TableItem)e.item;
                }

            } else {
                /* CTRL is not down, so we simply select the single element */
                newSelection = new TableItem[] { (TableItem)e.item };
            }
            /* Set the new selection of the table and notify the listeners */
            table.setSelection(newSelection);
            table.notifyListeners(SWT.Selection, e);

            /* Remove the Tooltip */
            shell.dispose();
            table.setFocus();
            break;
        case SWT.MouseExit:
            shell.dispose();
            break;
        }
    }
}