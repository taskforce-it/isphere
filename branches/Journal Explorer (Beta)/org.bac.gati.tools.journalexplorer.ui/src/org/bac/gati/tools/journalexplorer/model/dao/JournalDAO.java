package org.bac.gati.tools.journalexplorer.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.bac.gati.tools.journalexplorer.model.Journal;
import org.bac.gati.tools.journalexplorer.model.access.DAOBase;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

/**
 * This class loads the exported journal *TYPE3 data that has been exported by
 * DSPJRN to an output file. For example:
 * 
 * <pre>
 * DSPJRN JRN(library/journal) FILE((library/file)) RCVRNG(*CURCHAIN) 
 *   FROMTIME(060417 140000) TOTIME(060417 160000) ENTTYP(*RCD)    
 *   OUTPUT(*OUTFILE) OUTFILFMT(*TYPE3) OUTFILE(library/file)    
 *   ENTDTALEN(1024)
 * </pre>
 */
public class JournalDAO extends DAOBase {

    private String library;

    private String file;

    public JournalDAO(IBMiConnection connection, String library, String file) throws Exception {
        super(connection);
        this.library = library;
        this.file = file;
    }

    private static final String GET_JOURNAL_DATA = "    SELECT rrn(result) as ID, " + "           result.JOENTL," + "           result.JOSEQN,"
        + "           result.JOCODE," + "           result.JOENTT," + "           result.JOTSTP," + "           result.JOJOB,"
        + "           result.JOUSER," + "           result.JONBR," + "           result.JOPGM," + "           result.JOOBJ,"
        + "           result.JOLIB," + "           result.JOMBR," + "           result.JOCTRR," + "           result.JOFLAG,"
        + "           result.JOCCID," + "           result.JOINCDAT," + "           result.JOMINESD,"
        + "           SUBSTR(result.JOESD,1,5000) AS JOESD" // TODO
        + "      FROM %s.%s as result";

    public ArrayList<Journal> getJournalData() throws Exception {

        Journal journal = null;
        String statementSQL = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Journal> journalData = new ArrayList<Journal>();

        try {

            statementSQL = String.format(GET_JOURNAL_DATA, this.library, this.file);

            this.connection.setAutoCommit(false);
            preparedStatement = this.connection.prepareStatement(statementSQL);
            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(50);

            if (resultSet != null) {

                // TODO
                Date inicio = Calendar.getInstance().getTime();

                while (resultSet.next()) {

                    journal = new Journal();

                    journal.connection = this.ibmiConnection;
                    journal.setCommitmentCycle(resultSet.getInt("JOCCID"));
                    // Depending of the journal out type, the timestamp can be a
                    // single field or splitted in JODATE and JOTYPE
                    // TODO
                    if (hasColumn(resultSet, "JOTSTP")) {
                        journal.setDate(resultSet.getDate("JOTSTP"));
                        journal.setTime(resultSet.getTime("JOTSTP"));
                    } else {
                        // journal.setDate(resultSet.getString("JODATE"));
                        // journal.setTime(resultSet.getInt("JOTIME"));
                    }
                    journal.setEntryLength(resultSet.getInt("JOENTL"));
                    journal.setEntryType(resultSet.getString("JOENTT"));
                    journal.setIncompleteData(resultSet.getString("JOINCDAT"));
                    journal.setJobName(resultSet.getString("JOJOB"));
                    journal.setJobNumber(resultSet.getInt("JONBR"));
                    journal.setJobUserName(resultSet.getString("JOUSER"));
                    journal.setJoCtrr(resultSet.getInt("JOCTRR"));
                    journal.setJoFlag(resultSet.getString("JOFLAG"));
                    journal.setJournalCode(resultSet.getString("JOCODE"));
                    // setJournalID
                    journal.setMemberName(resultSet.getString("JOMBR"));
                    journal.setMinimizedSpecificData(resultSet.getString("JOMINESD"));
                    journal.setObjectLibrary(resultSet.getString("JOLIB"));
                    journal.setObjectName(resultSet.getString("JOOBJ"));
                    // setOutFileLibrary
                    // setOutFileName
                    journal.setProgramName(resultSet.getString("JOPGM"));
                    // setReferentialConstraint
                    journal.setRrn(resultSet.getInt("ID"));
                    journal.setSequenceNumber(resultSet.getLong("JOSEQN"));
                    journal.setSpecificData(resultSet.getBytes("JOESD"));
                    journal.setStringSpecificData(resultSet.getString("JOESD"));
                    // setSystemName
                    // setTime
                    // setTrigger
                    // setUserProfile

                    // Depending of the journal out type, the timestamp can be a
                    // single field or splitted in JODATE and JOTYPE
                    // TODO
                    // if (this.hasColumn(resultSet, "JOTSTP")) {
                    // }
                    // else {
                    // journal.setDate(resultSet.getString("JODATE"),
                    // resultSet.getString("JOTIME"));
                    // }

                    journal.setOutFileLibrary(library);
                    journal.setOutFileName(file);
                    journalData.add(journal);

                }
                // TODO
                Date Fin = Calendar.getInstance().getTime();
                System.out.println(Integer.toString(journalData.size()) + "':" + Long.toString(Fin.getTime() - inicio.getTime()));
            }
        } catch (Exception exception) {
            throw exception;
        } finally {
            super.destroy(preparedStatement);
            super.destroy(resultSet);
        }
        return journalData;
    }

    private boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {

        ResultSetMetaData metaData = resultSet.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            if (metaData.getColumnName(i).equals(columnName)) {
                return true;
            }
        }

        return false;
    }

    /*
     * private boolean hasColumn(ResultSet resultSet, String columnName) { int
     * columnIndex; try { columnIndex = resultSet.findColumn(columnName); return
     * columnIndex >= 0; } catch (Exception exception) { return false; } }
     */
}
