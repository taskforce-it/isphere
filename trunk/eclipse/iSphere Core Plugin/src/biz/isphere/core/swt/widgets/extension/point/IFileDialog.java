/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension.point;

import org.eclipse.swt.SWTException;

public interface IFileDialog {

    /**
     * Makes the dialog visible and brings it to the front of the display.
     * 
     * @return a string describing the absolute path of the first selected file,
     *         or null if the dialog was cancelled or an error occurred
     * @throws SWTException <ul>
     *         <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
     *         <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread
     *         that created the dialog</li>
     *         </ul>
     */
    public String open() throws SWTException;

    /**
     * Sets the flag that the dialog will use to determine whether to prompt the
     * user for file overwrite if the selected file already exists.
     * 
     * @param anOverwrite - true if the dialog will prompt for file overwrite,
     *        false otherwise
     */
    public void setOverwrite(boolean anOverwrite);

    /**
     * Returns the flag that the dialog will use to determine whether to prompt
     * the user for file overwrite if the selected file already exists.
     * 
     * @return true if the dialog will prompt for file overwrite, false
     *         otherwise
     */
    public boolean getOverwrite();

    /**
     * Sets the receiver's text, which is the string that the window manager
     * will typically display as the receiver's title, to the argument, which
     * must not be null.
     * 
     * @param aText - the new text
     */
    public void setText(String aText);

    /**
     * Set the initial filename which the dialog will select by default when
     * opened to the argument, which may be null. The name will be prefixed with
     * the filter path when one is supplied.
     * 
     * @param aFileName - the file name
     */
    public void setFileName(String aFileName);

    /**
     * Sets the directory path that the dialog will use to the argument, which
     * may be null. File names in this path will appear in the dialog, filtered
     * according to the filter extensions. If the string is null, then the
     * operating system's default filter path will be used.
     * <p>
     * Note that the path string is platform dependent. For convenience, either
     * '/' or '\' can be used as a path separator.
     * 
     * @param aFilterPath - the directory path
     */
    public void setFilterPath(String aFilterPath);

    /**
     * Sets the names that describe the filter extensions which the dialog will
     * use to filter the files it shows to the argument, which may be null.
     * <p>
     * Each name is a user-friendly short description shown for its
     * corresponding filter. The names array must be the same length as the
     * <code>extensions</code> array.
     * 
     * @param aFilterNames - the list of filter names, or null for no filter
     *        names
     */
    public void setFilterNames(String[] aFilterNames);

    /**
     * Set the file extensions which the dialog will use to filter the files it
     * shows to the argument, which may be null.
     * <p>
     * The strings are platform specific. For example, on some platforms, an
     * extension filter string is typically of the form "*.extension", where
     * "*.*" matches all files. For filters with multiple extensions, use
     * semicolon as a separator, e.g. "*.jpg;*.png".
     * 
     * @param aFilterExtensions - the file extension filter
     */
    public void setFilterExtensions(String[] aFilterExtensions);

}
