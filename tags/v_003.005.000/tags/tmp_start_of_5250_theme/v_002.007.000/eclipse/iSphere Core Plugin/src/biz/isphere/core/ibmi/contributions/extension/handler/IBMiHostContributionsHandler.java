/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ibmi.contributions.extension.handler;

import java.sql.Connection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions;

import com.ibm.as400.access.AS400;

public class IBMiHostContributionsHandler {

    private static final String EXTENSION_ID = "biz.isphere.core.ibmi.contributions.extension.point.IIBMiHostContributions";

    public static boolean hasContribution() {
        
        if (getContributionsFactory() == null) {
            return false;
        }
        
        return true;
    }
    
    public static AS400 findSystem(String hostName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.findSystem(hostName);
    }
    
    public static AS400 getSystem(String connectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getSystem(connectionName);
    }

    public static Connection getJdbcConnection(String connectionName) {

        IIBMiHostContributions factory = getContributionsFactory();

        if (factory == null) {
            return null;
        }

        return factory.getJdbcConnection(connectionName);
    }

    /**
     * Returns the RDi contributions if there is a registered extension for
     * that.
     * 
     * @return RDi contributions factory or null
     */
    private static IIBMiHostContributions getContributionsFactory() {
        
        IIBMiHostContributions factory = null;

        IExtensionRegistry tRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] configElements = tRegistry.getConfigurationElementsFor(EXTENSION_ID);

        if (configElements != null && configElements.length > 0) {
            try {
                final Object tempDialog = configElements[0].createExecutableExtension("class");
                if (tempDialog instanceof IIBMiHostContributions) {
                    factory = (IIBMiHostContributions)tempDialog;
                }
            } catch (CoreException e) {
            }
        }
        return factory;
    }

}
