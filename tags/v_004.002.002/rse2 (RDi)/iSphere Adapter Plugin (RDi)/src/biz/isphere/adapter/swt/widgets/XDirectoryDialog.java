/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.swt.widgets;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.swt.widgets.extension.point.IDirectoryDialog;

public class XDirectoryDialog implements IDirectoryDialog {

    private DirectoryDialog directoryDialog;

    /**
     * Constructs a new instance of this class given its parent and a style
     * value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class SWT
     * which is applicable to instances of this class, or must be built by
     * bitwise OR'ing together (that is, using the int "|" operator) two or more
     * of those SWT style constants. The class description lists the style
     * constants that are applicable to the class. Style bits are also inherited
     * from superclasses.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     * @param aStyle - the style of dialog to construct
     */
    public XDirectoryDialog(Shell aParent, int aStyle) {
        directoryDialog = new DirectoryDialog(aParent, aStyle);
    }

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     */
    public XDirectoryDialog(Shell aParent) {
        directoryDialog = new DirectoryDialog(aParent);
    }

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
    public String open() throws SWTException {
        return directoryDialog.open();
    }

    /**
     * Sets the receiver's text, which is the string that the window manager
     * will typically display as the receiver's title, to the argument, which
     * must not be null.
     * 
     * @param aText - the new text
     */
    public void setText(String aText) {
        directoryDialog.setText(aText);
    }

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
    public void setFilterPath(String aFilterPath) {
        directoryDialog.setFilterPath(aFilterPath);
    }

    /**
     * Returns the path which the dialog will use to filter the directories it
     * shows.
     * 
     * @return the filter path
     * @see #setFilterPath(String)
     */
    public String getFilterPath() {
        return directoryDialog.getFilterPath();
    }
}
