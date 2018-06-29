/*******************************************************************************
 * javahexeditor, a java hex editor
 * Copyright (C) 2006-2015 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
 * The official javahexeditor site is sourceforge.net/projects/javahexeditor
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.core.swt.widgets.hexeditor.internal;

import org.eclipse.swt.widgets.Display;

/**
 * Utility class to handle SWT widgets.
 * 
 * @author Peter Dell
 */
public final class SWTUtility {

    /**
     * Blocks the caller until the task is finished. Does not block the user
     * interface thread.
     * 
     * @param task independent of the user interface thread (no widgets used)
     */
    public static void blockUntilFinished(Runnable task) {
        Thread thread = new Thread(task);
        thread.start();
        Display display = Display.getCurrent();
        final boolean[] pollerEnabled = { false };
        while (thread.isAlive() && !display.isDisposed()) {
            if (!display.readAndDispatch()) {
                // awake periodically so it returns when task has finished
                if (!pollerEnabled[0]) {
                    pollerEnabled[0] = true;
                    display.timerExec(300, new Runnable() {
                        public void run() {
                            pollerEnabled[0] = false;
                        }
                    });
                }
                display.sleep();
            }
        }
    }
}
