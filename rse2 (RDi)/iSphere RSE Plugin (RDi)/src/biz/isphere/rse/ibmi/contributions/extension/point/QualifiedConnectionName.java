/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.ibmi.contributions.extension.point;

import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.internal.core.model.SystemProfileManager;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class QualifiedConnectionName implements Comparable<QualifiedConnectionName> {

    private static final String CONNECTION_NAME_DELIMITER = ":"; //$NON-NLS-1$
    private static final String CONNECTION_NAME_FORMAT = "%s:%s"; //$NON-NLS-1$

    private String defaultSystemProfileName;

    private String connectionName;
    private String profileName;

    public QualifiedConnectionName(IHost host) {
        this.profileName = host.getSystemProfileName();
        this.connectionName = host.getAliasName();
    }

    public QualifiedConnectionName(IBMiConnection connection) {
        this.profileName = connection.getProfileName();
        this.connectionName = connection.getConnectionName();
    }

    public QualifiedConnectionName(String profileName, String connectionName) {
        this.profileName = profileName;
        this.connectionName = connectionName;
    }

    public QualifiedConnectionName(String qualifiedConnectionName) {

        String[] parts = qualifiedConnectionName.split(CONNECTION_NAME_DELIMITER);
        switch (parts.length) {
        case 1:
            profileName = getDefaultSystemProfileName();
            connectionName = parts[0];
            break;

        case 2:
            profileName = parts[0];
            connectionName = parts[1];
            break;

        default:
            throw new IllegalArgumentException("Illegal qualified connection name: " + qualifiedConnectionName);
        }
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getQualifiedName() {

        if (profileName.matches(getDefaultSystemProfileName())) {
            return connectionName;
        }

        return String.format(CONNECTION_NAME_FORMAT, profileName, connectionName);
    }

    private String getDefaultSystemProfileName() {

        if (defaultSystemProfileName == null) {
            ISystemProfile defaultSystemProfile = SystemProfileManager.getDefault().getDefaultPrivateSystemProfile();
            defaultSystemProfileName = defaultSystemProfile.getName();
        }

        return defaultSystemProfileName;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    public int compareTo(QualifiedConnectionName other) {

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

        if (profileName.equals(getDefaultSystemProfileName())) {
            return -1;
        }

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
