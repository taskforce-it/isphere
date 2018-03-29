/*
 * @(#)SessionConfig.java
 * Copyright:    Copyright (c) 2001
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 */
package org.tn5250j;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.tn5250j.event.SessionConfigEvent;
import org.tn5250j.event.SessionConfigListener;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.settings.ColorProperty;
import org.tn5250j.tools.GUIGraphicsUtils;

/**
 * A host session configuration object.
 * <p>
 * This class covers all service regarding a 5250 session configuration. "Load"
 * and "Save" operations are delegated to the {@link ConfigureFactory} class.
 * <p>
 * The class has been enhanced to support "session themes". A session theme
 * contains color properties, that override the color properties of the session
 * configuration.
 * <p>
 * Process of loading and overloading session properties with theme properties:
 * <ol>
 * <li>Load original session configuration.</li>
 * <li>Save color properties of session configuration.</li>
 * <li>Overlay color properties with theme properties.</li>
 * </ol>
 * <p>
 * Process of saving session and theme properties:
 * <ol>
 * <li>Save theme color properties.</li>
 * <li>Restore original session color properties.</li>
 * <li>Save original session properties.</li>
 * </ol>
 */
public class SessionConfig {

    private static final String IS_DIRTY_FLAG = "saveme";
    private static final String THEME_CONFIGURATION_FILE_SUFFIX = ".theme";
    private static final String THEME_CONFIGURATION_KEY = "sessionTheme";
    private static final String THEME_CONFIGURATION_HEADER = "--- Session Theme ---";

    public static final String THEME_NONE = "*NONE";

    private String configurationResource;
    private String sessionName;
    private String sessionTheme;
    private boolean sessionThemeEnabled;
    private Properties sesProps;
    private boolean usingDefaults;

    private Vector listeners;

    private String themeConfigurationFile;
    private Properties themeColorProperties;

    public SessionConfig(String configurationResource, String sessionName) {

        this(configurationResource, sessionName, "");

    }

    public SessionConfig(String configurationResource, String sessionName, String sessionTheme) {

        this.configurationResource = configurationResource;
        this.sessionName = sessionName;
        this.sessionTheme = sessionTheme;

        if (hasSessionTheme()) {
            themeConfigurationFile = sessionTheme + THEME_CONFIGURATION_FILE_SUFFIX;
            setSessionThemeEnabled(true);
        } else {
            setSessionThemeEnabled(false);
        }

        loadConfigurationResource();
    }

    public static String[] loadThemes() {

        return ConfigureFactory.getInstance().loadThemeNames(THEME_CONFIGURATION_FILE_SUFFIX);
    }

    private void setSessionThemeEnabled(boolean enabled) {

        if (!hasSessionTheme()) {
            this.sessionThemeEnabled = false;
        } else {
            this.sessionThemeEnabled = enabled;
        }
    }

    public String getConfigurationResource() {

        if (configurationResource == null || configurationResource == "") {
            configurationResource = "TN5250JDefaults.props";
            usingDefaults = true;
        }

        return configurationResource;

    }

    public String getSessionName() {
        return sessionName;
    }

    /**
     * Returns the name of the session theme.
     * 
     * @return session theme name
     */
    public String getSessionTheme() {
        return sessionTheme;
    }

    public void firePropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {

        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }

        System.out.println("SessionConfig.firePropertyChange(): " + source + " (" + propertyName + ")");

        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector)listeners.clone();
            }
        }

        SessionConfigEvent sce = new SessionConfigEvent(source, propertyName, oldValue, newValue, sessionTheme);

        if (targets != null) {
            int size = targets.size();
            for (int i = 0; i < size; i++) {
                SessionConfigListener target = (SessionConfigListener)targets.elementAt(i);
                target.onConfigChanged(sce);
            }
        }
    }

    public boolean isModified() {

        return isSessionPropertyDirty() || isThemePropertyDirty();
    }

    public void resetModified() {

        setSessionPropertiesDirty(false);
        setThemePropertiesDirty(false);
    }

    public void saveSessionProps() {

        if (isThemePropertyDirty()) {
            saveThemeProps();
        }

        if (isSessionPropertyDirty()) {

            System.out.println("==> SessionConfig.saveSessionProps()");

            setSessionPropertiesDirty(false);

            if (usingDefaults) {

                ConfigureFactory.getInstance().saveSettings("dfltSessionProps", getConfigurationResource(), "");

            } else {
                try {
                    FileOutputStream out = new FileOutputStream(settingsDirectory() + getConfigurationResource());
                    // save off the width and height to be restored later
                    sesProps.store(out, "------ Defaults --------");
                } catch (FileNotFoundException fnfe) {
                } catch (IOException ioe) {
                }
            }
        }
    }

    /**
     * Saves the properties of the session theme.
     */
    private void saveThemeProps() {

        System.out.println("==> SessionConfig.saveThemeProps()");

        saveThemeProps(themeConfigurationFile);
    }

    /**
     * Saves the properties of the session theme.
     */
    public void saveThemeProps(String fileName) {

        setThemePropertiesDirty(false);

        ConfigureFactory.getInstance().saveSettings(getThemeConfigurationKey(fileName), fileName, THEME_CONFIGURATION_HEADER);
    }

    private String getThemeConfigurationKey(String fileName) {
        return THEME_CONFIGURATION_KEY + "_" + fileName;
    }

    private void loadConfigurationResource() {

        sesProps = new Properties();

        if (configurationResource == null || configurationResource == "") {
            configurationResource = "TN5250JDefaults.props";
            usingDefaults = true;
            loadDefaults();
        } else {
            try {
                FileInputStream in = new FileInputStream(settingsDirectory() + getConfigurationResource());
                sesProps.load(in);
                if (sesProps.size() == 0) {
                    loadDefaults();
                }
            } catch (IOException ioe) {
                System.out.println("Information Message: Properties file is being " + "created for first time use:  File name "
                    + getConfigurationResource());
                loadDefaults();
            } catch (SecurityException se) {
                System.out.println(se.getMessage());
            }
        }

        if (sessionThemeEnabled) {
            overlayConfigurationWithTheme();
        }
    }

    private String settingsDirectory() {
        return ConfigureFactory.getInstance().getProperty("emulator.settingsDirectory");
    }

    private void loadDefaults() {

        try {

            sesProps = ConfigureFactory.getInstance().getProperties("dfltSessionProps", getConfigurationResource(), true, "Default Settings");
            if (sesProps.size() == 0) {

                ClassLoader cl = this.getClass().getClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }

                // emul defaults
                java.net.URL file = cl.getResource(getConfigurationResource());
                Properties emuldefaults = new Properties();
                emuldefaults.load(file.openStream());
                sesProps.putAll(emuldefaults);

                // color schema defaults
                file = cl.getResource("tn5250jSchemas.properties");
                Properties schemaProps = new Properties();
                schemaProps.load(file.openStream());

                // we will now load the default schema
                String prefix = schemaProps.getProperty("schemaDefault");
                sesProps.setProperty(ColorProperty.BACKGROUND.key(), schemaProps.getProperty(prefix + ".colorBg"));
                sesProps.setProperty(ColorProperty.RED.key(), schemaProps.getProperty(prefix + ".colorRed"));
                sesProps.setProperty(ColorProperty.TURQUOISE.key(), schemaProps.getProperty(prefix + ".colorTurq"));
                sesProps.setProperty(ColorProperty.CURSOR.key(), schemaProps.getProperty(prefix + ".colorCursor"));
                sesProps.setProperty(ColorProperty.GUI_FIELD.key(), schemaProps.getProperty(prefix + ".colorGUIField"));
                sesProps.setProperty(ColorProperty.WHITE.key(), schemaProps.getProperty(prefix + ".colorWhite"));
                sesProps.setProperty(ColorProperty.YELLOW.key(), schemaProps.getProperty(prefix + ".colorYellow"));
                sesProps.setProperty(ColorProperty.GREEN.key(), schemaProps.getProperty(prefix + ".colorGreen"));
                sesProps.setProperty(ColorProperty.PINK.key(), schemaProps.getProperty(prefix + ".colorPink"));
                sesProps.setProperty(ColorProperty.BLUE.key(), schemaProps.getProperty(prefix + ".colorBlue"));
                sesProps.setProperty(ColorProperty.SEPARATOR.key(), schemaProps.getProperty(prefix + ".colorSep"));
                sesProps.setProperty(ColorProperty.HEX_ATTR.key(), schemaProps.getProperty(prefix + ".colorHexAttr"));
                sesProps.setProperty("font", GUIGraphicsUtils.getDefaultFont());
                sesProps.setProperty("print.font", GUIGraphicsUtils.getDefaultPrinterFont());

                ConfigureFactory.getInstance().saveSettings("dfltSessionProps", getConfigurationResource(), "");
            }

        } catch (IOException ioe) {
            System.out.println("Information Message: Properties file is being " + "created for first time use:  File name "
                + getConfigurationResource());
        } catch (SecurityException se) {
            System.out.println(se.getMessage());
        }
    }

    /**
     * Overlays the current session properties with the properties of the
     * session theme.
     */
    private void overlayConfigurationWithTheme() {

        themeColorProperties = ConfigureFactory.getInstance().getProperties(getThemeConfigurationKey(themeConfigurationFile), themeConfigurationFile,
            true, THEME_CONFIGURATION_HEADER, false);
        if (themeColorProperties == null || themeColorProperties.size() == 0) {
            initializeThemeColorProperties();
        }
    }

    /**
     * Initializes the theme properties with the current session properties.
     */
    private void initializeThemeColorProperties() {

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            themeColorProperties.put(key, sesProps.getProperty(key));
        }
    }

    /**
     * Returns <code>true</code> when a session theme has been set.
     * 
     * @return true, when a theme has been set
     */
    private boolean hasSessionTheme() {

        if (sessionTheme != null && sessionTheme.trim().length() > 0 && !SessionConfig.THEME_NONE.equals(sessionTheme)) {
            return true;
        }

        return false;
    }

    private boolean isThemeProperty(String prop) {
        return sessionThemeEnabled && ColorProperty.isColorProperty(prop);
    }

    public boolean isPropertyExists(String prop) {

        if (isThemeProperty(prop)) {
            return themeColorProperties.containsKey(prop);
        } else {
            return sesProps.containsKey(prop);
        }
    }

    public String getStringProperty(String prop) {

        if (isPropertyExists(prop)) {
            if (isThemeProperty(prop)) {
                return (String)themeColorProperties.get(prop);
            } else {
                return (String)sesProps.get(prop);
            }
        }

        return "";
    }

    public void setProperty(String key, String value) {

        if (key != null && value != null && value.equals(sesProps.getProperty(key))) {
            return;
        }

        if (isThemeProperty(key)) {
            System.out.println("==> SessionConfig.setProperty(THEME): (" + key + "=" + value + ")");
            themeColorProperties.setProperty(key, value);
            setThemePropertiesDirty(true);
        } else {
            System.out.println("==> SessionConfig.setProperty(SESSION): (" + key + "=" + value + ")");
            sesProps.setProperty(key, value);
            setSessionPropertiesDirty(true);
        }
    }

    private boolean isThemePropertyDirty() {

        if (sessionThemeEnabled && themeColorProperties.containsKey(IS_DIRTY_FLAG)) {
            return true;
        }

        return false;
    }

    private void setThemePropertiesDirty(boolean dirty) {

        if (sessionThemeEnabled) {
            if (dirty) {
                System.out.println("*** Theme properties: DIRTY ***");
                themeColorProperties.setProperty(IS_DIRTY_FLAG, "yes");
            } else {
                System.out.println("### Theme properties: UNCHANGED ###");
                themeColorProperties.remove(IS_DIRTY_FLAG);
            }
        }
    }

    private boolean isSessionPropertyDirty() {

        if (sesProps.containsKey(IS_DIRTY_FLAG)) {
            return true;
        }

        return false;
    }

    private void setSessionPropertiesDirty(boolean dirty) {

        if (dirty) {
            System.out.println("*** Session properties: DIRTY ***");
            sesProps.setProperty(IS_DIRTY_FLAG, "yes");
        } else {
            System.out.println("### Session properties: UNCHANGED ###");
            sesProps.remove(IS_DIRTY_FLAG);
        }
    }

    public int getIntegerProperty(String prop) {

        checkNonThemeProperty(prop);

        return getIntegerPropertyNoChecked(prop);
    }

    private int getIntegerPropertyNoChecked(String prop) {

        int intProp = 0;

        if (isPropertyExists(prop)) {

            try {
                intProp = Integer.parseInt(getStringProperty(prop));
            } catch (NumberFormatException ne) {
                // ignore errors
            }
        }

        return intProp;
    }

    public Rectangle getRectangleProperty(String prop) {

        checkNonThemeProperty(prop);

        Rectangle rectProp = new Rectangle();

        if (isPropertyExists(prop)) {

            StringTokenizer stringtokenizer = new StringTokenizer(getStringProperty(prop), ",");
            if (stringtokenizer.hasMoreTokens()) {
                rectProp.x = Integer.parseInt(stringtokenizer.nextToken());
            }

            if (stringtokenizer.hasMoreTokens()) {
                rectProp.y = Integer.parseInt(stringtokenizer.nextToken());
            }

            if (stringtokenizer.hasMoreTokens()) {
                rectProp.width = Integer.parseInt(stringtokenizer.nextToken());
            }

            if (stringtokenizer.hasMoreTokens()) {
                rectProp.height = Integer.parseInt(stringtokenizer.nextToken());
            }
        }

        return rectProp;
    }

    public void setRectangleProperty(String prop, Rectangle rect) {

        checkNonThemeProperty(prop);

        String rectStr = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;

        setProperty(prop, rectStr);
    }

    public float getFloatProperty(String prop) {

        checkNonThemeProperty(prop);

        float floatProp = 0.0f;

        if (isPropertyExists(prop)) {
            floatProp = Float.parseFloat(getStringProperty(prop));
        }

        return floatProp;
    }

    public Color getColorProperty(String prop) {

        Color colorProp = null;

        if (isPropertyExists(prop)) {
            colorProp = new Color(getIntegerPropertyNoChecked(prop));
        }

        return colorProp;
    }

    public Object removeProperty(String prop) {

        checkNonColorProperty(prop);

        if (isThemeProperty(prop)) {
            return themeColorProperties.remove(prop);
        } else {
            return sesProps.remove(prop);
        }
    }

    private void checkNonColorProperty(String prop) {

        if (ColorProperty.isColorProperty(prop)) {
            throw new IllegalArgumentException("Color properties are not allowed: " + prop);
        }
    }

    private void checkNonThemeProperty(String prop) {

        if (ColorProperty.isColorProperty(prop)) {
            throw new IllegalArgumentException("Theme properties are not allowed: " + prop);
        }
    }

    /**
     * Add a SessionConfigListener to the listener list.
     * 
     * @param listener The SessionListener to be added
     */
    public synchronized void addSessionConfigListener(SessionConfigListener listener) {

        if (listeners == null) {
            listeners = new java.util.Vector(3);
        }
        listeners.addElement(listener);

    }

    /**
     * Remove a SessionListener from the listener list.
     * 
     * @param listener The SessionListener to be removed
     */
    public synchronized void removeSessionConfigListener(SessionConfigListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.removeElement(listener);

    }

}