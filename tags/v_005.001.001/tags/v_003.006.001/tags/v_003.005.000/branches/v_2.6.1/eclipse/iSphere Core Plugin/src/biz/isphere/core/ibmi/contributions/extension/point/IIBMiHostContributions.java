/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.ibmi.contributions.extension.point;

import java.sql.Connection;

import com.ibm.as400.access.AS400;

public interface IIBMiHostContributions {

    /**
     * Returns an AS400 object for a given connection name.
     * 
     * @param connectionName - connection name to identify the connection
     * @return AS400 object that is associated to the connection
     */
    public AS400 getSystem(String connectionName);

    /**
     * Returns an AS400 object for a given profile and connection name.
     * 
     * @param profile - name of the profile, that hosts the connection
     * @param connectionName - connection name to identify the connection
     * @return AS400 object that is associated to the connection
     */
    public AS400 getSystem(String profile, String connectionName);

    /**
     * Returns a JDBC connection for a given connection name.
     * 
     * @param connectionName - connection name to identify the connection
     * @return JDBC connection that is associated to the connection
     */
    public Connection getJdbcConnection(String connectionName);

    /**
     * Returns a JDBC connection for a given profile and connection name.
     * 
     * @param profile - name of the profile, that hosts the connection
     * @param connectionName - connection name to identify the connection
     * @return JDBC connection that is associated to the connection
     */
    public Connection getJdbcConnection(String profile, String connectionName);

}
