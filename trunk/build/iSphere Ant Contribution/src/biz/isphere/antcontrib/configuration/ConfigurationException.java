/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.configuration;

public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 2440520784825745346L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable e) {
        super(message, e);
    }
}
