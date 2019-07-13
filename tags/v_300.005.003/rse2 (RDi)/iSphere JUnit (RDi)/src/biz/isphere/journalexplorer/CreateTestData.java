package biz.isphere.journalexplorer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.as400.access.AS400JDBCXADataSource;

public class CreateTestData {

    private String host = System.getProperty("isphere.junit.host");
    private String user = System.getProperty("isphere.junit.user");
    private String password = System.getProperty("isphere.junit.password");

    private static final String LIBRARY = "LIB";
    private static final String JOURNAL = "JRN";
    private static final String JOURNAL_RECEIVER = "JRNRCV";
    private static final String PARENT = "PARENT";
    private static final String CHILD = "CHILD";

    private Map<String, String> replacementVariables = null;

    public CreateTestData() {

        replacementVariables = new HashMap<String, String>();
        replacementVariables.put(LIBRARY, "RADDATZ");
        replacementVariables.put(JOURNAL, "XJRN");
        replacementVariables.put(JOURNAL_RECEIVER, "XJRN");
        replacementVariables.put(PARENT, "XPARENT");
        replacementVariables.put(CHILD, "XCHILD");
    }

    public static void main(String[] args) {

        CreateTestData main = new CreateTestData();
        main.run();

    }

    private void run() {

        // Je nach Datenbank andere XADataSource einsetzen
        // (hier fuer DB2/400 auf AS/400 V5R4):
        AS400JDBCXADataSource xaDataSource = new AS400JDBCXADataSource(host);
        xaDataSource.setUser(user);
        xaDataSource.setPassword(password);
        xaDataSource.setLibraries(get(LIBRARY));
        XAConnection xaConnection = null;
        XAResource xaResource = null;
        Xid xid = null;

        try {

            xaConnection = xaDataSource.getXAConnection();
            xaResource = xaConnection.getXAResource();
            Connection connection = xaConnection.getConnection();
            xid = new XidImpl(100, new byte[] { 0x0A }, new byte[] { 0x03 });

            xaResource.start(xid, XAResource.TMNOFLAGS);

            System.out.println(connection.getMetaData().getDatabaseProductName());
            System.out.println(connection.getMetaData().getDatabaseProductVersion());

            if (!executeClCommand(connection, "CHKOBJ OBJ(${LIB}/${JRNRCV}) OBJTYPE(*JRNRCV)")) {
                executeClCommand(connection, "CRTJRNRCV JRNRCV(${LIB}/${JRNRCV})");
            }

            if (!executeClCommand(connection, "CHKOBJ OBJ(${LIB}/${JRN}) OBJTYPE(*JRN)")) {
                executeClCommand(connection, "CRTJRN JRN(${LIB}/${JRN}) JRNRCV(${LIB}/${JRNRCV}) MNGRCV(*SYSTEM) DLTRCV(*NO) RCVSIZOPT(*MAXOPT2) "
                    + "FIXLENDTA(*JOB *USR *PGM *PGMLIB *SYSSEQ *RMTADR *THD *LUW *XID)");
            }

            if (!executeClCommand(connection, "CHKOBJ OBJ(${LIB}/${PARENT}) OBJTYPE(*FILE)")) {
                createParentFile(connection);
            }

            if (!executeClCommand(connection, "CHKOBJ OBJ(${LIB}/${CHILD}) OBJTYPE(*FILE)")) {
                createChildFile(connection);
            }

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            addSampleParentData(connection);
            addSampleChildData(connection);

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Little Bulp", "*JOB *USR *PGM *PGMLIB *SYSSEQ *RMTADR *THD *LUW");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Gyro Gearloose", "*JOB *USR *PGM *PGMLIB *SYSSEQ *RMTADR *THD");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Eider Duck", "*JOB *USR *PGM *PGMLIB *SYSSEQ *RMTADR");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Fethry Duck", "*JOB *USR *PGM *PGMLIB *SYSSEQ");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Della Duck", "*JOB *USR *PGM *PGMLIB");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Louie Duck", "*JOB *USR *PGM");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Dewey Duck", "*JOB *USR");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Huey Duck", "*JOB");

            commit(xaResource, xid);
            xaResource.start(xid, XAResource.TMNOFLAGS);

            changeJournal(connection, "Donald Duck", "*USR");

            commit(xaResource, xid);

            exportJournalEntries(connection, get(PARENT));
            exportJournalEntries(connection, get(CHILD));

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (xaResource != null) {
                    xaResource.end(xid, XAResource.TMSUCCESS);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            try {
                if (xaResource != null) {
                    xaResource.rollback(xid);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {

            try {
                if (xaConnection != null) {
                    xaConnection.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private void commit(XAResource xaResource, Xid xid) throws XAException {
        xaResource.end(xid, XAResource.TMSUCCESS);

        int xaState = xaResource.prepare(xid);
        if (xaState == XAResource.XA_OK) {
            xaResource.commit(xid, false);
        } else if (xaState == XAResource.XA_RDONLY) {
            System.out.println("XA resource is read only.");
        } else {
            System.out.println("Unknown XA state: " + xaState);
        }
    }

    private void createParentFile(Connection connection) throws SQLException {

        String command = 
// @formatter:off
        "CREATE OR REPLACE TABLE ${PARENT} (" +
        "  P_KEY CHARACTER (30) NOT NULL WITH DEFAULT, " +
        "  P_COUNT INTEGER NOT NULL WITH DEFAULT, " +
        "  P_DATA VARCHAR (80) NOT NULL WITH DEFAULT)";
//@formatter:on

        executeSqlStatement(connection, command);

        command = 
// @formatter:off
        "ALTER TABLE ${PARENT} " +
        "  ADD CONSTRAINT ${PARENT}_PRIMARY_KEY " +
        "  PRIMARY KEY (P_KEY)";
//@formatter:on

        executeSqlStatement(connection, command);

        startJournaling(connection, get(PARENT));
    }

    private void createChildFile(Connection connection) throws SQLException {

        String command = 
// @formatter:off
        "CREATE OR REPLACE TABLE ${CHILD} (" +
        "  C_KEY CHARACTER (30) NOT NULL WITH DEFAULT, " +
        "  C_POS INTEGER NOT NULL WITH DEFAULT, " +
        "  C_DATA VARCHAR (80) NOT NULL WITH DEFAULT)";
// @formatter:on

        executeSqlStatement(connection, command);

        command = 
// @formatter:off
        "ALTER TABLE ${CHILD} " +
        "  ADD CONSTRAINT ${CHILD}_PRIMARY_KEY " +
        "  PRIMARY KEY (C_KEY, C_POS)";
//@formatter:on

        executeSqlStatement(connection, command);

        startJournaling(connection, get(CHILD));

        command = 
// @formatter:off
        "ALTER TABLE ${CHILD} " +
        "  ADD CONSTRAINT ${CHILD}_DELETE_CASACADE " +
        "  FOREIGN KEY (C_KEY) " +
        "  REFERENCES ${PARENT} (P_KEY) " +
        "  ON DELETE CASCADE " +
        "  ON UPDATE RESTRICT";
//@formatter:on

        executeSqlStatement(connection, command);

        command = 
// @formatter:off
        "CREATE OR REPLACE TRIGGER ${CHILD}_AFTER_INSERT AFTER INSERT " +
        "  ON ${CHILD} " +
        "  REFERENCING NEW AS N " +
        "  FOR EACH ROW " +
        "    UPDATE ${PARENT} " +
        "      SET P_COUNT = P_COUNT + 1 " +
        "    WHERE ${PARENT}.P_KEY = N.C_KEY";
//@formatter:on

        executeSqlStatement(connection, command);
    }

    private void addSampleParentData(Connection connection) throws SQLException {

        String command = 
// @formatter:off
        "INSERT INTO ${PARENT} (P_KEY, P_COUNT) " +
        "  VALUES " +
        "    ('Donald Duck', 0), " +
        "    ('Huey Duck', 0), " +
        "    ('Dewey Duck', 0), " +
        "    ('Louie Duck', 0), " +
        "    ('Della Duck', 0), " +
        "    ('Fethry Duck', 0), " +
        "    ('Eider Duck', 0), " +
        "    ('Gyro Gearloose', 0), " +
        "    ('Little Bulp', 0)";
//@formatter:on

        executeSqlStatement(connection, command);
    }

    private void addSampleChildData(Connection connection) throws SQLException {

        String command = 
// @formatter:off
        "INSERT INTO ${CHILD} (C_KEY, C_POS, C_DATA) " +
        "  VALUES " +
        "    ('Donald Duck', 1, 'Eider''s Nephew'), " +
        "    ('Huey Duck', 1, 'Donald''s nephew'), " +
        "    ('Dewey Duck', 1, 'Donald''s nephew'), " +
        "    ('Louie Duck', 1, 'Donald''s nephew'), " +
        "    ('Della Duck', 1, 'Donald''s twin sister'), " +
        "    ('Fethry Duck', 1, 'Donald''s cusin'), " +
        "    ('Eider Duck', 1, 'Donald''s Uncle'), " +
        "    ('Gyro Gearloose', 1, 'Genious'), " +
        "    ('Little Bulp', 1, 'Helper')";
//@formatter:on

        executeSqlStatement(connection, command);
    }

    private void startJournaling(Connection connection, String file) throws SQLException {

        String command = 
// @formatter:off
        "STRJRNPF " +
        "  FILE(${LIB}/" + file + ") " +
        "  JRN(${LIB}/${JRN}) " +
        "  IMAGES(*BOTH) " +
        "  OMTJRNE(*OPNCLO)";
// @formatter:on

        executeClCommand(connection, command);
    }

    private void changeJournal(Connection connection, String key, String fixLenDta) throws SQLException {

        changeJournal(connection, fixLenDta);
        updateParent(connection, key, fixLenDta);
        deleteParent(connection, key);
    }

    private String changeJournal(Connection connection, String fixLenDta) {

        String command = 
// @formatter:off
        "CHGJRN " +
        "  JRN(${LIB}/${JRN}) " +
        "  JRNRCV(*GEN) " +
        "  FIXLENDTA(" + fixLenDta + ")";
// @formatter:on

        executeClCommand(connection, command);

        return fixLenDta;
    }

    private void updateParent(Connection connection, String key, String fixLenDta) throws SQLException {

        String command = 
// @formatter:off
        "UPDATE ${PARENT} " +
        "  SET " +
        "    P_DATA = '" + fixLenDta + "' " +
        "  WHERE " +
        "    P_KEY = '" + key + "'";
//@formatter:on

        executeSqlStatement(connection, command);
    }

    private void deleteParent(Connection connection, String key) throws SQLException {

        String command = 
// @formatter:off
        "DELETE FROM ${PARENT} " +
        "  WHERE " +
        "    P_KEY = '" + key + "'";
//@formatter:on

        executeSqlStatement(connection, command);
    }

    private void exportJournalEntries(Connection connection, String file) {

        file = file + "5";

        String command = 
// @formatter:off
        "DSPJRN JRN(${LIB}/${JRN}) " +
        "  FILE((${LIB}/XPARENT)) " +
        "  RCVRNG(*CURCHAIN) ENTTYP(*RCD) " +
        "  OUTPUT(*OUTFILE) OUTFILFMT(*TYPE5) " +
        "  OUTFILE(${LIB}/" + file + ")";
// @formatter:on

        executeClCommand(connection, command);
    }

    private boolean executeClCommand(Connection connection, String command) {

        try {
            System.out.print(replaceVariables(command));
            executeSqlStatement(connection, "CALL QSYS2.QCMDEXC('" + command + "')");
        } catch (SQLException e) {
            System.out.println(" ==> ERROR: " + e.getMessage());
            return false;
        }

        System.out.println(" ==> OK");

        return true;
    }

    private void executeSqlStatement(Connection connection, String command) throws SQLException {
        command = replaceVariables(command);
        connection.createStatement().execute(command);
    }

    private String replaceVariables(String text) {

        Set<String> keys = replacementVariables.keySet();
        String value;
        for (String key : keys) {
            value = replacementVariables.get(key);
            key = "\\$\\{" + key + "\\}";
            text = text.replaceAll(key, value);
        }

        return text;
    }

    private String get(String key) {
        return replacementVariables.get(key);
    }

    // Entweder folgende XidImpl oder alternativ auch XidImpl vom
    // Application Server, z.B.:
    // weblogic.transaction.internal.XidImpl
    // org.jboss.tm.XidImpl
    class XidImpl implements Xid {
        protected int formatId;
        protected byte[] gtrid;
        protected byte[] bqual;

        public XidImpl(int formatId, byte gtrid[], byte bqual[]) {
            this.formatId = formatId;
            this.gtrid = gtrid;
            this.bqual = bqual;
        }

        public int getFormatId() {
            return formatId;
        }

        public byte[] getGlobalTransactionId() {
            return gtrid;
        }

        public byte[] getBranchQualifier() {
            return bqual;
        }
    }

}
