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

    public AS400 getSystem(String connectionName);

    public AS400 getSystem(String profile, String connectionName);

    public Connection getJdbcConnection(String connectionName);

    public Connection getJdbcConnection(String profile, String connectionName);

}
