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
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.tn5250j.event.SessionConfigEvent;
import org.tn5250j.event.SessionConfigListener;
import org.tn5250j.interfaces.ConfigureFactory;
import org.tn5250j.settings.ColorProperty;
import org.tn5250j.tools.GUIGraphicsUtils;
import org.tn5250j.tools.LangTool;

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
    private static final String THEME_CONFIGURATION_FILE_PREFIX = "ThemeOverlay_";
    private static final String THEME_CONFIGURATION_FILE_SUFFIX = ".props";
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

    private Properties savedColorProperties;

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
            themeConfigurationFile = THEME_CONFIGURATION_FILE_PREFIX + sessionTheme + THEME_CONFIGURATION_FILE_SUFFIX;
            setSessionThemeEnabled(true);
        } else {
            setSessionThemeEnabled(false);
        }

        loadConfigurationResource();
    }

    public static String[] loadThemes() {

        return ConfigureFactory.getInstance().loadThemeNames(THEME_CONFIGURATION_FILE_PREFIX, THEME_CONFIGURATION_FILE_SUFFIX);
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

    public Properties getProperties() {

        return sesProps;
    }

    public void setSessionProps(Properties props) {

        sesProps.putAll(props);

    }

    public boolean isModified() {

        return sesProps.containsKey(IS_DIRTY_FLAG);
    }

    public void setModified() {

        System.out.println("Setting modified flag...");

        sesProps.setProperty(IS_DIRTY_FLAG, "yes");
    }

    public void resetModified() {

        System.out.println("Reseting modified flag...");

        sesProps.remove(IS_DIRTY_FLAG);
    }

    // public void saveSessionProps(java.awt.Container parent) {
    //
    // if (sesProps.containsKey(IS_DIRTY_FLAG)) {
    //
    // resetModified();
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // public void run() {
    // Object[] args = { getConfigurationResource() };
    // String message =
    // MessageFormat.format(LangTool.getString("messages.saveSettings"), args);
    //
    // int result = JOptionPane.showConfirmDialog(null /* parent */, message);
    //
    // if (result == JOptionPane.OK_OPTION) {
    // saveSessionProps();
    // }
    // }
    // });
    //
    // }
    //
    // }

    public void saveSessionProps() {

        resetModified();

        if (sessionThemeEnabled) {
            saveThemeProps();
            restoreDefaultColorProperties();
        }

        if (usingDefaults) {

            System.out.println("Saving session properties...");

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

        if (sessionThemeEnabled) {
            overlayConfigurationWithTheme();
        }
    }

    /**
     * Saves the properties of the session theme.
     */
    private void saveThemeProps() {

        System.out.println("Saving theme properties to file: " + themeConfigurationFile);

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            themeColorProperties.put(key, sesProps.getProperty(key));
        }

        ConfigureFactory.getInstance().saveSettings(getThemeConfigurationKey(), themeConfigurationFile, THEME_CONFIGURATION_HEADER);
    }

    private String getThemeConfigurationKey() {
        return THEME_CONFIGURATION_KEY + "_" + themeConfigurationFile;
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
            preserveDefaultColorProperties();
            overlayConfigurationWithTheme();
        }
    }

    private String settingsDirectory() {
        return ConfigureFactory.getInstance().getProperty("emulator.settingsDirectory");
    }

    private void loadDefaults() {

        try {

            System.out.println("Loading default session properties...");

            sesProps = ConfigureFactory.getInstance().getProperties("dfltSessionProps", getConfigurationResource(), true, "Default Settings");
            if (sesProps.size() == 0) {

                System.out.println("Initializing default session properties...");

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

        System.out.println("Loading theme properties from file: " + themeConfigurationFile);

        sesProps = cloneProperties(sesProps);

        themeColorProperties = ConfigureFactory.getInstance().getProperties(getThemeConfigurationKey(), themeConfigurationFile, true,
            THEME_CONFIGURATION_HEADER, false);
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

        System.out.println("Initializing theme properties...");

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

    /**
     * Saves the session properties that are also part of the theme properties.
     */
    private void preserveDefaultColorProperties() {

        System.out.println("Saving default color properties...");

        savedColorProperties = new Properties();

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            savedColorProperties.put(key, sesProps.getProperty(key));
        }
    }

    /**
     * Restores the session properties that had been saved before.
     */
    private void restoreDefaultColorProperties() {

        System.out.println("Restoring session properties...");

        String[] keys = ColorProperty.keys();
        for (String key : keys) {
            sesProps.put(key, savedColorProperties.getProperty(key));
        }
    }

    private void setColorProperty(Properties colorProperties, String key) {

        if (hasProperty(colorProperties, key)) {
            sesProps.setProperty(key, colorProperties.getProperty(key));
        }
    }

    private boolean hasProperty(Properties colorProperties, String key) {
        return colorProperties.containsKey(key);
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

    public Object setProperty(String key, String value) {
        return sesProps.setProperty(key, value);
    }

    public Object removeProperty(String key) {
        return sesProps.remove(key);
    }

    // public synchronized Vector getSessionConfigListeners() {
    //
    // return listeners;
    // }

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