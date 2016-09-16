/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
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
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.swt.widgets.extension.DefaultFileDialog;
import biz.isphere.core.swt.widgets.extension.point.IFileDialog;
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

        return factory.getDialog(aParent, aStyle);
    }

    /**
     * Constructs a new instance of this class given only its parent.
     * 
     * @param aParent - a shell which will be the parent of the new instance
     */
    public IFileDialog getFileDialog(Shell aParent) {
        IFileDialog dialog = null;

        return dialog;
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
