/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.taskdef;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import biz.isphere.antcontrib.configuration.Configuration;
import biz.isphere.antcontrib.configuration.ConfigurationException;
import biz.isphere.antcontrib.logger.Logger;

public abstract class BasicTask extends Task {

    private Configuration config;
    private String jacobDllPath;

    public BasicTask() {

        config = Configuration.getInstance();
    }

    public void setJacobDllPath(String path) {
        jacobDllPath = path;
    }

    public String getJacobDllPath() {
        return jacobDllPath;
    }

    public void execute() {

        try {

            initialize();
            executeTask();

        } catch (ConfigurationException e) {
            Logger.logError("Failed to execute task: " + getName(), e);
            throw new BuildException("Failed to execute task.", e);
        } catch (BuildException e) {
            Logger.logError("Failed to execute task: " + getName(), e);
            throw e;
        } catch (Throwable e) {
            Logger.logError("Failed to execute task: " + getName(), e);
            throw new BuildException(e);
        } finally {
            terminate();
        }
    }

    protected void initializeTask() {
        return;
    }

    protected abstract void executeTask();

    protected void terminateTask() {
        return;
    }

    private void initialize() throws ConfigurationException {
        if (isNullOrEmpty(jacobDllPath)) {
            config.configureTask();
        } else {
            config.configureTask(jacobDllPath);
        }

        try {
            initializeTask();
        } catch (Throwable e) {
            Logger.logError("Failed to initialize task: " + getName(), e);
        }
    }

    private void terminate() {

        try {
            terminateTask();
        } catch (Throwable e) {
            Logger.logError("Failed to terminate task: " + getName(), e);
        }

        config.endTask();
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }

    protected boolean isNullOrEmpty(Object object) {

        if (object == null) {
            return true;
        }

        if (object instanceof String) {
            if (((String)object).trim().length() == 0) {
                return true;
            }
        }

        return false;
    }
}
