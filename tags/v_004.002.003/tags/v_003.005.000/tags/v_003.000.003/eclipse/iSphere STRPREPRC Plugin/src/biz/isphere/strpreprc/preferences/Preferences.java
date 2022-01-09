/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.preferences;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;

import biz.isphere.base.internal.FileHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.strpreprc.ISphereStrPrePrcPlugin;

/**
 * Class to manage access to the preferences of the plugin.
 * <p>
 * Eclipse stores the preferences as <i>diffs</i> to their default values in
 * directory
 * <code>[workspace]\.metadata\.plugins\org.eclipse.core.runtime\.settings\</code>.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences {

    private static String DELIMITER = "|"; //$NON-NLS-1$

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the plugin.
     */
    private IPreferenceStore preferenceStore;

    /*
     * Preferences keys:
     */

    private static final String DOMAIN = ISphereStrPrePrcPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

    private static final String TEMPLATES = DOMAIN + "TEMPLATES."; //$NON-NLS-1$

    private static final String DEFAULT_WIDTH = TEMPLATES + "DEFAULT_WIDTH"; //$NON-NLS-1$

    private static final String DEFAULT_RIGHT_COMMENT_CHAR = TEMPLATES + "DEFAULT_RIGHT_COMMENT_CHAR"; //$NON-NLS-1$

    private static final String DEFAULT_LEFT_COMMENT_CHAR = TEMPLATES + "DEFAULT_LEFT_COMMENT_CHAR"; //$NON-NLS-1$

    private static final String DEFAULT_TAB_WIDTH = TEMPLATES + "DEFAULT_TAB"; //$NON-NLS-1$

    private static final String DEFAULT_INDENT = TEMPLATES + "DEFAULT_INDENT"; //$NON-NLS-1$

    private static final String DEFAULT_KEYWORDS = TEMPLATES + "DEFAULT_BASE_KEYWORDS"; //$NON-NLS-1$

    private static final String BASE_KEYWORDS = TEMPLATES + "BASE_KEYWORDS"; //$NON-NLS-1$

    private static final String TEMPLATE_DIRECTORY = TEMPLATES + "TEMPLATE_DIRECTORY"; //$NON-NLS-1$

    private static final String USE_TEMPLATE_DIRECTORY = TEMPLATES + "USE_TEMPLATE_DIRECTORY"; //$NON-NLS-1$

    private static final String USE_PARAMETER_SECTIONS = TEMPLATES + "USE_SECTIONS_AT_ALL"; //$NON-NLS-1$

    private static final String DEFAULT_SECTION = TEMPLATES + "DEFAULT_SECTION"; //$NON-NLS-1$

    private static final String SKIP_EDIT_DIALOG = TEMPLATES + "SKIP_EDIT_DIALOG"; //$NON-NLS-1$

    public static final String IMPORTANT = "IMPORTANT";
    public static final String COMPILE = "COMPILE";
    public static final String LINK = "LINK";

    /*
     * Name of the default template directory
     */

    private static final String TEMPLATE_DIRECTORY_NAME = "templates";

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISphereStrPrePrcPlugin.getDefault().getPreferenceStore();
        }
        return instance;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }

    /*
     * Preferences: GETTER
     */

    public int getDefaultWidth() {
        return preferenceStore.getInt(DEFAULT_WIDTH);
    }

    public String getDefaultRightCommentChar() {
        return preferenceStore.getString(DEFAULT_RIGHT_COMMENT_CHAR);
    }

    public String getDefaultLeftCommentChar() {
        return preferenceStore.getString(DEFAULT_LEFT_COMMENT_CHAR);
    }

    public int getDefaultTabWidth() {
        return preferenceStore.getInt(DEFAULT_TAB_WIDTH);
    }

    public int getDefaultIndention() {
        return preferenceStore.getInt(DEFAULT_INDENT);
    }

    public String getDefaultKeywords() {
        return " " + preferenceStore.getString(DEFAULT_KEYWORDS).trim(); //$NON-NLS-1$
    }

    public String[] getBaseKeywords() {
        return stringToArray(preferenceStore.getString(BASE_KEYWORDS));
    }

    public String getTemplateDirectory() {
        return preferenceStore.getString(TEMPLATE_DIRECTORY);
    }

    public boolean useTemplateDirectory() {
        return preferenceStore.getBoolean(USE_TEMPLATE_DIRECTORY);
    }

    public boolean useParameterSections() {
        return preferenceStore.getBoolean(USE_PARAMETER_SECTIONS);
    }

    public String getDefaultSection() {
        return preferenceStore.getString(DEFAULT_SECTION);
    }

    public boolean skipEditDialog() {
        return preferenceStore.getBoolean(SKIP_EDIT_DIALOG);
    }

    /*
     * Preferences: SETTER
     */

    public void setDefaultWidth(int width) {
        preferenceStore.setValue(DEFAULT_WIDTH, width);
    }

    public void setDefaultRightCommentChar(String rightCommentChar) {
        preferenceStore.setValue(DEFAULT_RIGHT_COMMENT_CHAR, rightCommentChar);
    }

    public void setDefaultLeftCommentChar(String leftCommentChar) {
        preferenceStore.setValue(DEFAULT_LEFT_COMMENT_CHAR, leftCommentChar);
    }

    public void setDefaultTabWidth(int width) {
        preferenceStore.setValue(DEFAULT_TAB_WIDTH, width);
    }

    public void setDefaultIndention(int indent) {
        preferenceStore.setValue(DEFAULT_INDENT, indent);
    }

    public void setDefaultKeywords(String baseKeywords) {
        preferenceStore.setValue(DEFAULT_KEYWORDS, baseKeywords);
    }

    public void setBaseKeywords(String[] baseKeywords) {
        preferenceStore.setValue(BASE_KEYWORDS, arrayToString(baseKeywords));
    }

    public void setTemplateDirectory(String directory) {
        preferenceStore.setValue(TEMPLATE_DIRECTORY, directory);
    }

    public void setUseTemplateDirectory(boolean useIt) {
        preferenceStore.setValue(USE_TEMPLATE_DIRECTORY, useIt);
    }

    public void setUseParameterSections(boolean useIt) {
        preferenceStore.setValue(USE_PARAMETER_SECTIONS, useIt);
    }

    public void setDefaultSection(String section) {
        preferenceStore.setValue(DEFAULT_SECTION, section);
    }

    public void setSkipEditDialog(boolean skip) {
        preferenceStore.setValue(SKIP_EDIT_DIALOG, skip);
    }

    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        preferenceStore.setDefault(DEFAULT_WIDTH, getInitialDefaultWidth());
        preferenceStore.setDefault(DEFAULT_RIGHT_COMMENT_CHAR, getInitialDefaultRightCommentChar());
        preferenceStore.setDefault(DEFAULT_LEFT_COMMENT_CHAR, getInitialDefaultLeftCommentChar());
        preferenceStore.setDefault(DEFAULT_TAB_WIDTH, getInitialDefaultTabWidth());
        preferenceStore.setDefault(DEFAULT_INDENT, getInitialDefaultIndention());
        preferenceStore.setDefault(DEFAULT_KEYWORDS, getInitialDefaultKeywords());
        preferenceStore.setDefault(BASE_KEYWORDS, getInitialBaseKeywords());
        preferenceStore.setDefault(TEMPLATE_DIRECTORY, getInitialTemplateDirectory());
        preferenceStore.setDefault(USE_TEMPLATE_DIRECTORY, getInitialUseTemplateDirectory());
        preferenceStore.setDefault(USE_PARAMETER_SECTIONS, getInitialUseParameterSections());
        preferenceStore.setDefault(DEFAULT_SECTION, getInitialDefaultSection());
        preferenceStore.setDefault(SKIP_EDIT_DIALOG, getInitialSkipEditDialog());
    }

    /*
     * Preferences: Default Values
     */

    public int getInitialDefaultWidth() {
        return 76;
    }

    public String getInitialDefaultRightCommentChar() {
        return "*"; //$NON-NLS-1$
    }

    public String getInitialDefaultLeftCommentChar() {
        return "*"; //$NON-NLS-1$
    }

    public int getInitialDefaultTabWidth() {
        return 2;
    }

    public int getInitialDefaultIndention() {
        return 6;
    }

    public String getInitialDefaultKeywords() {
        return "SRCFILE(&SL/&SF) SRCMBR(&SM)";
    }

    public String getInitialBaseKeywords() {
        return "SRCFILE|SRCMBR";
    }

    public String getInitialTemplateDirectory() {

        String path = ISphereStrPrePrcPlugin.getDefault().getStateLocation().toFile().getAbsolutePath();
        path = path + File.separator + TEMPLATE_DIRECTORY_NAME + File.separator;

        FileHelper.ensureDirectory(path);

        return path;
    }

    public boolean getInitialUseTemplateDirectory() {
        return true;
    }

    public boolean getInitialUseParameterSections() {
        return true;
    }

    public String getInitialDefaultSection() {
        return IMPORTANT;
    }

    public String[] getSections() {
        return new String[] { IMPORTANT, COMPILE, LINK };
    }

    public boolean getInitialSkipEditDialog() {
        return false;
    }

    /*
     * Helpers
     */

    private String arrayToString(String[] baseKeywords) {

        StringBuilder baseKeywordsBuffer = new StringBuilder();
        for (String keyword : baseKeywords) {
            baseKeywordsBuffer.append(keyword);
            baseKeywordsBuffer.append(DELIMITER);
        }

        return baseKeywordsBuffer.toString();
    }

    private String[] stringToArray(String baseKeywordsString) {

        if (StringHelper.isNullOrEmpty(baseKeywordsString)) {
            return new String[0];
        }

        return baseKeywordsString.split("\\|");
    }
}