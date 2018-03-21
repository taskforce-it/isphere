/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.tn5250j.TN5250jConstants;

import biz.isphere.tn5250j.core.preferences.Preferences;

/**
 * iSphere TN5250J session configuration.
 */
public class Session {

    private String sessionDirectory;
    private String connection;
    private String name;
    private String device;
    private String port;
    private String codePage;
    private String screenSize;
    // private String enhancedMode;
    private String proxyHost;
    private String proxyPort;
    private String area;
    private String user;
    private String password;
    private String program;
    private String library;
    private String menu;
    private String sslType; // Added with 3.0.0beta9

    public Session(String sessionDirectory) {
        this.sessionDirectory = sessionDirectory;
        connection = "";
        name = "";
        device = "";
        port = "";
        codePage = "";
        screenSize = "";
        // enhancedMode = "";
        proxyHost = "";
        proxyPort = "";
        area = "";
        user = "";
        password = "";
        program = "";
        library = "";
        menu = "";
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCodePage() {
        return codePage;
    }

    public void setCodePage(String codePage) {
        this.codePage = codePage;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    /*
     * public String getEnhancedMode() { return enhancedMode; } public void
     * setEnhancedMode(String enhancedMode) { this.enhancedMode = enhancedMode;
     * }
     */
    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getSSLType() {
        if (sslType == null) {
            return TN5250jConstants.SSL_TYPE_NONE;
        }
        return sslType;
    }

    public void setSSLType(String sslType) {
        this.sslType = sslType;
    }

    public static boolean exists(String sessionDirectory, String connection, String name) {

        File file = getSessionSettingsFile(sessionDirectory, name);

        if (!file.exists()) {
            return false;
        }

        if (!file.isFile()) {
            return false;
        }

        return true;
    }

    public static Session load(String sessionDirectory, String connection, String name) {
        return Session.load(sessionDirectory, connection, name, null);
    }

    public static Session load(String sessionDirectory, String connection, String name, Session session) {
        try {
            Properties properties = new Properties();
            FileInputStream fis = new FileInputStream(getSessionSettingsFile(sessionDirectory, name));
            properties.load(fis);
            fis.close();
            if (session == null) {
                session = new Session(sessionDirectory);
            }
            session.setConnection(connection);
            session.setName(name);
            setSessionProperties(properties, session);
            return session;
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static void setSessionProperties(Properties properties, Session session) {
        Preferences preferences = Preferences.getInstance();
        if (properties.getProperty("Device") != null) {
            session.setDevice(properties.getProperty("Device"));
        }
        if (properties.getProperty("Port") != null) {
            session.setPort(properties.getProperty("Port"));
        }
        if (properties.getProperty("CodePage") != null) {
            session.setCodePage(properties.getProperty("CodePage"));
        }
        if (properties.getProperty("SSLType") != null) {
            session.setSSLType(properties.getProperty("SSLType"));
        }
        if (properties.getProperty("ScreenSize") != null) {
            session.setScreenSize(properties.getProperty("ScreenSize"));
        }
        // if (properties.getProperty("EnhancedMode") != null) {
        // session.setEnhancedMode(properties.getProperty("EnhancedMode"));
        // }
        if (properties.getProperty("ProxyHost") != null) {
            session.setProxyHost(properties.getProperty("ProxyHost"));
        }
        if (properties.getProperty("ProxyPort") != null) {
            session.setProxyPort(properties.getProperty("ProxyPort"));
        }
        if (properties.getProperty("Area") != null) {
            session.setArea(properties.getProperty("Area"));
        } else {
            session.setArea(preferences.getSessionArea());
        }
        if (properties.getProperty("User") != null) {
            session.setUser(properties.getProperty("User"));
        }
        if (properties.getProperty("Password") != null) {
            session.setPassword(properties.getProperty("Password"));
        }
        if (properties.getProperty("Program") != null) {
            session.setProgram(properties.getProperty("Program"));
        }
        if (properties.getProperty("Library") != null) {
            session.setLibrary(properties.getProperty("Library"));
        }
        if (properties.getProperty("Menu") != null) {
            session.setMenu(properties.getProperty("Menu"));
        }
    }

    public boolean create() {
        if (store()) {
            return true;
        }
        return false;
    }

    public boolean update() {
        if (store()) {
            return true;
        }
        return false;
    }

    private boolean store() {

        boolean ok = false;

        Properties properties = new Properties();
        properties.put("Device", device);
        properties.put("Port", port);
        properties.put("CodePage", codePage);
        properties.put("ScreenSize", screenSize);
        // properties.put("EnhancedMode", enhancedMode);
        properties.put("ProxyHost", proxyHost);
        properties.put("ProxyPort", proxyPort);
        properties.put("Area", area);
        properties.put("User", user);
        properties.put("Password", password);
        properties.put("Program", program);
        properties.put("Library", library);
        properties.put("Menu", menu);
        properties.put("SSLType", getSSLType());

        try {
            FileOutputStream fos = new FileOutputStream(getSessionSettingsFile(sessionDirectory, name));
            properties.store(fos, name);
            fos.close();
            ok = true;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return ok;
    }

    public boolean delete() {
        boolean ok = false;
        if (getSessionSettingsFile(sessionDirectory, name).delete()) {
            ok = true;
        }
        return ok;
    }

    private static File getSessionSettingsFile(String sessionDirectory, String name) {

        return new File(sessionDirectory + File.separator + name);
    }
}
