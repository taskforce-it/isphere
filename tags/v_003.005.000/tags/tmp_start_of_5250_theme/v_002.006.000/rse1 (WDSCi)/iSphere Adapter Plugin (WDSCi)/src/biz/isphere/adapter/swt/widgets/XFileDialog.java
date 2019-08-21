/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.adapter.swt.widgets;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.adapter.Messages;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;

public class XFileDialog implements IFileDialog {

    private static String platform = SWT.getPlatform();

    private FileDialog fileDialog;

    private boolean overwrite;
    private String filterPath;

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
    public XFileDialog(Shell aParent, int aStyle) {
        fileDialog = new FileDialog(aParent, aStyle);
    }

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     */
    public XFileDialog(Shell aParent) {
        fileDialog = new FileDialog(aParent);
    }

    /**
     * Makes the dialog visible and brings it to the front of the display.
     * 
     * @return a string describing the absolute path of the first selected file,
     *         or null if the dialog was cancelled or an error occurred
     * @throws SWTException
     *         <ul>
     *         <li>ERROR_WIDGET_DISPOSED - if the dialog has been disposed</li>
     *         <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread
     *         that created the dialog</li>
     *         </ul>
     */
    public String open() throws SWTException {
        String tFileName = null;
        boolean tCanOverwrite = false;
        while (!tCanOverwrite) {
            tFileName = fileDialog.open();
            if (tFileName == null) {
                return null;
            }

            File tFile = new File(tFileName);
            if (tFile.exists() && overwrite) {
                String tQuestion = Messages.bind(Messages.XFileDialog_OverwriteDialog_question, tFileName);
                MessageDialog tOverwriteDialog = new MessageDialog(fileDialog.getParent(), Messages.XFileDialog_OverwriteDialog_headline, null,
                    tQuestion, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
                        IDialogConstants.CANCEL_LABEL }, 0);
                int tOverwrite = tOverwriteDialog.open();
                switch (tOverwrite) {
                case 0: // Yes
                    tCanOverwrite = true;
                    break;
                case 1: // No
                    break;
                case 2: // Cancel
                default:
                    return null;
                }
            } else {
                tCanOverwrite = true;
            }
        }

        if (tFileName != null) {
            filterPath = retrieveFilterPath(tFileName);
        }

        return tFileName;
    }

    /**
     * Sets the flag that the dialog will use to determine whether to prompt the
     * user for file overwrite if the selected file already exists.
     * 
     * @param anOverwrite - true if the dialog will prompt for file overwrite,
     *        false otherwise
     */
    public void setOverwrite(boolean anOverwrite) {
        overwrite = anOverwrite;
    }

    /**
     * Returns the flag that the dialog will use to determine whether to prompt
     * the user for file overwrite if the selected file already exists.
     * 
     * @return true if the dialog will prompt for file overwrite, false
     *         otherwise
     */
    public boolean getOverwrite() {
        return overwrite;
    }

    /**
     * Sets the receiver's text, which is the string that the window manager
     * will typically display as the receiver's title, to the argument, which
     * must not be null.
     * 
     * @param aText - the new text
     */
    public void setText(String aText) {
        fileDialog.setText(aText);
    }

    /**
     * Set the initial filename which the dialog will select by default when
     * opened to the argument, which may be null. The name will be prefixed with
     * the filter path when one is supplied.
     * 
     * @param aFileName - the file name
     */
    public void setFileName(String aFileName) {
        fileDialog.setFileName(aFileName);
    }

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
    public void setFilterPath(String aFilterPath) {
        filterPath = aFilterPath;
        fileDialog.setFilterPath(aFilterPath);
    }

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
    public void setFilterNames(String[] aFilterNames) {
        fileDialog.setFilterNames(aFilterNames);
    }

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
    public void setFilterExtensions(String[] aFilterExtensions) {
        fileDialog.setFilterExtensions(aFilterExtensions);
    }

    /**
     * Returns the directory path that the dialog will use, or an empty string
     * if this is not set. File names in this path will appear in the dialog,
     * filtered according to the filter extensions.
     * 
     * @return the directory path string
     * 
     * @see #setFilterExtensions
     */
    public String getFilterPath() {
        return filterPath;
    }

    /*
     *  private methods
     */
    
    private String retrieveFilterPath(String fileName) {

        int p = fileName.lastIndexOf(File.separator);
        if (p >= 0) {
            return fileName.substring(p + 1);
        }

        return getDefaultRootDirectory();
    }

    private String getDefaultRootDirectory() {

        if (platform.equals("win32") || platform.equals("wpf")) {
            return "c:\\"; //$NON-NLS-1$
        } else {
            return "/"; //$NON-NLS-1$
        }
    }
}
