/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ibmi.contributions.extension.point;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

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
public class BasicQualifiedConnectionName implements Comparable<BasicQualifiedConnectionName> {

    private static final String CONNECTION_NAME_DELIMITER = ":"; //$NON-NLS-1$
    private static final String CONNECTION_NAME_FORMAT = "%s:%s"; //$NON-NLS-1$

    private static final String NO_PROFILE_NAME = "*N"; //$NON-NLS-1$

    private String connectionName;
    private String profileName;

    /**
     * Constructs a qualified connection name object from given profile and
     * connection names.
     * 
     * @param profileName - A remote system profile name.
     * @param connectionName - A remote system connection name.
     */
    public BasicQualifiedConnectionName(String profileName, String connectionName) {
        this.profileName = profileName;
        this.connectionName = connectionName;
    }

    /**
     * Constructs a qualified connection name object from a qualified connection
     * name string, which must be formatted as
     * <code>'profileName:connectionName'</code>. The profile name is optional.<br>
     * When the profile name is missing, the constructor falls back to the
     * default system profile name.
     * 
     * @param qualifiedConnectionName - A qualified connection name of format
     *        <code>'profileName:connectionName'</code>.
     */
    public BasicQualifiedConnectionName(String qualifiedConnectionName) {

        int x = qualifiedConnectionName.lastIndexOf(CONNECTION_NAME_DELIMITER);
        if (x > 0) {
            profileName = qualifiedConnectionName.substring(0, x);
            connectionName = qualifiedConnectionName.substring(x + 1);
        } else if (x == 0) {
            profileName = NO_PROFILE_NAME;
            connectionName = qualifiedConnectionName.substring(1);
        } else {
            profileName = NO_PROFILE_NAME;
            connectionName = qualifiedConnectionName;
        }

        if (StringHelper.isNullOrEmpty(connectionName)) {
            throw new IllegalArgumentException("Illegal qualified connection name: " + qualifiedConnectionName);
        }
    }

    /**
     * Returns the connection without the profile name.
     * 
     * @return connection name.
     */
    public String getConnectionName() {
        return connectionName;
    }

    /**
     * Returns the profile name.
     * 
     * @return profile name.
     */
    public String getProfileName() {
        if (profileName == NO_PROFILE_NAME) {
            return null;
        }
        return profileName;
    }

    /**
     * Returns the qualified connection name in the form of
     * "[profile]:connection"
     * 
     * @return qualified connection name.
     */
    public String getQualifiedName() {
        if (profileName == NO_PROFILE_NAME) {
            return connectionName;
        }
        return String.format(CONNECTION_NAME_FORMAT, profileName, connectionName);
    }

    /**
     * Returns the qualified connection formatted for UI presentation.
     * 
     * @return qualified UI connection name.
     */
    public String getUIConnectionName() {

        String uiConnectionName;

        if (IBMiHostContributionsHandler.isShowQualifyConnectionNames() && !StringHelper.isNullOrEmpty(getProfileName())
            && !BasicQualifiedConnectionName.NO_PROFILE_NAME.equalsIgnoreCase(getProfileName())) {
            uiConnectionName = getProfileName() + "." + getConnectionName();
        } else {
            uiConnectionName = getConnectionName();
        }

        return uiConnectionName;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    public int compareTo(BasicQualifiedConnectionName other) {

        if (other == null) {
            return 1;
        } else {
            int result = compareProfileNames(other.getProfileName());
            if (result == 0) {
                return compareConnectionNames(other.getConnectionName());
            }
        }

        return 0;
    }

    private int compareProfileNames(String other) {
        return compare(profileName, other);
    }

    private int compareConnectionNames(String other) {
        return compare(connectionName, other);
    }

    private int compare(String me, String other) {

        if (me == null && other == null) {
            return 0;
        } else if (other == null) {
            return 1;
        }

        return me.compareTo(other);
    }
}
