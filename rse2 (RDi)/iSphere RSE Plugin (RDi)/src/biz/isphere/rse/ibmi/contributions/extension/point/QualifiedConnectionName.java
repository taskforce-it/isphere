/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import org.eclipse.rse.core.model.IHost;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

/**
 * This class represents a remote system connection name. A connection name
 * consists of two parts:
 * <ul>
 * <li>profile name</li>
 * <li>connection name</li>
 * </ul>
 * The format of a qualified connection name is
 * <code>'profileName:connectionName'</code>.
 */
public class QualifiedConnectionName extends biz.isphere.core.ibmi.contributions.extension.point.BasicQualifiedConnectionName {

    /**
     * Constructs a qualified connection name object from a given remote system
     * host.
     * 
     * @param host - A remote system host.
     */
    public QualifiedConnectionName(IHost host) {
        this(host.getSystemProfileName(), host.getAliasName());
    }

    /**
     * Constructs a qualified connection name object from a given remote system
     * connection.
     * 
     * @param host - A remote system connection.
     */
    public QualifiedConnectionName(IBMiConnection connection) {
        this(connection.getProfileName(), connection.getConnectionName());
    }

    /**
     * Constructs a qualified connection name object from given profile and
     * connection names.
     * 
     * @param profileName - A remote system profile name.
     * @param connectionName - A remote system connection name.
     */
    public QualifiedConnectionName(String profileName, String connectionName) {
        super(profileName, connectionName);
    }

    /**
     * Constructs a qualified connection name object from a qualified connection
     * name string, which must be formatted as
     * <code>'profileName:connectionName'</code>. The profile name is
     * optional.<br>
     * When the profile name is missing, the constructor falls back to the
     * default system profile name.
     * 
     * @param qualifiedConnectionName - A qualified connection name of format
     *        <code>'profileName:connectionName'</code>.
     */
    public QualifiedConnectionName(String qualifiedConnectionName) {
        super(qualifiedConnectionName);
    }
}
