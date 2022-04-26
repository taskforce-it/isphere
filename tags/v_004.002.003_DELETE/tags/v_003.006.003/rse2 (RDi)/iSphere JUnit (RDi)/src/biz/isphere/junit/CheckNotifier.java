/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import junit.framework.JUnit4TestAdapter;

import org.eclipse.osgi.util.ManifestElement;
import org.junit.Test;
import org.osgi.framework.BundleException;

import biz.isphere.core.internal.Version;

/**
 * <b>JUnit 4 Test Case</b>
 * <p>
 * Verifies that the properties of the "Notifier" manifest file are valid.
 * 
 * @author Thomas Raddatz
 */
public class CheckNotifier {

    private String workspace;

    public CheckNotifier() {

        workspace = getWorkspaceLocation();
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CheckNotifier.class);
    }

    /**
     * Verifies the property of the release section.
     * 
     * @throws Exception
     */
    @Test
    public void testReleaseProperties() throws Exception {

        String path = workspace + "/iSphere Notifier/MANIFEST.MF";
        URL url = new URL("file:" + path);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        Manifest manifest = new Manifest(is);
        is.close();

        String xReleaseVersion = getString(manifest, "Bundle-Version");
        new Version(xReleaseVersion);

        String xReleaseUpdateLibrary = getString(manifest, "X-Bundle-Update-Library");
        assertTrue("true".equals(xReleaseUpdateLibrary) || "false".equals(xReleaseUpdateLibrary));

        String xReleaseInfo = getString(manifest, "X-Bundle-Info", true);
        assertTrue(xReleaseInfo == null || xReleaseInfo.length() > 0);

        System.out.println();
        System.out.println("Release version:        " + xReleaseVersion);
        System.out.println("Release info:           " + xReleaseInfo);
        System.out.println("Release update library: " + xReleaseUpdateLibrary);

        String xBetaVersion = getString(manifest, "X-Beta-Version");
        new Version(xBetaVersion);

        String xBetaUpdateLibrary = getString(manifest, "X-Beta-Update-Library");
        assertTrue("true".equals(xBetaUpdateLibrary) || "false".equals(xBetaUpdateLibrary));

        String xBetaInfo = getString(manifest, "X-Beta-Info", true);
        assertTrue(xBetaInfo == null || xBetaInfo.length() > 0);

        System.out.println();
        System.out.println("Beta version:           " + xReleaseVersion);
        System.out.println("Beta info:              " + xReleaseInfo);
        System.out.println("Beta update library:    " + xReleaseUpdateLibrary);

        System.out.println("** Finished testing release properties **");
    }

    private String getWorkspaceLocation() {

        // Get classes directory ('/bin')
        String fullPath = getClass().getClassLoader().getResource(".").getPath().replaceAll("%20", " ");

        if (fullPath.endsWith("/")) {

            // Remove trailing slash
            fullPath = fullPath.substring(0, fullPath.length() - 1);

            // Remove trailing '/bin' and project name from path
            for (int c = 1; c <= 2; c++) {
                int i = fullPath.lastIndexOf("/");
                if (i != -1) {
                    fullPath = fullPath.substring(0, i);
                }
            }
        }

        return fullPath;
    }

    /**
     * Verifies the property of the beta release section.
     * 
     * @throws Exception
     */
    @Test
    public void testBetaProperties() throws Exception {

        String path = workspace + "/iSphere Notifier/MANIFEST.MF";
        URL url = new URL("file:" + path);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        Manifest manifest = new Manifest(is);
        is.close();

        String xBetaVersion = getString(manifest, "X-Beta-Version");
        if (xBetaVersion != null) {
            new Version(xBetaVersion);
        }

        String xBetaUpdateLibrary = getString(manifest, "X-Beta-Update-Library");
        if (xBetaUpdateLibrary != null) {
            assertTrue("true".equals(xBetaUpdateLibrary) || "false".equals(xBetaUpdateLibrary));
        }

        String xBetaInfo = getString(manifest, "X-Beta-Info", true);
        assertTrue(xBetaInfo == null || xBetaInfo.length() > 0);

        System.out.println();
        System.out.println("Beta version:        " + xBetaVersion);
        System.out.println("Beta info:           " + xBetaInfo);
        System.out.println("Beta update library: " + xBetaUpdateLibrary);

        System.out.println("** Finished testing beta release properties **");
    }

    private String getString(Manifest manifest, String version, boolean replaceControlCharacter) throws BundleException {

        String value = getString(manifest, version);
        if (value == null) {
            return null;
        }

        value = value.replaceAll(";", ", ").replaceAll("\\\\n", "\n");

        return value;
    }

    private String getString(Manifest manifest, String version) throws BundleException {

        String[] propertyValues = getPropertyValues(manifest, version);
        if (propertyValues != null && propertyValues.length == 1) {
            return propertyValues[0];
        }

        return null;
    }

    private String[] getPropertyValues(Manifest manifest, String property) throws BundleException {
        String[] propertyValues = null;
        Properties prop = _manifestToProperties(manifest.getMainAttributes());
        String requires = prop.getProperty(property);
        if (requires != null) {
            ManifestElement elements[] = ManifestElement.parseHeader(property, requires);
            propertyValues = new String[elements.length];
            for (int idx = 0; idx < elements.length; idx++) {
                propertyValues[idx] = elements[idx].getValue();
            }
        }
        return propertyValues;
    }

    private Properties _manifestToProperties(Attributes d) {
        Iterator<?> iter = d.keySet().iterator();
        Properties result = new Properties();
        Attributes.Name key;
        for (; iter.hasNext(); result.put(key.toString(), d.get(key)))
            key = (Attributes.Name)iter.next();

        return result;
    }
}
