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
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private Properties dfltSessionProps;
    private boolean usingDefaults;

    private List<SessionConfigListener> sessionCfglisteners = null;
    private final ReadWriteLock sessionCfglistenersLock = new ReentrantReadWriteLock();

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

    public final void firePropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {

        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }

        System.out.println("SessionConfig.firePropertyChange(): " + source + " (" + propertyName + ")");

        sessionCfglistenersLock.readLock().lock();
        try {
            if (this.sessionCfglisteners != null) {
                final SessionConfigEvent sce = new SessionConfigEvent(source, propertyName, oldValue, newValue, sessionTheme);
                for (SessionConfigListener target : this.sessionCfglisteners) {
                    target.onConfigChanged(sce);
                }
            }
        } finally {
            sessionCfglistenersLock.readLock().unlock();
        }

    }

    public boolean isModified() {

        return sesProps.containsKey(IS_DIRTY_FLAG);
    }

    public void setModified() {

        System.out.println("*** Session config changed ***");

        sesProps.setProperty(IS_DIRTY_FLAG, "yes");
    }

    public void resetModified() {

        System.out.println("### UNCHANGED ###");

        sesProps.remove(IS_DIRTY_FLAG);
    }

    public void saveSessionProps() {

        System.out.println("==> SessionConfig.saveSessionProps()");

        resetModified();

        if (sessionThemeEnabled) {
            saveThemeProps();
        }

        updateDfltSessionProperties();

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

    /**
     * Updates the properties that were actually loaded with the current values
     * of the cloned session properties.
     * <p>
     * Ignores color properties when the session has a theme, because in that
     * case these properties go into the theme configuration file.
     */
    private void updateDfltSessionProperties() {

        Set<Entry<Object, Object>> sessionProperties = sesProps.entrySet();
        for (Entry<Object, Object> entry : sessionProperties) {
            if (sessionThemeEnabled && ColorProperty.isColorProperty((String)entry.getKey())) {
                // ignore color properties, because these properties go into the
                // theme configuration file and must not change the default
                // configuration.
            } else {
                dfltSessionProps.put(entry.getKey(), entry.getValue());
            }
        }

    }

    /**
     * Saves the properties of the session theme.
     */
    private void saveThemeProps() {

        saveThemeProps(themeConfigurationFile);
    }

    /**
     * Saves the properties of the session theme.
     */
    public void saveThemeProps(String fileName) {

        Properties tempColorProperties = ConfigureFactory.getInstance().getProperties(getThemeConfigurationKey(fileName), fileName, true,
            THEME_CONFIGURATION_HEADER, false);

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            tempColorProperties.put(key, sesProps.getProperty(key));
        }

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

            dfltSessionProps = ConfigureFactory.getInstance().getProperties("dfltSessionProps", getConfigurationResource(), true, "Default Settings");
            if (dfltSessionProps.size() == 0) {

                ClassLoader cl = this.getClass().getClassLoader();
                if (cl == null) {
                    cl = ClassLoader.getSystemClassLoader();
                }

                // emul defaults
                java.net.URL file = cl.getResource(getConfigurationResource());
                Properties emuldefaults = new Properties();
                emuldefaults.load(file.openStream());
                dfltSessionProps.putAll(emuldefaults);

                // color schema defaults
                file = cl.getResource("tn5250jSchemas.properties");
                Properties schemaProps = new Properties();
                schemaProps.load(file.openStream());

                // we will now load the default schema
                String prefix = schemaProps.getProperty("schemaDefault");
                dfltSessionProps.setProperty(ColorProperty.BACKGROUND.key(), schemaProps.getProperty(prefix + ".colorBg"));
                dfltSessionProps.setProperty(ColorProperty.RED.key(), schemaProps.getProperty(prefix + ".colorRed"));
                dfltSessionProps.setProperty(ColorProperty.TURQUOISE.key(), schemaProps.getProperty(prefix + ".colorTurq"));
                dfltSessionProps.setProperty(ColorProperty.CURSOR.key(), schemaProps.getProperty(prefix + ".colorCursor"));
                dfltSessionProps.setProperty(ColorProperty.GUI_FIELD.key(), schemaProps.getProperty(prefix + ".colorGUIField"));
                dfltSessionProps.setProperty(ColorProperty.WHITE.key(), schemaProps.getProperty(prefix + ".colorWhite"));
                dfltSessionProps.setProperty(ColorProperty.YELLOW.key(), schemaProps.getProperty(prefix + ".colorYellow"));
                dfltSessionProps.setProperty(ColorProperty.GREEN.key(), schemaProps.getProperty(prefix + ".colorGreen"));
                dfltSessionProps.setProperty(ColorProperty.PINK.key(), schemaProps.getProperty(prefix + ".colorPink"));
                dfltSessionProps.setProperty(ColorProperty.BLUE.key(), schemaProps.getProperty(prefix + ".colorBlue"));
                dfltSessionProps.setProperty(ColorProperty.SEPARATOR.key(), schemaProps.getProperty(prefix + ".colorSep"));
                dfltSessionProps.setProperty(ColorProperty.HEX_ATTR.key(), schemaProps.getProperty(prefix + ".colorHexAttr"));
                dfltSessionProps.setProperty("font", GUIGraphicsUtils.getDefaultFont());
                dfltSessionProps.setProperty("print.font", GUIGraphicsUtils.getDefaultPrinterFont());

                ConfigureFactory.getInstance().saveSettings("dfltSessionProps", getConfigurationResource(), "");
            }

            // Clone properties, so that each session has its own copy that can
            // safely be overlaid with a theme.
            sesProps = cloneProperties(dfltSessionProps);

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

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            setColorProperty(themeColorProperties, key);
        }

        return;
    }

    private Properties cloneProperties(Properties properties) {

        Properties newProperties = new Properties();

        Set<Entry<Object, Object>> enties = properties.entrySet();
        for (Entry<Object, Object> entry : enties) {
            newProperties.put(entry.getKey(), entry.getValue());
        }

        return newProperties;
    }

    /**
     * Initializes the theme properties with the current session properties.
     */
    private void initializeThemeColorProperties() {

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            themeColorProperties.put(key, dfltSessionProps.getProperty(key));
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

    private void setColorProperty(Properties colorProperties, String key) {

        if (colorProperties.containsKey(key)) {
            sesProps.setProperty(key, colorProperties.getProperty(key));
        }
    }

    public boolean isPropertyExists(String prop) {
        return sesProps.containsKey(prop);
    }

    public String getStringProperty(String prop) {

        if (sesProps.containsKey(prop)) {
            return (String)sesProps.get(prop);
        }
        return "";

    }

    public int getIntegerProperty(String prop) {

        if (sesProps.containsKey(prop)) {
            try {
                int i = Integer.parseInt((String)sesProps.get(prop));
                return i;
            } catch (NumberFormatException ne) {
                return 0;
            }
        }
        return 0;

    }

    public Color getColorProperty(String prop) {

        if (sesProps.containsKey(prop)) {
            Color c = new Color(getIntegerProperty(prop));
            return c;
        }
        return null;

    }

    public Rectangle getRectangleProperty(String key) {

        Rectangle rectProp = new Rectangle();

        if (sesProps.containsKey(key)) {
            String rect = sesProps.getProperty(key);
            StringTokenizer stringtokenizer = new StringTokenizer(rect, ",");
            if (stringtokenizer.hasMoreTokens()) rectProp.x = Integer.parseInt(stringtokenizer.nextToken());
            if (stringtokenizer.hasMoreTokens()) rectProp.y = Integer.parseInt(stringtokenizer.nextToken());
            if (stringtokenizer.hasMoreTokens()) rectProp.width = Integer.parseInt(stringtokenizer.nextToken());
            if (stringtokenizer.hasMoreTokens()) rectProp.height = Integer.parseInt(stringtokenizer.nextToken());

        }

        return rectProp;

    }

    public void setRectangleProperty(String key, Rectangle rect) {

        String rectStr = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
        sesProps.setProperty(key, rectStr);
    }

    public float getFloatProperty(String prop) {

        if (sesProps.containsKey(prop)) {
            float f = Float.parseFloat((String)sesProps.get(prop));
            return f;
        }
        return 0.0f;

    }

    public boolean hasProperty(String key) {
        return sesProps.containsKey(key);
    }

    public Object setProperty(String key, String value) {

        if (key != null && value != null && value.equals(sesProps.getProperty(key))) {
            return null;
        }

        System.out.println("==> SessionConfig.setProperty(): (" + key + "=" + value + ")");

        setModified();

        return sesProps.setProperty(key, value);
    }

    public Object removeProperty(String key) {

        setModified();

        return sesProps.remove(key);
    }

    /**
     * Add a SessionConfigListener to the listener list.
     * 
     * @param listener The SessionListener to be added
     */
    public final void addSessionConfigListener(SessionConfigListener listener) {
        sessionCfglistenersLock.writeLock().lock();
        try {
            if (sessionCfglisteners == null) {
                sessionCfglisteners = new ArrayList<SessionConfigListener>(3);
            }
            sessionCfglisteners.add(listener);
        } finally {
            sessionCfglistenersLock.writeLock().unlock();
        }
    }

    /**
     * Remove a SessionListener from the listener list.
     * 
     * @param listener The SessionListener to be removed
     */
    public final void removeSessionConfigListener(SessionConfigListener listener) {
        sessionCfglistenersLock.writeLock().lock();
        try {
            if (sessionCfglisteners != null) {
                sessionCfglisteners.remove(listener);
            }
        } finally {
            sessionCfglistenersLock.writeLock().unlock();
        }
    }

}