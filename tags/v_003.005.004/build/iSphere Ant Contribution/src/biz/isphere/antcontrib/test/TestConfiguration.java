/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.test;

import biz.isphere.antcontrib.configuration.Configuration;
import biz.isphere.antcontrib.configuration.ConfigurationException;
import biz.isphere.antcontrib.winword.WdApplication;

public class TestConfiguration {

    public static void main(String[] args) {
        TestConfiguration main = new TestConfiguration();
        main.run(args);
    }

    private void run(String[] args) {

        WdApplication winword = null;

        try {
            Configuration.getInstance().configureTask();
            winword = new WdApplication(true);
            winword.getDocuments().open("C:\\workspaces\\rdp_080\\workspace\\iSphere Ant Plugin\\Test.doc");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        } finally {
            if (winword != null) {
                winword.quit();
            }
        }

    }

}