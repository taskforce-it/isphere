package org.bac.gati.tools.journalexplorer.rse.shared.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bac.gati.tools.journalexplorer.rse.Messages;

import com.ibm.as400.access.AS400Date;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class DAOBase {
    protected static final String properties = "thread used=false;extendeddynamic=true;package criteria=select;package cache=true;"; //$NON-NLS-1$

    protected IBMiConnection ibmiConnection;
    private Connection connection;
    private String dateFormat;
    private String dateSeparator;

    public DAOBase(String connectionName) throws Exception {
        if (connectionName != null) {
            this.ibmiConnection = IBMiConnection.getConnection(connectionName);
            if (!ibmiConnection.isConnected()) {
                if (!ibmiConnection.connect()) {
                    throw new Exception(Messages.DAOBase_ConnectionNotStablished);
                }
            }

            this.dateFormat = this.ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateFormat();
            if (this.dateFormat.startsWith("*")) {
                this.dateFormat = this.dateFormat.substring(1);
            }
            this.dateSeparator = this.ibmiConnection.getQSYSJobSubSystem().getServerJob(null).getInternationalProperties().getDateSeparator();
            this.connection = ibmiConnection.getJDBCConnection("", true); //$NON-NLS-1$
            this.connection.setAutoCommit(false);
        } else
            throw new Exception(Messages.DAOBase_InvalidConnectionObject);
    }

    public void destroy() {
    }

    protected String getConnectionName() {
        return ibmiConnection.getConnectionName();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    protected int getDateFormat() {
        return AS400Date.toFormat(this.dateFormat);
    }

    protected void destroy(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) connection.close();
    }

    protected void destroy(ResultSet resultSet) throws Exception {
        if (resultSet != null) resultSet.close();
    }

    protected void destroy(PreparedStatement preparedStatement) throws Exception {
        if (preparedStatement != null) preparedStatement.close();
    }

    protected void rollback(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) {
            if (connection.getAutoCommit() == false) connection.rollback();
        }
    }

    protected void commit(Connection connection) throws Exception {
        if (connection != null && !connection.isClosed()) {
            if (connection.getAutoCommit() == false) connection.commit();
        }
    }
}
