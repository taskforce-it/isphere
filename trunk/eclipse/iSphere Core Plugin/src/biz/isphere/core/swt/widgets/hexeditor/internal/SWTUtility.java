/*
 * javahexeditor, a java hex editor
 * Copyright (C) 2006, 2009 Jordi Bergenthal, pestatije(-at_)users.sourceforge.net
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
 */

package biz.isphere.core.swt.widgets.hexeditor.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

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
     * @param task
     *            independent of the user interface thread (no widgets used)
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

    /**
     * Helper method to make a shell come closer to another shell
     * 
     * @param fixedShell
     *            where movingShell will get closer to
     * @param movingShell
     *            shell to be relocated
     */
    public static void reduceDistance(Shell fixedShell, Shell movingShell) {
	if (fixedShell == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'fixedShell' must not be null.");
	}
	if (movingShell == null) {
	    throw new IllegalArgumentException(
		    "Parameter 'movingShell' must not be null.");
	}
	movingShell.pack();

	Rectangle fixed = fixedShell.getBounds();
	Rectangle moving = movingShell.getBounds();
	int[] fixedLower = { fixed.x, fixed.y };
	int[] fixedHigher = { fixed.x + fixed.width, fixed.y + fixed.height };
	int[] movingLower = { moving.x, moving.y };
	int[] movingSpan = { moving.width, moving.height };

	for (int i = 0; i < 2; ++i) {
	    if (movingLower[i] + movingSpan[i] < fixedLower[i])
		movingLower[i] = fixedLower[i] - movingSpan[i] + 10;
	    else if (fixedHigher[i] < movingLower[i])
		movingLower[i] = fixedHigher[i] - 10;
	}
	movingShell.setLocation(movingLower[0], movingLower[1]);
    }

    public static int showMessage(Shell shell, int style, String title,
	    String message, String... parameters) {
	MessageBox messageBox = new MessageBox(shell, style);
	messageBox.setText(title);
	messageBox.setMessage(TextUtility.format(message, parameters));
	return messageBox.open();
    }

    public static int showErrorMessage(Shell shell, String title,
	    String message, String... parameters) {
	return showMessage(shell, SWT.ERROR | SWT.OK, title, message,
		parameters);

    }
}
