/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class AutoScrollbarsListener implements Listener {

    public void handleEvent(Event event) {

        if (!(event.widget instanceof Text)) {
            return;
        }
        
        Text text = (Text)event.widget;
        if (text.getHorizontalBar() == null && text.getVerticalBar() == null) {
            return;
        }
        
        if (event.type != SWT.Modify && event.type != SWT.Resize) {
            return;
        }

        Rectangle r1 = text.getClientArea();
        Rectangle r2 = text.computeTrim(r1.x, r1.y, r1.width, r1.height);

        Point p;
        if ((text.getStyle() & SWT.WRAP) == SWT.WRAP) {
            p = text.computeSize(r1.x, SWT.DEFAULT, true);
        } else {
            p = text.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        }

        if (text.getHorizontalBar() != null) {
            text.getHorizontalBar().setVisible(r2.width <= p.x);
        }

        if (text.getVerticalBar() != null) {
            text.getVerticalBar().setVisible(r2.height <= p.y);
        }

        text.getParent().layout(true);
        text.showSelection();
    }
}
