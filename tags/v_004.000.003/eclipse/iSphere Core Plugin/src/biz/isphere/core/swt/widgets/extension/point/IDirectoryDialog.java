/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension.point;

import org.eclipse.swt.SWTException;

public interface IDirectoryDialog {

    /**
     * Makes the dialog visible and brings it to the front of the display.
     * 
     * @return a string describing the absolute path of the selected directory,
     *         or null if the dialog was cancelled or an error occurred
     * @throws SWTException <ul>
     *         <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
     *         <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread
     *         that created the dialog</li>
     *         </ul>
     */
    public String open() throws SWTException;

    /**
     * Sets the receiver's text, which is the string that the window manager
     * will typically display as the receiver's title, to the argument, which
     * must not be null.
     * 
     * @param aText - the new text
     */
    public void setText(String aText);

    /**
     * Sets the path that the dialog will use to filter the directories it shows
     * to the argument, which may be null. If the string is null, then the
     * operating system's default filter path will be used.
     * <p>
     * Note that the path string is platform dependent. For convenience, either
     * '/' or '\' can be used as a path separator.
     * 
     * @param aFilterPath - the filter path
     */
    public void setFilterPath(String aFilterPath);

    /**
     * Returns the path which the dialog will use to filter the directories it
     * shows.
     * 
     * @return the filter path
     * @see #setFilterPath(String)
     */
    public String getFilterPath();
}
