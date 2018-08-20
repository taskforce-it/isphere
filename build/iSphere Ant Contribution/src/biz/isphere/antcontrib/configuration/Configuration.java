/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import biz.isphere.antcontrib.logger.Logger;

import com.jacob.com.LibraryLoader;

public final class Configuration {

    public static final String JAVA_LIBRARY_PATH = "java.library.path";
    public static final String JACOB_DLL_PATH = "jacob.dll.path";

    /**
     * The instance of this Singleton class.
     */
    private static Configuration instance = null;
    private static boolean isPluginConfiguration = false;

    private boolean isConfigured;
    private String pluginAntPath;
    private boolean isTempJniLibraryWarning;
    private String tempJNILibraryPath;

    private static File sysTempDir;

    /**
     * Number of attempts to create a temporary directory.
     */
    private static final int TEMP_DIR_ATTEMPTS = 10;

    private static final String JACOB_TEMP_LIB_PATH_PREFIX = "{jacob-temp-";
    private static final String JACOB_TEMP_LIB_PATH_SUFFIX = "}";

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Configuration() {
        isConfigured = false;
        tempJNILibraryPath = null;
        isTempJniLibraryWarning = true;
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
            Logger.logInfo("Configuration created.");
        }
        return instance;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public void configureTask() throws ConfigurationException {
        configure(null);
    }

    public void configureTask(String pathName) throws ConfigurationException {
        if (pluginAntPath != null) {
            Logger.logInfo("Specified JACOB DLL path ignored. Using iSphere Ant Plug-In path instead.");
            configure(pluginAntPath);
        } else {
            configure(pathName);
        }
    }

    public void endTask() {

        if (tempJNILibraryPath != null) {
            recursiveDelete(new File(tempJNILibraryPath));
            tempJNILibraryPath = null;
        }

        if (!isPluginConfiguration) {
            dispose();
        }
    }

    public void configurePlugin(String pathName) throws ConfigurationException {
        try {
            isPluginConfiguration = true;
            Logger.logInfo("Configuring plug-in configuration.");

            pluginAntPath = pathName;
            configure(pathName);
        } catch (Exception e) {
            // Actually we catch an AntSecurityException, which is thrown,
            // when an Ant scripted that references iSphere-ant-standalone.jar
            // is opened in the editor before the plug-in has been started.
            isConfigured = false;
            return;
        }
    }

    public void dispose() {
        instance = null;
        Logger.logInfo("Configuration disposed.");
    }

    private void configure(String pathName) throws ConfigurationException {

        if (pathName == null) {
            Logger.logInfo("Trying to find JACOB JNI library files ... ");
            if (canUseJacobDllPath()) {
                // add the dll name in case we got a director
                setJacobDllPath(getJacobDllPath());
                Logger.logInfo("Configured JACOB using " + JACOB_DLL_PATH + ": " + getJacobDllPath());
            } else if (canUseJavaLibraryPath()) {
                // just fine
                Logger.logInfo("Configured JACOB using " + JAVA_LIBRARY_PATH + ": " + getJavaLibraryPath());
            } else if (canUseLibraryPath(pluginAntPath)) {
                setJacobDllPath(pluginAntPath);
                Logger.logInfo("Configured JACOB using the iSphere Ant Plug-In path: " + getJacobDllPath());
            } else {
                configureWithTempDir();
            }
        } else {
            Logger.logInfo("Trying to use JNI library files of directory: " + pathName);
            if (canUseLibraryPath(pathName)) {
                setJacobDllPath(pathName);
            } else {
                throw new ConfigurationException("Failed to configure JACOB. Could not find JNI library.");
            }
        }

        isConfigured = true;
    }

    private void configureWithTempDir() throws ConfigurationException {

        try {

            String tempDir = createTempDir();
            initializeTempJacobLibrary(tempDir);
            setJacobDllPath(tempDir);
            tempJNILibraryPath = tempDir;

            if (isTempJniLibraryWarning) {
                Logger.logWarning("JACOB has been configured using a temporary path, which leads to performance loss."
                    + " You may consider to unpack the JACOB jar and dll files to a local directory and" + " set the environment variable "
                    + JACOB_DLL_PATH + ".");
                isTempJniLibraryWarning = false;
            }

        } catch (IOException e) {
            throw new ConfigurationException("Failed to configure JACOB. Could not create temporary JNI library.", e);
        }
    }

    private void setJacobDllPath(String pathName) {

        File dllPath = new File(pathName);
        if (dllPath.isDirectory()) {
            dllPath = new File(dllPath, getPreferredDLLName());
        }

        System.setProperty(JACOB_DLL_PATH, dllPath.getAbsolutePath());

        Logger.logInfo("JACOB DLL path set to: " + getJacobDllPath());
    }

    private boolean canUseJacobDllPath() {

        String pathName = getJacobDllPath();
        if (pathName == null) {
            return false;
        }

        return canUseLibraryPath(pathName);
    }

    private String getJacobDllPath() {
        return System.getProperty(JACOB_DLL_PATH);
    }

    private boolean canUseJavaLibraryPath() {

        String javaLibraryPath = getJavaLibraryPath();
        if (javaLibraryPath == null) {
            return false;
        }

        if (canUseLibraryPath(javaLibraryPath)) {
            return true;
        }

        return false;
    }

    private String getJavaLibraryPath() {

        String[] pathNames = System.getProperty(JAVA_LIBRARY_PATH).split(File.pathSeparator);
        for (String pathName : pathNames) {
            if (canUseLibraryPath(pathName)) {
                return pathName;
            }
        }

        return null;
    }

    private String getPreferredDLLName() {
        return LibraryLoader.getPreferredDLLName() + ".dll";
    }

    private boolean canUseLibraryPath(String pathName) {

        if (pathName == null || pathName.length() == 0) {
            return false;
        }

        File path = new File(pathName);
        if (path.isFile() && !path.exists()) {
            return false;
        }

        String dllName = getPreferredDLLName();
        File dllFile = new File(path + File.separator + dllName);
        if (dllFile.exists() && dllFile.isFile()) {
            return true;
        }

        return false;
    }

    private String initializeTempJacobLibrary(String defaultJacobLibPathName) throws IOException {

        String defaultJacobLibPath = new File(defaultJacobLibPathName).getAbsolutePath();
        extractFile("/lib", "jacob.jar", defaultJacobLibPath);
        extractFile("/lib", getPreferredDLLName(), defaultJacobLibPath);

        return defaultJacobLibPath;
    }

    private void extractFile(String path, String name, String toPath) throws IOException {

        InputStream in = null;
        OutputStream out = null;

        try {

            File fileOut = new File(toPath + File.separator + name);

            if (!"/".equals(File.separator)) {
                path = path.replaceAll("\\" + File.separator, "/");
            }

            if (fileOut.isFile() && fileOut.exists()) {
                return;
            }

            String resource = path + "/" + name;
            in = Configuration.class.getResourceAsStream(resource);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resource);
            }

            out = new FileOutputStream(fileOut);

            Logger.logInfo("Extracting from: " + resource);
            Logger.logInfo("Extracting to  : " + fileOut);

            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }

            Logger.logInfo("*** Extracted : " + name);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a new temporary directory. Use something like
     * {@link #recursiveDelete(File)} to clean this directory up since it isn't
     * deleted automatically
     * 
     * @return the new directory
     * @throws IOException if there is an error creating the temporary directory
     */
    public static String createTempDir() throws IOException {
        sysTempDir = new File(System.getProperty("java.io.tmpdir"));
        File newTempDir;
        int attemptCount = 0;
        do {
            attemptCount++;
            if (attemptCount > TEMP_DIR_ATTEMPTS) {
                throw new IOException("The highly improbable has occurred! Failed to " + "create a unique temporary directory after "
                    + TEMP_DIR_ATTEMPTS + " attempts.");
            }
            String dirName = UUID.randomUUID().toString().toUpperCase();
            newTempDir = new File(sysTempDir, JACOB_TEMP_LIB_PATH_PREFIX + dirName + JACOB_TEMP_LIB_PATH_SUFFIX);
        } while (newTempDir.exists());

        if (newTempDir.mkdirs()) {
            Logger.logInfo("JACOB: temporary library path created.");
            return newTempDir.getPath();
        } else {
            throw new IOException("Failed to create temp dir named " + newTempDir.getAbsolutePath());
        }
    }

    /**
     * Recursively delete file or directory
     * 
     * @param fileOrDir - the file or directory to delete
     * @return <code>true</code> if all files are successfully deleted, else
     *         <code>false</code>.
     */
    private boolean recursiveDelete(File fileOrDir) {

        if (fileOrDir.isDirectory()) {
            for (File innerFile : fileOrDir.listFiles()) {
                if (innerFile.isDirectory()) {
                    recursiveDelete(innerFile);
                } else if (innerFile.isFile()) {
                    innerFile.delete();
                }
            }
        }

        Logger.logInfo("JACOB: temporary library path delete.");

        return fileOrDir.delete();
    }
}