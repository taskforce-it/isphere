/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.swt.widgets.datetime.DateEdit;
import biz.isphere.core.swt.widgets.datetime.TimeEdit;
import biz.isphere.core.swt.widgets.extension.DefaultDirectoryDialog;
import biz.isphere.core.swt.widgets.extension.DefaultFileDialog;
import biz.isphere.core.swt.widgets.extension.point.IDateEdit;
import biz.isphere.core.swt.widgets.extension.point.IDirectoryDialog;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
import biz.isphere.core.swt.widgets.extension.point.ITimeEdit;
import biz.isphere.core.swt.widgets.extension.point.IWidgetFactory;

public class WidgetFactoryContributionsHandler {

    private static final String EXTENSION_ID = "biz.isphere.core.swt.widgets.extension.point.IWidgetFactory";

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
    public IFileDialog getFileDialog(Shell aParent, int aStyle) {

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new DefaultFileDialog(aParent, aStyle);
        }

        return factory.getFileDialog(aParent, aStyle);
    }

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     */
    public IFileDialog getFileDialog(Shell aParent) {

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new DefaultFileDialog(aParent);
        }

        return factory.getFileDialog(aParent);
    }

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
    public IDirectoryDialog getDirectoryDialog(Shell aParent, int aStyle) {

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new DefaultDirectoryDialog(aParent);
        }

        return factory.getDirectoryDialog(aParent, aStyle);
    }

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     * @return the directory dialog
     * @see org.eclipse.swt.widgets.DirectoryDialog
     */
    public IDirectoryDialog getDirectoryDialog(Shell aParent) {

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new DefaultDirectoryDialog(aParent);
        }

        return factory.getDirectoryDialog(aParent);
    }

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
    public IDateEdit getDateEdit(Composite aParent, int style) {
        checkStyle(style, SWT.BORDER);

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new DateEdit(aParent, style);
        }

        IDateEdit dateEdit = factory.getDateEdit(aParent, style);
        if (dateEdit == null) {
            dateEdit = new DateEdit(aParent, style);
        }

        return dateEdit;
    }

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
    public ITimeEdit getTimeEdit(Composite aParent, int style) {
        checkStyle(style, SWT.BORDER);

        IWidgetFactory factory = getWidgetFactory();

        if (factory == null) {
            return new TimeEdit(aParent, style);
        }

        ITimeEdit timeEdit = factory.getTimeEdit(aParent, style);
        if (timeEdit == null) {
            timeEdit = new TimeEdit(aParent, style);
        }

        return timeEdit;
    }

    private void checkStyle(int style, int mask) {

        if ((style | mask) != mask) {
            throw new RuntimeException("Unsupported style bit set.");
        }
    }

    /**
     * Returns the widget factory if there is a registered extension for that.
     * 
     * @return widget factory or null
     */
    private IWidgetFactory getWidgetFactory() {
        IWidgetFactory factory = null;

        IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

        if (configElements != null && configElements.length > 0) {
            try {
                final Object tempDialog = configElements[0].createExecutableExtension("class");
                if (tempDialog instanceof IWidgetFactory) {
                    factory = (IWidgetFactory)tempDialog;
                }
            } catch (CoreException e) {
            }
        }
        return factory;
    }

}
