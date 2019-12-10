/*******************************************************************************
 * Copyright (c) 2012-2019 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.joblogexplorer.ISphereJobLogExplorerPlugin;
import biz.isphere.joblogexplorer.Messages;

public class JobLogParserConfiguration {

    private static final String FIELD_DATE = "${date}"; //$NON-NLS-1$
    private static final String FIELD_VERSION = "${version}"; //$NON-NLS-1$

    private static final String CONFIGURATION_DIRECTORY = "jobLogParser";//$NON-NLS-1$
    private static final String DEFAULT_CONFIGURATION_FILE = "jobLogParser.properties";//$NON-NLS-1$
    private static final String EXAMPLE_CONFIGURATION_FILE = "example_jobLogParser.properties";//$NON-NLS-1$

    private static final String REPOSITORY_LOCATION = "joblogparser"; //$NON-NLS-1$

    // Global values
    private String JOB_NUMBER = "[0-9]{6}"; //$NON-NLS-1$
    private String OBJECT_NAME = "[\\$§#A-Z][A-Z0-9._\\$§#]{0,9}"; //$NON-NLS-1$
    private String PROGRAM = OBJECT_NAME;
    private String LIBRARY = OBJECT_NAME;
    private String LICENSED_PROGRAM = "[0-9]{4}SS[0-9]{1}"; //$NON-NLS-1$
    private String OS_RELEASE = "V[0-9]R[0-9]M[0-9]"; //$NON-NLS-1$
    private String SPACES = "[ ]+"; //$NON-NLS-1$
    private String TIMEZONE = "[A-Z]{2,5}"; //$NON-NLS-1$
    private String SYSTEM_NAME = "[A-Z][0-9A-Z]{0,7}"; //$NON-NLS-1$
    private String STMT = "\\*STMT|\\*N|[0-9A-F]{4}"; //$NON-NLS-1$

    // Page number properties
    private String PAGE_NUMBER_LABEL = "[a-zA-Z.]+"; //$NON-NLS-1$
    private String PAGE_NUMBER_VALUE = "[0-9]{1,4}"; //$NON-NLS-1$
    private String PAGE_DATE = "[0-9/\\\\-. ,]{6,8}"; //$NON-NLS-1$
    private String PAGE_TIME = "[0-9]{2}[:. ,][0-9]{2}[:. ,][0-9]{2}"; //$NON-NLS-1$

    // Page header properties
    private String HEADER_ATTRIBUTE_NAME = "[a-zA-Z][a-zA-Z ]+"; //$NON-NLS-1$
    private String HEADER_ATTRIBUTE_VALUE = "&{OBJECT_NAME}" + "|" + "&{JOB_NUMBER}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    // Message properties
    private String MESSAGE_ID = "\\*NONE|[A-Z][A-Z0-9]{2}[A-F0-9]{4}"; //$NON-NLS-1$
    private String MESSAGE_TYPE = "[A-Z][a-z ]+"; //$NON-NLS-1$
    private String MESSAGE_SEVERITY = "[0-9]{2}"; //$NON-NLS-1$
    private String MESSAGE_DATE = "[0-9/\\\\-. ,]{6,8}"; //$NON-NLS-1$
    private String MESSAGE_TIME = "(?:[0-9]{2}[:. ,][0-9]{2}[:. ,][0-9]{2})(?:,[0-9]{3,6})?"; //$NON-NLS-1$
    private String MESSAGE_CONTINUATION_LINE_INDENTION = "[ ]{30,}"; //$NON-NLS-1$

    private String MESSAGE_ATTRIBUTE_NAME = "[a-zA-Z][a-zA-Z ]+"; //$NON-NLS-1$
    private String MESSAGE_ATTRIBUTE_VALUE = "(.+)"; //$NON-NLS-1$

    // Default regular expressions
    private String regex_startOfPage;
    private String regex_headerAttribute;
    private String regex_messageFirstLine;
    private String regex_messageContinuationLine;

    // Compiled regular expression patterns
    private Pattern pattern_startOfPage;
    private Pattern pattern_headerAttribute;
    private Pattern pattern_messageFirstLine;
    private Pattern pattern_messageContinuationLine;

    /**
     * Constructs a new JobLogParserConfiguration object.
     */
    public JobLogParserConfiguration() {

        produceRegularExpressions();
        compilePattern();
    }

    /**
     * Return the regular expression pattern that is used to identify the start
     * of a page. The start-of-page line is identified by a couple of
     * properties, such as:
     * <p>
     * <ul>
     * <li>Licensed program, e.g. 5770SS1</li>
     * <li>Release, e.g. V7R2M0</li>
     * <li>Page number label, any character sequence before the page number</li>
     * <li>Page number, up to 4 numeric digits</li>
     * </ul>
     * 
     * @return regular expression pattern
     */
    public Pattern getStartOfPage() {
        return pattern_startOfPage;
    }

    /**
     * Returns the regular expression pattern, that is used to retrieve the job
     * attributes from the header of the page.
     * 
     * @return regular expression pattern
     */
    public Pattern getPageHeader() {
        return pattern_headerAttribute;
    }

    /**
     * Returns the regular expression pattern, that is used to identify the
     * start-of-message line.The start-of-message line is identified by a couple
     * of properties, such as:
     * <p>
     * <ul>
     * <li>Message Id, e.g. CPF1124</li>
     * <li>Message type, e.g. Information</li>
     * <li>Message severity, blank or a 2-digit number</li>
     * <li>Date sent</li>
     * <li>Time sent</li>
     * </ul>
     * 
     * @return regular expression pattern
     */
    public Pattern getStartOfMessage() {
        return pattern_messageFirstLine;
    }

    /**
     * Returns the regular expression pattern, that is used to retrieve the
     * message attributes from a message section of the page.
     * 
     * @return regular expression pattern
     */
    public Pattern getMessageAttribute() {
        return pattern_messageContinuationLine;
    }

    /**
     * Tests whether the configuration file for a given language exists.
     * 
     * @param languageId - Language, the configuration is loaded for
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean exists(String languageId) {

        try {

            if (findConfigurationFile(languageId, CONFIGURATION_DIRECTORY) != null) {
                return true;
            }

        } catch (UnsupportedEncodingException e) {
        }

        return false;
    }

    public String[] getAvailableLanguageIDs() {

        final List<String> languageIds = new ArrayList<String>();

        try {

            String pathName = getConfigurationDirectory();
            File directory = new File(pathName);
            if (directory.exists() || directory.isDirectory()) {

                final Pattern pattern = Pattern.compile("jobLogParser(?:_([a-z]{2}))?.properties");//$NON-NLS-1$

                directory.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {

                        if (DEFAULT_CONFIGURATION_FILE.equalsIgnoreCase(name)) {
                            languageIds.add("*DEFAULT");//$NON-NLS-1$
                        } else {
                            Matcher matcher = pattern.matcher(name);
                            if (matcher.find()) {
                                languageIds.add(matcher.group(1));
                            }
                        }
                        return false;
                    }
                });
            }

        } catch (UnsupportedEncodingException e) {
            // Only thrown, when started from a command line
            e.printStackTrace();
        }

        return languageIds.toArray(new String[languageIds.size()]);
    }

    /**
     * Loads a new configuration from a 'jobLogParser.properties' file in folder
     * '[workspace]/.metadata/.plugins/biz.isphere.joblogexplorer/'.
     * <p>
     * An example properties file can be found in package
     * 'biz.isphere.joblogexplorer.model'.
     * 
     * @param languageId - Language, the configuration is loaded for
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean loadConfiguration(String languageId) {

        FileInputStream inStream = null;
        String pathName = null;

        try {

            File path = findConfigurationFile(languageId, CONFIGURATION_DIRECTORY);
            if (path == null) {
                return false;
            }

            pathName = path.getAbsolutePath();

            Properties properties = new Properties();
            properties.load(new FileInputStream(path));

            // Global values
            JOB_NUMBER = getProperty(properties, "global.job.number", JOB_NUMBER); //$NON-NLS-1$
            OBJECT_NAME = getProperty(properties, "global.object.name", OBJECT_NAME); //$NON-NLS-1$

            PROGRAM = OBJECT_NAME;
            LIBRARY = OBJECT_NAME;

            LICENSED_PROGRAM = getProperty(properties, "global.licensed.program", LICENSED_PROGRAM); //$NON-NLS-1$
            OS_RELEASE = getProperty(properties, "global.os.release", OS_RELEASE); //$NON-NLS-1$

            SPACES = getProperty(properties, "global.spaces", SPACES); //$NON-NLS-1$

            TIMEZONE = getProperty(properties, "global.timezone", TIMEZONE); //$NON-NLS-1$
            SYSTEM_NAME = getProperty(properties, "global.system.name", SYSTEM_NAME); //$NON-NLS-1$
            STMT = getProperty(properties, "global.stmt", STMT); //$NON-NLS-1$

            // Page number properties
            PAGE_NUMBER_LABEL = getProperty(properties, "page.number.label", PAGE_NUMBER_LABEL); //$NON-NLS-1$
            PAGE_NUMBER_VALUE = getProperty(properties, "page.number.value", PAGE_NUMBER_VALUE); //$NON-NLS-1$
            PAGE_DATE = getProperty(properties, "page.date", PAGE_DATE); //$NON-NLS-1$
            PAGE_TIME = getProperty(properties, "page.time", PAGE_TIME); //$NON-NLS-1$

            // Page header properties
            HEADER_ATTRIBUTE_NAME = getProperty(properties, "header.attribute.name", HEADER_ATTRIBUTE_NAME); //$NON-NLS-1$
            HEADER_ATTRIBUTE_VALUE = getProperty(properties, "header.attribute.value", HEADER_ATTRIBUTE_VALUE); //$NON-NLS-1$

            // Message properties
            MESSAGE_ID = getProperty(properties, "message.id", MESSAGE_ID); //$NON-NLS-1$
            MESSAGE_TYPE = getProperty(properties, "message.type", MESSAGE_TYPE); //$NON-NLS-1$
            MESSAGE_SEVERITY = getProperty(properties, "message.severity", MESSAGE_SEVERITY); //$NON-NLS-1$
            MESSAGE_DATE = getProperty(properties, "message.date", MESSAGE_DATE); //$NON-NLS-1$
            MESSAGE_TIME = getProperty(properties, "message.time", MESSAGE_TIME); //$NON-NLS-1$
            MESSAGE_CONTINUATION_LINE_INDENTION = getProperty(properties, "message.continuation.line.indention", MESSAGE_CONTINUATION_LINE_INDENTION); //$NON-NLS-1$

            HEADER_ATTRIBUTE_NAME = getProperty(properties, "message.attribute.name", MESSAGE_ATTRIBUTE_NAME); //$NON-NLS-1$
            HEADER_ATTRIBUTE_VALUE = getProperty(properties, "message.attribute.value", MESSAGE_ATTRIBUTE_VALUE); //$NON-NLS-1$

            produceRegularExpressions();

            // Override default expressions
            regex_startOfPage = replaceVariables(getProperty(properties, "regex.startOfPage", regex_startOfPage)); //$NON-NLS-1$
            regex_headerAttribute = replaceVariables(getProperty(properties, "regex.headerAttribute", regex_headerAttribute)); //$NON-NLS-1$
            regex_messageFirstLine = replaceVariables(getProperty(properties, "regex.messageFirstLine", regex_messageFirstLine)); //$NON-NLS-1$
            regex_messageContinuationLine = replaceVariables(getProperty(properties, "regex.messageContinuationLine", regex_messageContinuationLine)); //$NON-NLS-1$

            compilePattern();

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not load job log parser configuration ***", e); //$NON-NLS-1$
            MessageDialogAsync.displayError(Messages.bind(Messages.Could_not_load_job_log_parser_configuration, pathName));
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }

    public void createSampleConfigurationFile() {

        InputStream in = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {

            String directory = getConfigurationDirectory();
            File outFile = new File(directory, EXAMPLE_CONFIGURATION_FILE); //$NON-NLS-1$
            if (outFile.exists()) {
                outFile.delete();
            }

            in = getClass().getResourceAsStream(DEFAULT_CONFIGURATION_FILE);
            if (in == null) {
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

            Date now = Calendar.getInstance().getTime();
            String version = ISpherePlugin.getDefault().getVersion();
            String date = formatter.format(now);

            final String NEW_LINE = System.getProperty("line.separator"); //$NON-NLS-1$

            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {

                if (version != null && line.indexOf(FIELD_VERSION) >= 0) {
                    line = line.replace(FIELD_VERSION, version);
                    version = null;
                }

                if (date != null && line.indexOf(FIELD_DATE) >= 0) {
                    line = line.replace(FIELD_DATE, date);
                    date = null;
                }

                out.append(line);
                out.append(NEW_LINE); //$NON-NLS-1$
            }

            writer = new BufferedWriter(new FileWriter(outFile));
            writer.write(out.toString());

        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Produces the final regular expressions. iSphere expressions can contain a
     * variable, such as <code>&{fooVariable}</code>, which is replaced by the
     * actual value.
     */
    private void produceRegularExpressions() {

        regex_startOfPage = replaceVariables("^&{SPACES}(&{LICENSED_PROGRAM})&{SPACES}(&{RELEASE}).+&{SPACES}(&{SYSTEM_NAME})&{SPACES}(&{PAGE_DATE})&{SPACES}(&{PAGE_TIME})&{SPACES}(&{TIMEZONE})?&{SPACES}(&{PAGE_NUMBER_LABEL})&{SPACES}(&{PAGE_NUMBER_VALUE})"); //$NON-NLS-1$
        regex_headerAttribute = replaceVariables("&{SPACES}(?:(&{HEADER_ATTRIBUTE_NAME})[. ]*:)&{SPACES}(&{HEADER_ATTRIBUTE_VALUE})"); //$NON-NLS-1$
        regex_messageFirstLine = replaceVariables("^(&{MESSAGE_ID})&{SPACES}(&{MESSAGE_TYPE})&{SPACES}(&{MESSAGE_SEVERITY})?&{SPACES}(&{MESSAGE_DATE})&{SPACES}(&{MESSAGE_TIME})" //$NON-NLS-1$
            + "&{SPACES}(&{PROGRAM})&{SPACES}(&{LIBRARY})?&{SPACES}(&{STMT})" //$NON-NLS-1$
            + "&{SPACES}(\\*EXT|&{PROGRAM})&{SPACES}(&{LIBRARY})?&{SPACES}(&{STMT})(.*)?$"); //$NON-NLS-1$
        regex_messageContinuationLine = replaceVariables("^&{MESSAGE_CONTINUATION_LINE_INDENTION}(?:(&{MESSAGE_ATTRIBUTE_NAME})[. ]*:)&{SPACES}&{MESSAGE_ATTRIBUTE_VALUE}"); //$NON-NLS-1$
    }

    /**
     * Replaces variables, such as <code>&{fooVariable}</code>.
     * 
     * @param string - regular expression string with variables.
     * @return final regular expression string
     */
    private String replaceVariables(String string) {

        String result = string;

        while (result.indexOf("&") >= 0) { //$NON-NLS-1$

            result = result.replaceAll("&\\{JOB_NUMBER}", fixEscapeCharacters(JOB_NUMBER)); //$NON-NLS-1$
            result = result.replaceAll("&\\{OBJECT_NAME}", fixEscapeCharacters(OBJECT_NAME)); //$NON-NLS-1$
            result = result.replaceAll("&\\{PROGRAM}", fixEscapeCharacters(PROGRAM)); //$NON-NLS-1$
            result = result.replaceAll("&\\{LIBRARY}", fixEscapeCharacters(LIBRARY)); //$NON-NLS-1$
            result = result.replaceAll("&\\{STMT}", fixEscapeCharacters(STMT)); //$NON-NLS-1$

            result = result.replaceAll("&\\{LICENSED_PROGRAM}", fixEscapeCharacters(LICENSED_PROGRAM)); //$NON-NLS-1$
            result = result.replaceAll("&\\{RELEASE}", fixEscapeCharacters(OS_RELEASE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{TIMEZONE}", fixEscapeCharacters(TIMEZONE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{SYSTEM_NAME}", fixEscapeCharacters(SYSTEM_NAME)); //$NON-NLS-1$

            result = result.replaceAll("&\\{PAGE_NUMBER_LABEL}", fixEscapeCharacters(PAGE_NUMBER_LABEL)); //$NON-NLS-1$
            result = result.replaceAll("&\\{PAGE_NUMBER_VALUE}", fixEscapeCharacters(PAGE_NUMBER_VALUE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{PAGE_DATE}", fixEscapeCharacters(PAGE_DATE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{PAGE_TIME}", fixEscapeCharacters(PAGE_TIME)); //$NON-NLS-1$

            result = result.replaceAll("&\\{HEADER_ATTRIBUTE_NAME}", fixEscapeCharacters(HEADER_ATTRIBUTE_NAME)); //$NON-NLS-1$
            result = result.replaceAll("&\\{HEADER_ATTRIBUTE_VALUE}", fixEscapeCharacters(HEADER_ATTRIBUTE_VALUE)); //$NON-NLS-1$

            result = result.replaceAll("&\\{MESSAGE_ID}", fixEscapeCharacters(MESSAGE_ID)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_TYPE}", fixEscapeCharacters(MESSAGE_TYPE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_SEVERITY}", fixEscapeCharacters(MESSAGE_SEVERITY)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_DATE}", fixEscapeCharacters(MESSAGE_DATE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_TIME}", fixEscapeCharacters(MESSAGE_TIME)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_ATTRIBUTE_NAME}", fixEscapeCharacters(MESSAGE_ATTRIBUTE_NAME)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_ATTRIBUTE_VALUE}", fixEscapeCharacters(MESSAGE_ATTRIBUTE_VALUE)); //$NON-NLS-1$
            result = result.replaceAll("&\\{MESSAGE_CONTINUATION_LINE_INDENTION}", fixEscapeCharacters(MESSAGE_CONTINUATION_LINE_INDENTION)); //$NON-NLS-1$

            result = result.replaceAll("&\\{SPACES}", SPACES); //$NON-NLS-1$

        }

        return result;
    }

    private String fixEscapeCharacters(String value) {
        return value.replaceAll("\\*", "\\\\*"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Compiles the regular expression patterns that are used by the
     * JobLogParser.
     */
    private void compilePattern() {

        pattern_startOfPage = Pattern.compile(regex_startOfPage);
        pattern_headerAttribute = Pattern.compile(regex_headerAttribute);
        pattern_messageFirstLine = Pattern.compile(regex_messageFirstLine);
        pattern_messageContinuationLine = Pattern.compile(regex_messageContinuationLine);
    }

    /**
     * Searches for the configuration file that is identified by a base file
     * name and a language ID.
     * 
     * @param languageId - Language that is used to identify the file.
     * @param fileName - Base file name without '_language' and '.properties'
     *        extension.
     * @return file on success, else null
     * @throws UnsupportedEncodingException
     */
    private File findConfigurationFile(String languageId, String fileName) throws UnsupportedEncodingException {

        String configFileName = ""; //$NON-NLS-1$
        if (languageId != null) {
            configFileName = fileName + "_" + languageId; //$NON-NLS-1$
        }

        File configFile = findConfigurationFile(configFileName + ".properties"); //$NON-NLS-1$
        if (configFile == null) {
            configFile = findConfigurationFile(fileName + ".properties"); //$NON-NLS-1$
        }

        return configFile;
    }

    /**
     * Checks, whether a given file exists. Uses getResource() when started from
     * a command line. Otherwise searches the file in the Eclipse workspace
     * settings folder.
     * 
     * @param fileName - File name.
     * @return file on success, else null
     * @throws UnsupportedEncodingException
     */
    private File findConfigurationFile(String fileName) throws UnsupportedEncodingException {

        String directory = getConfigurationDirectory();
        if (directory == null) {
            return null;
        }

        String path = directory + fileName;

        File configFile = new File(path);
        if (configFile.exists() && configFile.isFile()) {
            return configFile;
        }

        return null;
    }

    /**
     * Return the name of the directory where the configuration files are
     * stored.
     * 
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getConfigurationDirectory() throws UnsupportedEncodingException {

        String folder;
        if (ISphereJobLogExplorerPlugin.getDefault() != null) {
            // Executed, when started from a plug-in.
            folder = ISphereJobLogExplorerPlugin.getDefault().getStateLocation().toFile().getAbsolutePath() + File.separator + REPOSITORY_LOCATION
                + File.separator;
            FileHelper.ensureDirectory(folder);
        } else {
            // Executed, when started on a command line.
            URL url = getClass().getResource(DEFAULT_CONFIGURATION_FILE);
            if (url == null) {
                return null;
            }
            String resource = URLDecoder.decode(url.getFile(), "utf-8"); //$NON-NLS-1$
            folder = new File(resource).getParent() + File.separator;
        }
        return folder;
    }

    /**
     * Returns the property that is associated to a given key from a given
     * properties file. When the property does not exist, a default value is
     * returned.
     * 
     * @param properties - Properties that are searched for the key.
     * @param key - Key whose associated property is returned.
     * @param defaultValue - Default value that is returned if the key does not
     *        exist or when the associated value has a length of 0 bytes.
     * @return property value, identified by 'key'
     */
    private String getProperty(Properties properties, String key, String defaultValue) {

        if (properties.containsKey(key)) {
            Object value = properties.getProperty(key);
            if (value instanceof String && ((String)value).length() > 0) {
                return (String)value;
            }
        }

        return defaultValue;
    }

    /**
     * This method is used for testing purposes.
     * <p>
     * It parses the specified job log and prints the result.
     * 
     * @param args - none (not used)
     */
    public static void main(String[] args) throws Exception {

        JobLogParserConfiguration main = new JobLogParserConfiguration();
        String[] languageIDs = main.getAvailableLanguageIDs();

        System.out.println("Language IDs:"); //$NON-NLS-1$
        for (String languageId : languageIDs) {
            System.out.println("  " + languageId); //$NON-NLS-1$
        }
    }
}
