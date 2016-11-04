/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.regex.Pattern;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.jobloganalyzer.ISphereJobLogAnalyzerPlugin;

public class JobLogReaderConfiguration {

    private static final String REPOSITORY_LOCATION = "joblogparser";

    private String LICENSED_PROGRAM = "[0-9]{4}SS[0-9]{1}";
    private String RELEASE = "V[0-9]{1,1}R[0-9]{1,1}M[0-9]{1,1}";
    private String PAGE_NUMBER_LABEL = "[a-zA-Z]+";
    private String PAGE_NUMBER_VALUE = "[ ]+[0-9]{1,4}";
    private String HEADER_ATTRIBUTE_NAME = "[a-zA-Z ]+";
    private String HEADER_ATTRIBUTE_VALUE = "[A-Z0-9]{1,10}";
    private String MESSAGE_ID = "[A-Z][A-Z0-9]{2}[A-F0-9]{4}";
    private String MESSAGE_TYPE = "[A-Z][a-z]+";
    private String MESSAGE_SEVERITY = "[0-9]{2}";
    private String MESSAGE_DATE = "[0-9/\\\\-. ,]{6,8}";
    private String MESSAGE_TIME = "[0-9:.,]{15}";
    private String MESSAGE_CONTINUATION_LINE_INDENTION = "30";
    private String MESSAGE_ATTRIBUTE_NAME = "([a-zA-Z ]+)[. ]+";
    private String MESSAGE_ATTRIBUTE_VALUE = "(.+)";

    private String regex_pageNumber;
    private String regex_headerAttribute;
    private String regex_messageFirstLine;
    private String regex_messageContinuationLine;

    private Pattern pattern_pageNumber;
    private Pattern pattern_headerAttribute;
    private Pattern pattern_messageFirstLine;
    private Pattern pattern_messageContinuationLine;

    private static final String SPACES = "[ ]+";

    /**
     * Constructs a new JobLogReaderConfiguration object.
     */
    public JobLogReaderConfiguration() {

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
        return pattern_pageNumber;
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
     * Loads a new configuration from a 'jobLogParser.properties' file in folder
     * '[workspace]/.metadata/.plugins/biz.isphere.jobloganalyzer/'.
     * <p>
     * An example properties file can be found in package
     * 'biz.isphere.jobloganalyzer.model'.
     * 
     * @param locale - Locale, the configuration is loaded for
     * @return <code>true</code> on success, else <code>false</code>.
     */
    public boolean loadConfiguration(String locale) {

        FileInputStream inStream = null;

        try {

            File path = findConfigurationFile(locale, "jobLogParser"); //$NON-NLS-1$
            if (path == null) {
                return false;
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(path));

            // Page number properties
            LICENSED_PROGRAM = getProperty(properties, "global.licensed.program", LICENSED_PROGRAM);
            RELEASE = getProperty(properties, "global.os.release", RELEASE);
            PAGE_NUMBER_LABEL = getProperty(properties, "page.number.label", PAGE_NUMBER_LABEL);
            PAGE_NUMBER_VALUE = getProperty(properties, "page.number.value", PAGE_NUMBER_VALUE);

            // Page header properties
            HEADER_ATTRIBUTE_NAME = getProperty(properties, "header.attribute.name", HEADER_ATTRIBUTE_NAME);
            HEADER_ATTRIBUTE_VALUE = getProperty(properties, "header.attribute.value", HEADER_ATTRIBUTE_VALUE);

            // Message properties
            MESSAGE_ID = getProperty(properties, "message.id", MESSAGE_ID);
            MESSAGE_TYPE = getProperty(properties, "message.type", MESSAGE_TYPE);
            MESSAGE_SEVERITY = getProperty(properties, "message.severity", MESSAGE_SEVERITY);
            MESSAGE_DATE = getProperty(properties, "message.date", MESSAGE_DATE);
            MESSAGE_TIME = getProperty(properties, "message.time", MESSAGE_TIME);
            MESSAGE_CONTINUATION_LINE_INDENTION = getProperty(properties, "message.continuation.line.indention", MESSAGE_CONTINUATION_LINE_INDENTION);

            produceRegularExpressions();

            // Override default expressions
            regex_pageNumber = getProperty(properties, "regex.pageNumber", regex_pageNumber);
            regex_headerAttribute = getProperty(properties, "regex.headerAttribute", regex_headerAttribute);
            regex_messageFirstLine = getProperty(properties, "regex.messageFirstLine", regex_messageFirstLine);
            regex_messageContinuationLine = getProperty(properties, "regex.messageContinuationLine", regex_messageContinuationLine);

            compilePattern();

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not load job log parser configuration ***", e);
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

    /**
     * Produces the final regular expressions. iSphere expressions can contain a
     * variable, such as <code>&{fooVariable}</code>, which is replaced by the
     * actual value.
     */
    private void produceRegularExpressions() {

        regex_pageNumber = replaceVariables("^ *(&{LICENSED_PROGRAM}).+(&{RELEASE}).+&{PAGE_NUMBER_LABEL}(&{PAGE_NUMBER_VALUE})");
        regex_headerAttribute = replaceVariables("(&{HEADER_ATTRIBUTE_NAME})[. ]*:&{SPACES}(&{HEADER_ATTRIBUTE_VALUE})");
        regex_messageFirstLine = replaceVariables("(^\\*NONE|&{MESSAGE_ID})&{SPACES}(&{MESSAGE_TYPE})&{SPACES}(&{MESSAGE_SEVERITY})?&{SPACES}(&{MESSAGE_DATE})&{SPACES}(&{MESSAGE_TIME})(.*$)");
        regex_messageContinuationLine = replaceVariables("^[ ]{&{MESSAGE_CONTINUATION_LINE_INDENTION},}&{MESSAGE_ATTRIBUTE_NAME}:&{SPACES}&{MESSAGE_ATTRIBUTE_VALUE}");
    }

    /**
     * Replaces variables, such as <code>&{fooVariable}</code>.
     * 
     * @param string - regular expression string with variables.
     * @return final regular expression string
     */
    private String replaceVariables(String string) {

        String result = string;

        result = result.replaceAll("&\\{LICENSED_PROGRAM}", LICENSED_PROGRAM);
        result = result.replaceAll("&\\{RELEASE}", RELEASE);
        result = result.replaceAll("&\\{PAGE_NUMBER_LABEL}", PAGE_NUMBER_LABEL);
        result = result.replaceAll("&\\{PAGE_NUMBER_VALUE}", PAGE_NUMBER_VALUE);

        result = result.replaceAll("&\\{HEADER_ATTRIBUTE_NAME}", HEADER_ATTRIBUTE_NAME);
        result = result.replaceAll("&\\{HEADER_ATTRIBUTE_VALUE}", HEADER_ATTRIBUTE_VALUE);

        result = result.replaceAll("&\\{MESSAGE_ID}", MESSAGE_ID);
        result = result.replaceAll("&\\{MESSAGE_TYPE}", MESSAGE_TYPE);
        result = result.replaceAll("&\\{MESSAGE_SEVERITY}", MESSAGE_SEVERITY);
        result = result.replaceAll("&\\{MESSAGE_DATE}", MESSAGE_DATE);
        result = result.replaceAll("&\\{MESSAGE_TIME}", MESSAGE_TIME);
        result = result.replaceAll("&\\{MESSAGE_ATTRIBUTE_NAME}", MESSAGE_ATTRIBUTE_NAME);
        result = result.replaceAll("&\\{MESSAGE_ATTRIBUTE_VALUE}", MESSAGE_ATTRIBUTE_VALUE);
        result = result.replaceAll("&\\{MESSAGE_CONTINUATION_LINE_INDENTION}", MESSAGE_CONTINUATION_LINE_INDENTION);

        result = result.replaceAll("&\\{SPACES}", SPACES);

        return result;
    }

    /**
     * Compiles the regular expression patterns that are used by the
     * JobLogReader.
     */
    private void compilePattern() {

        pattern_pageNumber = Pattern.compile(regex_pageNumber);
        pattern_headerAttribute = Pattern.compile(regex_headerAttribute);
        pattern_messageFirstLine = Pattern.compile(regex_messageFirstLine);
        pattern_messageContinuationLine = Pattern.compile(regex_messageContinuationLine);
    }

    /**
     * Searches for the configuration file that is identified by a base file
     * name and a locale.
     * 
     * @param locale - Locale that is used to identify the file.
     * @param fileName - Base file name without locale and '.properties'
     *        extension.
     * @return file on success, else null
     * @throws UnsupportedEncodingException
     */
    private File findConfigurationFile(String locale, String fileName) throws UnsupportedEncodingException {

        String configFileName = ""; ////$NON-NLS-1$
        if (locale != null) {
            configFileName = fileName + "_" + locale; //$NON-NLS-1$
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

        String path;
        if (ISphereJobLogAnalyzerPlugin.getDefault() != null) {
            // Executed, when started from a plug-in.
            String folder = ISphereJobLogAnalyzerPlugin.getDefault().getStateLocation().toFile().getAbsolutePath() + File.separator
                + REPOSITORY_LOCATION + File.separator;
            FileHelper.ensureDirectory(folder);
            path = folder + fileName;
        } else {
            // Executed, when started on a command line.
            URL url = getClass().getResource(fileName);
            if (url == null) {
                return null;
            }
            path = URLDecoder.decode(url.getFile(), "utf-8"); //$NON-NLS-1$
        }

        File configFile = new File(path);
        if (configFile.exists() && configFile.isFile()) {
            return configFile;
        }

        return null;
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
}
