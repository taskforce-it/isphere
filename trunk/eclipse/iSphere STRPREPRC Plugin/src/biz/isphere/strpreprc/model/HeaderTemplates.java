/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.strpreprc.Messages;
import biz.isphere.strpreprc.preferences.Preferences;

public final class HeaderTemplates {

    private static final String TEMPLATE_FILE_EXTENSION = ".strpreprc";

    private static final String CRLF = "\n";
    private static final String EOL = ";";
    private static final String OBJECT_VARIABLES = "&LI/&OB";

    private static final String DEFAULT = "*dft";
    private static final String SQLRPGLE = "sqlrpgle";
    private static final String RPGLE = "rpgle";
    private static final String C = "c";
    private static final String CLP = "clp";
    private static final String CLLE = "clle";
    private static final String PNLGRP = "pnlgrp";

    private Properties defaultCreationCommands;
    private Properties defaultImportantParameters;
    private Properties templates;
    private Set<String> baseKeywords;
    private Preferences preferences;

    /**
     * The instance of this Singleton class.
     */
    private static HeaderTemplates instance;

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private HeaderTemplates() {

        preferences = Preferences.getInstance();
        loadBaseKeywords();
        loadCreationCommands();
    }

    private void loadCreationCommands() {

        defaultCreationCommands = new Properties();
        defaultCreationCommands.put(DEFAULT, "...");
        defaultCreationCommands.put(SQLRPGLE, "CRTSQLRPGI MODULE(" + OBJECT_VARIABLES + ")" + preferences.getDefaultKeywords());
        defaultCreationCommands.put(RPGLE, "CRTRPGMOD MODULE(" + OBJECT_VARIABLES + ")" + preferences.getDefaultKeywords());
        defaultCreationCommands.put(C, "CRTCMOD MODULE(" + OBJECT_VARIABLES + ") OUTPUT(*PRINT)" + preferences.getDefaultKeywords());
        defaultCreationCommands.put(CLP, "CRTCLPGM " + preferences.getDefaultKeywords());
        defaultCreationCommands.put(CLLE, "CRTCLMOD MODULE(" + OBJECT_VARIABLES + ") " + preferences.getDefaultKeywords());
        defaultCreationCommands.put(PNLGRP, "PNLGRP PNLGRP(" + OBJECT_VARIABLES + ") " + preferences.getDefaultKeywords());

        defaultImportantParameters = new Properties();
        defaultImportantParameters.put(DEFAULT, new String[] { "TEXT('Hello World')" });
        defaultImportantParameters.put(SQLRPGLE, new String[] { "OBJ(" + OBJECT_VARIABLES + ")", "OBJTYPE(*MODULE)", "RPGPPOPT(*LVL2)",
            "CLOSQLCSR(*ENDACTGRP)", "DATFMT(*ISO)", "TIMFMT(*ISO)", "OPTION(*EVENTF)" });
        defaultImportantParameters.put(RPGLE, new String[] { "TRUNCNBR(*NO)", "DBGVIEW(*LIST)", "OPTION(*EVENTF)" });
        defaultImportantParameters.put(C, new String[] { "SYSIFCOPT(*NOIFSIO)", "OPTION(*EVENTF)" });
        defaultImportantParameters.put(CLP, new String[] { "PGM(" + OBJECT_VARIABLES + ")", "OPTION(*LISTDBG)" });
        defaultImportantParameters.put(CLLE, new String[] { "DBGVIEW(*LIST)", "OPTION(*EVENTF)" });
        defaultImportantParameters.put(PNLGRP, new String[] { "OPTION(*EVENTF)" });
    }

    private Properties loadTemplates() {

        if (templates != null) {
            return templates;
        }

        templates = new Properties();

        // loadExternalTemplates(preferences.getTemplateDirectory());

        int dftIndent = preferences.getDefaultIndention();

        generateTemplates(DEFAULT, "/*", "*/", dftIndent);
        generateTemplates(SQLRPGLE, "*", "", dftIndent);
        generateTemplates(RPGLE, "*", "", dftIndent);
        generateTemplates(C, "/*", "*/", dftIndent);
        generateTemplates(CLP, "/*", "*/", dftIndent);
        generateTemplates(CLLE, "/*", "*/", dftIndent);
        generateTemplates(PNLGRP, ".*", "", 0);

        return templates;
    }

    private void generateTemplates(String memberType, String leftCommentChar, String rightCommentChar, int indent) {
        if (!templates.contains(memberType)) {
            String creationCommand = getCreationCommand(memberType);
            String[] importantParameters = getImportantParameters(memberType);
            templates.put(memberType, createTemplate(memberType, creationCommand, importantParameters, leftCommentChar, rightCommentChar, indent));
        }
    }

    private void loadBaseKeywords() {

        String[] baseKeywords = preferences.getBaseKeywords();
        this.baseKeywords = new HashSet<String>();
        this.baseKeywords.addAll(Arrays.asList(baseKeywords));
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static HeaderTemplates getInstance() {
        if (instance == null) {
            instance = new HeaderTemplates();
        }
        return instance;
    }

    /**
     * Returns the template for a given source member type.
     * 
     * @param memberType - IBM i source member type
     */
    public String[] getTemplate(String memberType) {
        List<String> template = lookupTemplate(memberType.toLowerCase());
        if (template == null) {
            template = new ArrayList<String>();
        }
        return template.toArray(new String[template.size()]);
    }

    /**
     * Clears the template cache to enforce reloading or generating the
     * templates.
     */
    public void clearTemplatesCache() {
        templates = null;
    }

    /**
     * Returns the default object creation command for a given source member
     * type.
     * 
     * @param memberType - IBM i source member type
     */
    private String getCreationCommand(String memberType) {

        return defaultCreationCommands.getProperty(memberType, ""); //$NON-NLS-1$
    }

    /**
     * Returns the default important parameters for a given source member type.
     * 
     * @param memberType - IBM i source member type
     */
    private String[] getImportantParameters(String memberType) {

        return (String[])defaultImportantParameters.get(memberType); //$NON-NLS-1$
    }

    /**
     * Checks, whether a given keyword belongs to the list of keywords that are
     * part of the base object creation command.
     * 
     * @param keyword - keyword that is checked
     * @return <code>true</true> for a keyword of the base create command, else <code>false</code>
     */
    private boolean isBaseKeyword(String keyword) {

        return isBaseKeyword(keyword, null);
    }

    /**
     * Checks, whether a given keyword belongs to the list of keywords that are
     * part of the base object creation command.
     * 
     * @param keyword - keyword that is checked
     * @param memberType - IBM i source member type
     * @return <code>true</true> for a keyword of the base create command, else <code>false</code>
     */
    public boolean isBaseKeyword(String keyword, String memberType) {

        return baseKeywords.contains(keyword);
    }

    /**
     * Saves all known templates to the specified directory.
     * 
     * @param directoryName - directory to store the templates.
     * @return <code>true</code> on success, else <code>false</code>
     */
    public String save(String directoryName) {

        if (StringHelper.isNullOrEmpty(directoryName)) {
            return Messages.The_specified_directory_does_not_exist;
        }

        File directory = new File(directoryName);
        if (!directory.exists() || !directory.isDirectory()) {
            return Messages.The_specified_directory_does_not_exist;
        }

        int errorCounter = 0;
        int warningCounter = 0;
        for (Object keyValue : loadTemplates().keySet()) {
            if (keyValue instanceof String) {
                String key = (String)keyValue;

                List<String> template = lookupTemplate(key);
                if (template != null && template.size() > 0) {
                    File file = new File(directory, getTemplateFileName(key));
                    if (file.exists()) {
                        warningCounter++;
                    } else {
                        if (!saveTemplateToFile(template, file)) {
                            errorCounter++;
                        }
                    }
                }
            }
        }

        if (errorCounter > 0) {
            return Messages.bind(Messages.Could_not_save_A_files_to_directory_B, new Object[] { Integer.valueOf(errorCounter), directoryName });
        }

        return null;
    }

    private List<String> lookupTemplate(String key) {
        return (List<String>)loadTemplates().get(key);
    }

    private boolean saveTemplateToFile(List<String> template, File file) {

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(templateToString(template));
            return true;
        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not save template to file '" + file.getAbsolutePath() + "' ***", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Throwable e) {
            }
        }

        return false;
    }

    private String templateToString(List<String> template) {

        StringBuilder buffer = new StringBuilder();

        for (String line : template) {
            buffer.append(line);
            buffer.append(CRLF);
        }

        return buffer.toString();
    }

    private String getTemplateFileName(String key) {

        if (key.startsWith("*")) {
            key = key.substring(1);
        }

        String fileName = key + TEMPLATE_FILE_EXTENSION;

        return fileName;
    }

    private List<String> createTemplate(String memberType, String creationCommand, String[] importantParameters, String leftCommentChar,
        String rightCommentChar, int indent) {

        // Produce a basic template with a dummy command
        List<String> buffer = new ArrayList<String>();
        produceStartTag(buffer, leftCommentChar, rightCommentChar, indent);
        produceTag(buffer, StrPrePrc.CREATE_COMMAND, "*DUMMY*", leftCommentChar, rightCommentChar, indent);

        if (importantParameters != null && importantParameters.length > 0) {
            if (preferences.priorizeImportantSection()) {
                produceTag(buffer, StrPrePrc.IMPORTANT_START, "", leftCommentChar, rightCommentChar, indent);
                produceTag(buffer, StrPrePrc.PARAMETER, importantParameters, leftCommentChar, rightCommentChar, indent);
                produceTag(buffer, StrPrePrc.IMPORTANT_END, "", leftCommentChar, rightCommentChar, indent);
            } else {
                produceTag(buffer, StrPrePrc.COMPILE_START, "", leftCommentChar, rightCommentChar, indent);
                produceTag(buffer, StrPrePrc.PARAMETER, importantParameters, leftCommentChar, rightCommentChar, indent);
                produceTag(buffer, StrPrePrc.COMPILE_END, "", leftCommentChar, rightCommentChar, indent);
            }
        }

        produceEndTag(buffer, leftCommentChar, rightCommentChar, indent);
        produceExecuteTag(buffer, leftCommentChar, rightCommentChar, indent);

        // Load the basic template into the parser and update the object
        // creation command to get a properly formatted template
        StrPrePrcParser parser = new StrPrePrcParser(memberType);
        parser.loadTemplate(buffer.toArray(new String[buffer.size()]));
        parser.storeCreateCommand(creationCommand);
        String[] lines = parser.getHeader(null);

        // Return the final template to the caller
        buffer.clear();
        buffer.addAll(Arrays.asList(lines));

        return buffer;
    }

    private void produceStartTag(List<String> buffer, String leftCommentChar, String rightCommentChar, int indent) {
        produceTag(buffer, StrPrePrc.PRE_COMPILER_START, leftCommentChar, rightCommentChar, indent);
    }

    private void produceEndTag(List<String> buffer, String leftCommentChar, String rightCommentChar, int indent) {
        produceTag(buffer, StrPrePrc.PRE_COMPILER_END, leftCommentChar, rightCommentChar, indent);
    }

    private void produceExecuteTag(List<String> buffer, String leftCommentChar, String rightCommentChar, int indent) {
        produceTag(buffer, StrPrePrc.EXECUTE, leftCommentChar, rightCommentChar, indent);
    }

    private void produceTag(List<String> buffer, String tag, String leftCommentChar, String rightCommentChar, int indent) {
        produceTag(buffer, tag, "", leftCommentChar, rightCommentChar, indent);
    }

    private void produceTag(List<String> buffer, String tag, String[] values, String leftCommentChar, String rightCommentChar, int indent) {
        for (String value : values) {
            produceTag(buffer, tag, value, leftCommentChar, rightCommentChar, indent);
        }
    }

    private void produceTag(List<String> buffer, String tag, String value, String leftCommentChar, String rightCommentChar, int indent) {

        int tabWidth = preferences.getDefaultTabWidth();

        StringBuilder line = new StringBuilder();

        line.append(spaces(indent));
        line.append(leftCommentChar);
        line.append(spaces(tabWidth));
        line.append(tag);

        if (!StringHelper.isNullOrEmpty(value)) {
            line.append(spaces(tabWidth));
            line.append(value);
            line.append(EOL);
        }

        int width = preferences.getDefaultWidth();

        if (!StringHelper.isNullOrEmpty(rightCommentChar) && width > 0) {
            if (line.length() < width - rightCommentChar.length()) {
                line.append(spaces(width - rightCommentChar.length() - line.length()));
                line.append(rightCommentChar);
            }
        }

        buffer.add(line.toString());
    }

    private String spaces(int length) {
        return StringHelper.getFixLength("", length); //$NON-NLS-1$
    }

    public static void dispose() {
        instance = null;
    }
}