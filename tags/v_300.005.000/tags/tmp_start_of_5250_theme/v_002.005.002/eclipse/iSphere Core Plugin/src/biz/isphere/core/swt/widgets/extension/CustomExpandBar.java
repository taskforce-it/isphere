/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CustomExpandBar extends ExpandBar {

    private Shell shell;
    private CustomExpandBar expandBar;

    public CustomExpandBar(Composite parent, int style) {

        super(parent, style);

        shell = parent.getShell();

        expandBar = this;

        activateSizeComputer();

    }

    private void activateSizeComputer() {

        Listener listener = new Listener() {

            public void handleEvent(Event e) {

                shell.getDisplay().asyncExec(new Runnable() {

                    public void run() {

                        ExpandItem[] items = expandBar.getItems();
                        Rectangle area = expandBar.getClientArea();
                        int spacing = expandBar.getSpacing();

                        int minimumHeightForAllExpandItems = 0;
                        int availableHeightForAllExpandItems = area.height;
                        int availableHeightForExpandItemsWithGrabExcess = area.height;
                        int expandedExpandItemsWithGrabExcess = 0;

                        for (int idx = 0; idx < items.length; idx++) {
                            CustomExpandItem item = (CustomExpandItem)items[idx];
                            minimumHeightForAllExpandItems += item.getHeaderHeight() + spacing;
                            availableHeightForExpandItemsWithGrabExcess -= item.getHeaderHeight() + spacing;
                            if (item.getExpanded()) {
                                minimumHeightForAllExpandItems += item.getMinimumHeight();
                                if (item.getType() == CustomExpandItem.TYPE_FIX_HEIGHT || item.getType() == CustomExpandItem.TYPE_PACK_HEIGHT) {
                                    availableHeightForExpandItemsWithGrabExcess -= item.getMinimumHeight();
                                } else {
                                    expandedExpandItemsWithGrabExcess++;
                                }
                            }
                        }

                        minimumHeightForAllExpandItems += spacing;
                        availableHeightForExpandItemsWithGrabExcess -= spacing;

                        for (int idx = 0; idx < items.length; idx++) {
                            CustomExpandItem item = (CustomExpandItem)items[idx];
                            if (availableHeightForAllExpandItems >= minimumHeightForAllExpandItems && item.getExpanded()
                                && item.getType() != CustomExpandItem.TYPE_FIX_HEIGHT && item.getType() != CustomExpandItem.TYPE_PACK_HEIGHT
                                && availableHeightForExpandItemsWithGrabExcess / expandedExpandItemsWithGrabExcess < item.getMinimumHeight()) {
                                item.setTemporaryType(CustomExpandItem.TYPE_FIX_HEIGHT);
                                expandedExpandItemsWithGrabExcess--;
                                availableHeightForExpandItemsWithGrabExcess -= item.getMinimumHeight();
                            } else {
                                item.setTemporaryType(item.getType());
                            }
                        }

                        for (int idx = 0; idx < items.length; idx++) {
                            CustomExpandItem item = (CustomExpandItem)items[idx];
                            if (item.getExpanded()) {
                                if (availableHeightForAllExpandItems < minimumHeightForAllExpandItems) {
                                    item.setHeight(item.getMinimumHeight());
                                } else {
                                    if (item.getTemporaryType() == CustomExpandItem.TYPE_FIX_HEIGHT
                                        || item.getTemporaryType() == CustomExpandItem.TYPE_PACK_HEIGHT) {
                                        item.setHeight(item.getMinimumHeight());
                                    } else {
                                        item.setHeight(availableHeightForExpandItemsWithGrabExcess / expandedExpandItemsWithGrabExcess);
                                    }
                                }
                            } else {
                                item.setHeight(0);
                            }
                        }

                    }

                });

            }

        };
        expandBar.addListener(SWT.Resize, listener);
        expandBar.addListener(SWT.Expand, listener);
        expandBar.addListener(SWT.Collapse, listener);

    }

    public void alignHeight() {
        ExpandItem[] items = expandBar.getItems();
        int maximum = 0;
        for (int idx = 0; idx < items.length; idx++) {
            CustomExpandItem item = (CustomExpandItem)items[idx];
            if (item.getMinimumHeight() > maximum) {
                maximum = item.getMinimumHeight();
            }
        }
        for (int idx = 0; idx < items.length; idx++) {
            CustomExpandItem item = (CustomExpandItem)items[idx];
            item.setMinimumHeight(maximum);
        }
    }

    @Override
    protected void checkSubclass() {
    }

}
