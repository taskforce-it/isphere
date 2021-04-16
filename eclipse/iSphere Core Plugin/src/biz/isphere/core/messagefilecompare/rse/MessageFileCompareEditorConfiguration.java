/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.messagefilecompare.rse;

import biz.isphere.core.externalapi.IMessageFileCompareEditorConfiguration;

public class MessageFileCompareEditorConfiguration implements IMessageFileCompareEditorConfiguration {

    private boolean isLeftEditorEnabled;
    private boolean isLeftSelectMessageFileEnabled;
    private boolean isRightEditorEnabled;
    private boolean isRightSelectMessageFileEnabled;

    public static MessageFileCompareEditorConfiguration getDefaultConfiguration() {
        MessageFileCompareEditorConfiguration config = new MessageFileCompareEditorConfiguration();
        config.setLeftEditorEnabled(true);
        config.setLeftSelectMessageFileEnabled(true);
        config.setRightEditorEnabled(true);
        config.setRightSelectMessageFileEnabled(true);
        return config;
    }

    public static MessageFileCompareEditorConfiguration getLeftEnabledConfiguration() {
        MessageFileCompareEditorConfiguration config = new MessageFileCompareEditorConfiguration();
        config.setLeftEditorEnabled(true);
        config.setLeftSelectMessageFileEnabled(true);
        config.setRightEditorEnabled(false);
        config.setRightSelectMessageFileEnabled(false);
        return config;
    }

    public static MessageFileCompareEditorConfiguration getRightEnabledConfiguration() {
        MessageFileCompareEditorConfiguration config = new MessageFileCompareEditorConfiguration();
        config.setLeftEditorEnabled(false);
        config.setLeftSelectMessageFileEnabled(false);
        config.setRightEditorEnabled(true);
        config.setRightSelectMessageFileEnabled(true);
        return config;
    }

    public boolean isLeftEditorEnabled() {
        return isLeftEditorEnabled;
    }

    public void setLeftEditorEnabled(boolean enabled) {
        this.isLeftEditorEnabled = enabled;
    }

    public boolean isLeftSelectMessageFileEnabled() {
        return isLeftSelectMessageFileEnabled;
    }

    public void setLeftSelectMessageFileEnabled(boolean enabled) {
        this.isLeftSelectMessageFileEnabled = enabled;
    }

    public boolean isRightEditorEnabled() {
        return isRightEditorEnabled;
    }

    public void setRightEditorEnabled(boolean enabled) {
        this.isRightEditorEnabled = enabled;
    }

    public boolean isRightSelectMessageFileEnabled() {
        return isRightSelectMessageFileEnabled;
    }

    public void setRightSelectMessageFileEnabled(boolean enabled) {
        this.isRightSelectMessageFileEnabled = enabled;
    }
}
