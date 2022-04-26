/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.comparefilter.contributions.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import biz.isphere.core.comparefilter.contributions.extension.point.ICompareFilterContributions;

public class CompareFilterContributionsHandler {

    private static final String EXTENSION_ID = "biz.isphere.core.comparefilter.contributions.extension.point.ICompareFilterContributions"; //$NON-NLS-1$

    public static boolean hasContribution() {

        if (getContributionsFactory() == null) {
            return false;
        }

        return true;
    }

    public static String[] getFileExtensions() {

        ICompareFilterContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getFileExtensions();
    }

    public static String[] getDefaultFileExtensions() {

        ICompareFilterContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getDefaultFileExtensions();
    }

    public static void setFileExtensions(String[] extensions) {

        ICompareFilterContributions factory = getContributionsFactory();

        if (factory == null) {
            return;
        }

        factory.setFileExtensions(extensions);
    }

    public static String getImportExportLocation() {

        ICompareFilterContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getImportExportLocation();
    }

    public static void setImportExportLocation(String location) {

        ICompareFilterContributions factory = getContributionsFactory();

        if (factory == null) {
            return;
        }

        factory.setImportExportLocation(location);
    }

    /**
     * Returns the compare filter contributions if there is a registered
     * extension for that.
     * 
     * @return compare filter contributions factory or null
     */
    private static ICompareFilterContributions getContributionsFactory() {

        ICompareFilterContributions factory = null;

        IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

        if (configElements != null && configElements.length > 0) {
            try {
                final Object tempDialog = configElements[0].createExecutableExtension("class"); //$NON-NLS-1$
                if (tempDialog instanceof ICompareFilterContributions) {
                    factory = (ICompareFilterContributions)tempDialog;
                }
            } catch (CoreException e) {
            }
        }
        return factory;
    }

}
