/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.handlers.contributions.extension.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.IDisplayJournalEntriesContributions;

public class DisplayJournalEntriesHandler {

    private static final String EXTENSION_ID = "biz.isphere.journalexplorer.rse.handlers.contributions.extension.point.IDisplayJournalEntriesContributions"; //$NON-NLS-1$

    private static IDisplayJournalEntriesContributions factory;

    public static boolean hasContribution() {

        if (getContributionsFactory() == null) {
            return false;
        }

        return true;
    }

    public static void handleDisplayFileJournalEntries(String connectionName, String libraryName, String fileName, String memberName) {

        IDisplayJournalEntriesContributions factory = getContributionsFactory();

        if (factory == null) {
            return;
        }

        factory.handleDisplayFileJournalEntries(connectionName, libraryName, fileName, memberName);
    }

    /**
     * Returns the RDi contributions if there is a registered extension for
     * that.
     * 
     * @return RDi contributions factory or null
     */
    private static IDisplayJournalEntriesContributions getContributionsFactory() {

        if (factory == null) {

            IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
            IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

            if (configElements != null && configElements.length > 0) {
                try {
                    final Object tempDialog = configElements[0].createExecutableExtension("class");
                    if (tempDialog instanceof IDisplayJournalEntriesContributions) {
                        factory = (IDisplayJournalEntriesContributions)tempDialog;
                    }
                } catch (CoreException e) {
                }
            }

        }

        return factory;
    }

}
