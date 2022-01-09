package org.bac.gati.tools.journalexplorer.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bac.gati.tools.journalexplorer.internals.Messages;
import org.bac.gati.tools.journalexplorer.model.MetaColumn;
import org.bac.gati.tools.journalexplorer.model.MetaTable;
import org.bac.gati.tools.journalexplorer.model.access.DAOBase;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class MetaTableDAO extends DAOBase {

	public MetaTableDAO(IBMiConnection connection) throws Exception {
		super(connection);
	}

	private static final String GET_TABLE_DEFINITION_SQL = 
			  "    SELECT Tables.SYSTEM_TABLE_NAME, " //$NON-NLS-1$
			+ "           Tables.SYSTEM_TABLE_SCHEMA, " //$NON-NLS-1$
			+ "           Tables.COLUMN_COUNT, " //$NON-NLS-1$
			+ "           Tables.TABLE_TEXT, " //$NON-NLS-1$
			+ "           Tables.LONG_COMMENT," //$NON-NLS-1$
			+ "           Columns.COLUMN_NAME,  " //$NON-NLS-1$
			+ "           Columns.COLUMN_DEFAULT,  " //$NON-NLS-1$
			+ "           Columns.DATA_TYPE," //$NON-NLS-1$
			+ "           Columns.IS_NULLABLE," //$NON-NLS-1$
			+ "           Columns.LONG_COMMENT as COLUMN_LONG_COMMENT," //$NON-NLS-1$
			+ "           Columns.COLUMN_TEXT," //$NON-NLS-1$
			+ "           Columns.SYSTEM_COLUMN_NAME,  " //$NON-NLS-1$
			+ "           Columns.IS_IDENTITY," //$NON-NLS-1$
			+ "           Columns.LENGTH," //$NON-NLS-1$
			+ "           Columns.NUMERIC_SCALE," //$NON-NLS-1$
			+ "           Columns.ORDINAL_POSITION " //$NON-NLS-1$
			+ "      FROM QSYS2.SYSTABLES  Tables " //$NON-NLS-1$
			+ "INNER JOIN QSYS2.SYSCOLUMNS Columns " //$NON-NLS-1$
			+ "        ON Tables.SYSTEM_TABLE_NAME = Columns.SYSTEM_TABLE_NAME " //$NON-NLS-1$
			+ "       AND Tables.SYSTEM_TABLE_SCHEMA = Columns.TABLE_SCHEMA " //$NON-NLS-1$
			+ "     WHERE Tables.SYSTEM_TABLE_NAME = ? " //$NON-NLS-1$
			+ "       AND Tables.SYSTEM_TABLE_SCHEMA = ?" //$NON-NLS-1$
			+ " ORDER BY  Columns.ORDINAL_POSITION"; //$NON-NLS-1$
	public void retrieveColumnsMetaData(MetaTable table) throws Exception {
		
		MetaColumn column = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		boolean nextColumn = true;

		try {
			
			sqlStatement = this.connection.prepareStatement(GET_TABLE_DEFINITION_SQL);
			sqlStatement.setString(1, table.getDefinitionName());
			sqlStatement.setString(2, table.getDefinitionLibrary());
			
			resultSet = sqlStatement.executeQuery();

			if (resultSet == null) {
				
				throw new Exception(Messages.MetaTableDAO_NullResultSet);
			}
			else {
				
				if (resultSet.next()) {

					while (nextColumn) {
						column = new MetaColumn();
						column.setName(resultSet.getString("SYSTEM_COLUMN_NAME")); //$NON-NLS-1$
						column.setColumnText(resultSet.getString("COLUMN_TEXT")); //$NON-NLS-1$
						column.setDataType(resultSet.getString("DATA_TYPE")); //$NON-NLS-1$
						column.setSize(resultSet.getInt("LENGTH")); //$NON-NLS-1$
						column.setPrecision(resultSet.getInt("NUMERIC_SCALE")); //$NON-NLS-1$
						
						table.getColumns().add(column);
						
						nextColumn = resultSet.next();
					}
					
				} else {
					throw new Exception(Messages.MetaTableDAO_TableDefinitionNotFound);
				}
			}
			
		} catch (Exception exception) {
			
			throw exception;
			
		} finally {
			
			super.destroy(resultSet);
			super.destroy(sqlStatement);
		}
	}
}
