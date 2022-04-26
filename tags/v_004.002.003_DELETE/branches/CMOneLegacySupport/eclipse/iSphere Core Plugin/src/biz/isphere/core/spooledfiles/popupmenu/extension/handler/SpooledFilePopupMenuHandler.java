/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.popupmenu.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributionItem;
import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributions;

public class SpooledFilePopupMenuHandler {

    private static final String EXTENSION_ID = "biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributions"; //$NON-NLS-1$

    private static ISpooledFilePopupMenuContributions factory;

    public static boolean hasContribution() {

        if (getContributionsFactory() == null) {
            return false;
        }

        return true;
    }

    public ISpooledFilePopupMenuContributionItem[] getContributionItems() {

        ISpooledFilePopupMenuContributions factory = getContributionsFactory();

        if (factory == null) {
            return new ISpooledFilePopupMenuContributionItem[0];
        }

        return factory.getContributionItems();
    }

    /**
     * Returns the RDi contributions if there is a registered extension for
     * that.
     * 
     * @return RDi contributions factory or null
     */
    private static ISpooledFilePopupMenuContributions getContributionsFactory() {

        if (factory == null) {

            IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
            IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

            if (configElements != null && configElements.length > 0) {
                try {
                    final Object tempDialog = configElements[0].createExecutableExtension("class");
                    if (tempDialog instanceof ISpooledFilePopupMenuContributions) {
                        factory = (ISpooledFilePopupMenuContributions)tempDialog;
                    }
                } catch (CoreException e) {
                }
            }

        }

        return factory;
    }

}
