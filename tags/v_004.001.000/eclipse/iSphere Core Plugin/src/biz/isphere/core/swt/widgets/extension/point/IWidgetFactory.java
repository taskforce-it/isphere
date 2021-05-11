/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension.point;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public interface IWidgetFactory {

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
     * @return the file dialog
     * @see org.eclipse.swt.widgets.FileDialog
     */
    public IFileDialog getFileDialog(Shell aParent, int aStyle);

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     * @return the file dialog
     * @see org.eclipse.swt.widgets.FileDialog
     */
    public IFileDialog getFileDialog(Shell aParent);

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
     * @return the directory dialog
     * @see org.eclipse.swt.widgets.DirectoryDialog
     */
    public IDirectoryDialog getDirectoryDialog(Shell aParent, int aStyle);

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     * @return the directory dialog
     * @see org.eclipse.swt.widgets.DirectoryDialog
     */
    public IDirectoryDialog getDirectoryDialog(Shell aParent);

    /**
     * Produces a new date edit control. If the underlaying Eclipse has a
     * DateTime class, the date edit is a DateTime object, else a simple
     * DateEdit object is returned.
     * 
     * @param aParent - parent composite
     * @param style - style bits. Supported styles are: SWT.BORDER
     * @return the date edit control
     * @see org.eclipse.swt.widgets.DateTime
     * @see biz.isphere.core.swt.widgets.datetime.DateEdit
     */
    public IDateEdit getDateEdit(Composite aParent, int style);

    /**
     * Produces a new time edit control. If the underlaying Eclipse has a
     * DateTime class, the time edit is a DateTime object, else a simple
     * TimeEdit object is returned.
     * 
     * @param aParent - parent composite
     * @param style - style bits. Supported styles are: SWT.BORDER
     * @return the time edit control
     * @see org.eclipse.swt.widgets.DateTime
     * @see biz.isphere.core.swt.widgets.datetime.TimeEdit
     */
    public ITimeEdit getTimeEdit(Composite aParent, int style);
}
