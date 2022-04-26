package biz.isphere.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400JDBCDriver;
import com.ibm.xtq.xslt.runtime.RuntimeError;

import biz.isphere.base.internal.SqlHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.search.MatchOption;
import biz.isphere.core.search.SearchArgument;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.sourcefilesearch.FNDSTR_clear;
import biz.isphere.core.sourcefilesearch.FNDSTR_getHandle;
import biz.isphere.core.sourcefilesearch.FNDSTR_search;
import biz.isphere.core.sourcefilesearch.SearchElement;
import biz.isphere.core.sourcefilesearch.SearchResult;
import biz.isphere.core.sourcefilesearch.SearchResultStatement;

/**
 * This class is a JUnit test suite for testing the 'iSphere Source File Search'
 * feature.
 * 
 * @see Source member: QRPGUNIT.FNDSTRRU
 * @author Thomas Raddatz
 */
public class SourceFileSearchTest {

    private static final String ISPHERE_PRODUCT_LIBRARY = "ISPHEREDVP";

    private static final String SOURCE_LIBRARY = "ISPHEREDVP";
    private static final String SOURCE_FILE = "QRPGLESRC";

    private static AS400 as400;
    private static Connection jdbcConnection;

    private String currentLibrary = null;

    /**
     * Initializes the test suite, e.g. gets a JDBC connection to the host.
     */
    @org.junit.BeforeClass
    public static void setupSuite() {

        String hostname = System.getProperty("isphere.junit.as400"); //$NON-NLS-1$
        String user = System.getProperty("isphere.junit.username"); //$NON-NLS-1$
        String password = System.getProperty("isphere.junit.password"); //$NON-NLS-1$

        as400 = new AS400(hostname, user, password);
        jdbcConnection = getJdbcConnection(as400);
    }

    /**
     * Terminates the test suite, e.g. drops the JDBC connection.
     */
    @org.junit.AfterClass
    public static void tearDownSuite() {

        try {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeError(e);
        }
    }

    /**
     * Initializes a test case, e.g. sets the current library to the iSphere
     * product library.
     */
    @org.junit.Before
    public void setup() {

        try {
            currentLibrary = JUnitHelper.getCurrentLibrary(as400);
            JUnitHelper.setCurrentLibrary(as400, ISPHERE_PRODUCT_LIBRARY);
        } catch (Exception e) {
            throw new RuntimeError(e);
        }

    }

    /**
     * Terminates a test case, e.g. restores the current library.
     */
    @org.junit.After
    public void tearDown() {

        try {
            JUnitHelper.setCurrentLibrary(as400, currentLibrary);
        } catch (Exception e) {
            throw new RuntimeError(e);
        }
    }

    /*
     * Test procedures.
     */

    /**
     * Test a simple string search.
     */
    @Test
    public void testSimple() {

        String searchArgument = "demo";
        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesStrings(searchArgument, caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            if ("DEMO1".equals(searchResult.getMember())) {
                assertEquals(11, searchResult.getStatements()[0].getStatement());
            } else if ("DEMO2".equals(searchResult.getMember())) {
                assertEquals(11, searchResult.getStatements()[0].getStatement());
            } else {
                assertTrue(searchResult.getMember().startsWith("DEMO"));
            }

            count += searchResult.getStatements().length;
        }

        assertEquals(34, count);
    }

    /**
     * Test simple case-sensitive string search.
     */
    @Test
    public void testSimpleCaseMatch() {

        String searchArgument = "DEMO5";
        String caseSensitivity = SearchOptions.CASE_MATCH;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesStrings(searchArgument, caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            if ("DEMO05".equals(searchResult.getMember())) {
                assertEquals(42, searchResult.getStatements()[0].getStatement());
            } else if ("DEMO07".equals(searchResult.getMember())) {
                assertEquals(43, searchResult.getStatements()[0].getStatement());
            } else if ("DEMO08".equals(searchResult.getMember())) {
                assertEquals(44, searchResult.getStatements()[0].getStatement());
            } else if ("DEMO09".equals(searchResult.getMember())) {
                assertEquals(48, searchResult.getStatements()[0].getStatement());
            } else {
                assertTrue(searchResult.getMember().startsWith("DEMO"));
            }

            count += searchResult.getStatements().length;
        }

        assertEquals(4, count);
    }

    /**
     * Test simple 'contains not' string search.
     */
    @Test
    public void testSimpleNegative() {

        String searchArgument = "DEMO5";
        String caseSensitivity = SearchOptions.CASE_MATCH;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(excludesStrings(searchArgument, caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        Set<String> expectedMembers = new HashSet<String>();
        expectedMembers.add("DEMO1");
        expectedMembers.add("DEMO2");
        expectedMembers.add("DEMO3");
        expectedMembers.add("DEMO4");
        expectedMembers.add("DEMO6");

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            assertTrue("Unexpected member: " + searchResult.getMember(), expectedMembers.contains(searchResult.getMember()));

            assertTrue("Unexpected sourcemember: DEMO5", !"DEMO5".equals(searchResult.getMember()));
            assertTrue("Unexpected sourcemember: DEMO7", !"DEMO7".equals(searchResult.getMember()));
            assertTrue("Unexpected sourcemember: DEMO8", !"DEMO8".equals(searchResult.getMember()));
            assertTrue("Unexpected sourcemember: DEMO9", !"DEMO9".equals(searchResult.getMember()));

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(5, count);
    }

    /**
     * Test simple regular expression search.
     */
    @Test
    public void testSimpleRegex() {

        String searchArgument = "(BUFF|alc)Len=";
        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesRegex(searchArgument, caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            assertEquals("DEMO7", searchResult.getMember());

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(2, count);
    }

    /**
     * Test simple case-sensitive regular expression search.
     */
    @Test
    public void testSimpleCaseMatchRegex() {

        String searchArgument = "(BUFF|alc)Len=";
        String caseSensitivity = SearchOptions.CASE_MATCH;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesRegex(searchArgument, caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            assertEquals("DEMO7", searchResult.getMember());

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(1, count);
    }

    /**
     * Test 'match any' search string search with two arguments.
     */
    @Test
    public void testCombined2Any() {

        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ANY, true);
        searchOptions.addSearchArgument(includesStrings("qcpysrc,iqdbrtvfd", caseSensitivity));
        searchOptions.addSearchArgument(includesStrings("qcpysrc,iqsdrtvmv", caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        Set<String> expectedMembers = new HashSet<String>();
        expectedMembers.add("DEMO7");
        expectedMembers.add("DEMO8");
        expectedMembers.add("DEMO9");

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            assertTrue("Unexpected member: " + searchResult.getMember(), expectedMembers.contains(searchResult.getMember()));

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(3, count);
    }

    /**
     * Test 'match all' search string search with two arguments.
     */
    @Test
    public void testCombined2All() {

        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesStrings("qcpysrc,iqsdrtvmv", caseSensitivity));
        searchOptions.addSearchArgument(includesStrings("qcpysrc,iqsdrtvvt", caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals(SOURCE_LIBRARY, searchResult.getLibrary());
            assertEquals(SOURCE_FILE, searchResult.getFile());

            assertTrue("Unexpected member: " + searchResult.getMember(), "DEMO9".equals(searchResult.getMember()));

            for (int i = 0; i < searchResult.getStatements().length; i++) {
                assertTrue("Unexpected stmt #" + searchResult.getStatements()[i],
                    searchResult.getStatements()[i].getStatement() == 35 || searchResult.getStatements()[i].getStatement() == 37);
            }

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(2, count);
    }

    /**
     * Test 'match all' search string search with two arguments.
     * <p>
     * First argument must be found.<br>
     * Second argument (regex) must not be found.
     */
    @Test
    public void testCombined2Exclude() {

        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.ALL, true);
        searchOptions.addSearchArgument(includesStrings("DEMO4P", caseSensitivity));
        searchOptions.addSearchArgument(excludesRegex("open|close", caseSensitivity));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {
            count += searchResult.getStatements().length;
        }

        assertEquals(0, count);
    }

    /**
     * Test 'line mode'. Search all lines that conain the first search argument,
     * but that do not contain the second regular expression.
     */
    @Test
    public void testLineMode2Exclude() {

        String caseSensitivity = SearchOptions.CASE_IGNORE;

        SearchOptions searchOptions = new SearchOptions(MatchOption.LINE, true);
        searchOptions.addSearchArgument(includesStrings("DEMO4P", caseSensitivity, 1, 40));
        searchOptions.addSearchArgument(excludesRegex("open|close", caseSensitivity, 1, 40));

        SearchResult[] searchResults = performSearch(searchOptions);

        int count = 0;

        for (SearchResult searchResult : searchResults) {

            assertEquals("DEMO4", searchResult.getMember());

            for (int i = 0; i < searchResult.getStatements().length; i++) {
                assertTrue("Unexpected stmt #" + searchResult.getStatements()[i],
                    searchResult.getStatements()[i].getStatement() == 37 || searchResult.getStatements()[i].getStatement() == 84);
            }

            assertTrue(searchResult.getMember().startsWith("DEMO"));

            count += searchResult.getStatements().length;
        }

        assertEquals(2, count);
    }

    /*
     * Internally used functions.
     */

    private SearchArgument includesStrings(String searchArgument, String caseSensitivity) {
        return includesStrings(searchArgument, caseSensitivity, 1, 228);
    }

    private SearchArgument includesStrings(String searchArgument, String caseSensitivity, int from, int to) {
        return new SearchArgument(searchArgument, from, to, caseSensitivity, SearchOptions.SEARCH_ARG_STRING, SearchOptions.CONTAINS);
    }

    private SearchArgument excludesStrings(String searchArgument, String caseSensitivity) {
        return new SearchArgument(searchArgument, 1, 228, caseSensitivity, SearchOptions.SEARCH_ARG_STRING, SearchOptions.CONTAINS_NOT);
    }

    private SearchArgument includesRegex(String searchArgument, String caseSensitivity) {
        return new SearchArgument(searchArgument, 1, 228, caseSensitivity, SearchOptions.SEARCH_ARG_REGEX, SearchOptions.CONTAINS);
    }

    private SearchArgument excludesRegex(String searchArgument, String caseSensitivity) {
        return excludesRegex(searchArgument, caseSensitivity, 1, 228);
    }

    private SearchArgument excludesRegex(String searchArgument, String caseSensitivity, int from, int to) {
        return new SearchArgument(searchArgument, from, to, caseSensitivity, SearchOptions.SEARCH_ARG_REGEX, SearchOptions.CONTAINS_NOT);
    }

    private SearchResult[] performSearch(SearchOptions searchOptions) {

        int handle = new FNDSTR_getHandle().run(as400);

        ArrayList<SearchElement> searchElements = prepareSearchElement(new ArrayList<SearchElement>());
        SearchElement.setSearchElements(ISPHERE_PRODUCT_LIBRARY, jdbcConnection, handle, searchElements);

        new FNDSTR_search().run(as400, handle, searchOptions);

        SearchResult[] searchResult = getSearchResults(ISPHERE_PRODUCT_LIBRARY, jdbcConnection, handle);

        new FNDSTR_clear().run(as400, handle);

        return searchResult;
    }

    private ArrayList<SearchElement> prepareSearchElement(ArrayList<SearchElement> searchElements) {

        addElement(searchElements, "DEMO1");
        addElement(searchElements, "DEMO2");
        addElement(searchElements, "DEMO3");
        addElement(searchElements, "DEMO4");
        addElement(searchElements, "DEMO5");
        addElement(searchElements, "DEMO6");
        addElement(searchElements, "DEMO7");
        addElement(searchElements, "DEMO8");
        addElement(searchElements, "DEMO9");

        return searchElements;
    }

    private void addElement(ArrayList<SearchElement> searchElements, String memberName) {

        SearchElement element = new SearchElement();
        element.setFile(SOURCE_FILE);
        element.setLibrary(SOURCE_LIBRARY);
        element.setMember(memberName);

        searchElements.add(element);
    }

    private SearchResult[] getSearchResults(String iSphereLibrary, Connection jdbcConnection, int handle) {

        SqlHelper sqlHelper = new SqlHelper(jdbcConnection);

        ArrayList<SearchResult> arrayListSearchResults = new ArrayList<SearchResult>();

        PreparedStatement preparedStatementSelect = null;
        ResultSet resultSet = null;

        try {

            preparedStatementSelect = jdbcConnection.prepareStatement("SELECT * FROM " + sqlHelper.getObjectName(iSphereLibrary, "FNDSTRO")
                + " WHERE XOHDL = ? ORDER BY XOHDL, XOLIB, XOFILE, XOMBR, XOFLCD", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatementSelect.setString(1, Integer.toString(handle));
            resultSet = preparedStatementSelect.executeQuery();

            String _library = ""; //$NON-NLS-1$
            String _file = ""; //$NON-NLS-1$
            String _member = ""; //$NON-NLS-1$

            String library;
            String file;
            String member;
            String srcType;
            Timestamp lastChangedDate;

            SearchResult _searchResult = null;
            ArrayList<SearchResultStatement> alStatements = null;

            while (resultSet.next()) {

                library = resultSet.getString("XOLIB").trim(); //$NON-NLS-1$
                file = resultSet.getString("XOFILE").trim(); //$NON-NLS-1$
                member = resultSet.getString("XOMBR").trim(); //$NON-NLS-1$
                srcType = resultSet.getString("XOTYPE").trim(); //$NON-NLS-1$
                lastChangedDate = resultSet.getTimestamp("XOFLCD"); //$NON-NLS-1$

                if (!_library.equals(library) || !_file.equals(file) || !_member.equals(member)) {

                    if (_searchResult != null) {

                        SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
                        alStatements.toArray(_statements);

                        _searchResult.setStatements(_statements);

                        arrayListSearchResults.add(_searchResult);

                    }

                    _library = library;
                    _file = file;
                    _member = member;

                    _searchResult = new SearchResult();
                    _searchResult.setLibrary(library);
                    _searchResult.setFile(file);
                    _searchResult.setMember(member);
                    _searchResult.setSrcType(srcType);
                    _searchResult.setLastChangedDate(lastChangedDate);

                    alStatements = new ArrayList<SearchResultStatement>();

                }

                SearchResultStatement statement = new SearchResultStatement();
                statement.setStatement(resultSet.getInt("XOSTMT")); //$NON-NLS-1$
                statement.setLine(StringHelper.trimR(resultSet.getString("XOLINE"))); //$NON-NLS-1$
                alStatements.add(statement);

            }

            if (_searchResult != null) {

                SearchResultStatement[] _statements = new SearchResultStatement[alStatements.size()];
                alStatements.toArray(_statements);

                _searchResult.setStatements(_statements);

                arrayListSearchResults.add(_searchResult);

            }

        } catch (SQLException e) {
            System.out.println("*** Could not load source file search result ***");
            e.printStackTrace();
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("*** Could not close source file search result set ***");
                e.printStackTrace();
            }
        }

        if (preparedStatementSelect != null) {
            try {
                preparedStatementSelect.close();
            } catch (SQLException e) {
                System.out.println("*** Could not close prepared statement of source file search ***");
                e.printStackTrace();
            }
        }

        SearchResult[] _searchResults = new SearchResult[arrayListSearchResults.size()];
        arrayListSearchResults.toArray(_searchResults);
        return _searchResults;

    }

    private static Connection getJdbcConnection(AS400 system) {

        Connection jdbcConnection = null;
        AS400JDBCDriver as400JDBCDriver = null;

        try {

            try {

                as400JDBCDriver = (AS400JDBCDriver)DriverManager.getDriver("jdbc:as400");

            } catch (SQLException e) {

                as400JDBCDriver = new AS400JDBCDriver();
                DriverManager.registerDriver(as400JDBCDriver);

            }

            Properties properties = new Properties();
            properties.put("prompt", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.put("big decimal", "false"); //$NON-NLS-1$ //$NON-NLS-2$

            jdbcConnection = as400JDBCDriver.connect(system, properties, null);

        } catch (Throwable e) {
            System.out.println("*** Could not produce JDBC connection ***");
            e.printStackTrace();
        }

        return jdbcConnection;
    }

}
