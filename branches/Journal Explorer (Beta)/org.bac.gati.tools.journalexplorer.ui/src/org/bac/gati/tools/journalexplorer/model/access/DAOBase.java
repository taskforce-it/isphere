
package org.bac.gati.tools.journalexplorer.model.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bac.gati.tools.journalexplorer.internals.Messages;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class DAOBase
{
    protected static final String properties = "thread used=false;extendeddynamic=true;package criteria=select;package cache=true;"; //$NON-NLS-1$
    
    protected IBMiConnection ibmiConnection;
    
    protected Connection connection;

    public DAOBase(IBMiConnection connection) throws Exception
    {
    	if (connection != null) {
			if (!connection.isConnected()) {
				if (!connection.connect()) {
					throw new Exception(Messages.DAOBase_ConnectionNotStablished);
				}
			}
    		
    		this.ibmiConnection = connection;
    		this.connection = connection.getJDBCConnection("", false); //$NON-NLS-1$
    	}
    	else
    		throw new Exception(Messages.DAOBase_InvalidConnectionObject);
    }
    
    public void destroy()
    {
    }

    protected void destroy(Connection connection) throws Exception
    {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
    
    protected void destroy(ResultSet resultSet) throws Exception
    {
        if (resultSet != null)
            resultSet.close();
    }

    protected void destroy(PreparedStatement preparedStatement) throws Exception
    {
        if (preparedStatement != null)
            preparedStatement.close();
    }

    protected void rollback(Connection connection) throws Exception
    {
        if (connection != null && !connection.isClosed())
        {
            if (connection.getAutoCommit() == false)
                connection.rollback();
        }        
    }
    
    protected void commit(Connection connection) throws Exception
    {
        if (connection != null && !connection.isClosed())
        {
            if (connection.getAutoCommit() == false)
                connection.commit();
        }
    }
}


