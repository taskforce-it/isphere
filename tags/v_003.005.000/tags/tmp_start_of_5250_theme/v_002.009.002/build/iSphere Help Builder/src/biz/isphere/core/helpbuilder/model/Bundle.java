/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Bundle {

    private static final String SYMBOLIC_NAME = "Bundle-SymbolicName";
    private static final String SINGLETON = "singleton";

    private Map<String, String> values;

    public Bundle(String path) throws IOException {
        loadData(path);
    }

    public String getSymbolicName() {
        return values.get(SYMBOLIC_NAME);
    }

    private void loadData(String path) throws IOException {

        values = new HashMap<String, String>();

        StringBuilder line = new StringBuilder();
        String part = null;

        BufferedReader reader = new BufferedReader(new FileReader(path));
        while ((part = reader.readLine()) != null) {
            line.append(part);
            if (!part.endsWith(",")) {
                String[] lineParts = line.toString().split(":", 2);
                String key = lineParts[0];
                String value = lineParts[1];

                if (key.equals(SYMBOLIC_NAME)) {
                    addSymbolicName(key, value);
                } else {
                    addBundleEntry(key, value);
                }

                line.delete(0, line.length());
            }
        }
    }

    private void addSymbolicName(String key, String value) {

        String[] parts = value.split(";");
        if (parts.length == 0) {
            return;
        }

        addBundleEntry(SYMBOLIC_NAME, parts[0]);

        if (parts.length == 1) {
            return;
        }

        for (int i = 1; i < parts.length; i++) {
            String[] subParts = parts[i].split(":=");
            if (subParts.length == 2) {
                if (subParts[0].equals(SINGLETON)) {
                    addBundleEntry(SINGLETON, subParts[1]);
                }
            }
        }
    }

    private void addBundleEntry(String key, String value) {

        values.put(key, value.trim());
    }
}
