/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization.rse;

import biz.isphere.core.externalapi.ISynchronizeMembersEditorConfiguration;

public class SynchronizeMembersEditorConfiguration implements ISynchronizeMembersEditorConfiguration {

    private boolean isLeftEditorEnabled;
    private boolean isLeftSelectObjectEnabled;
    private boolean isRightEditorEnabled;
    private boolean isRightSelectObjectEnabled;

    public static SynchronizeMembersEditorConfiguration getDefaultConfiguration() {
        SynchronizeMembersEditorConfiguration config = new SynchronizeMembersEditorConfiguration();
        config.setLeftEditorEnabled(true);
        config.setLeftSelectObjectEnabled(true);
        config.setRightEditorEnabled(true);
        config.setRightSelectedObjectEnabled(true);
        return config;
    }

    public static SynchronizeMembersEditorConfiguration getLeftEnabledConfiguration() {
        SynchronizeMembersEditorConfiguration config = new SynchronizeMembersEditorConfiguration();
        config.setLeftEditorEnabled(true);
        config.setLeftSelectObjectEnabled(true);
        config.setRightEditorEnabled(false);
        config.setRightSelectedObjectEnabled(false);
        return config;
    }

    public static SynchronizeMembersEditorConfiguration getRightEnabledConfiguration() {
        SynchronizeMembersEditorConfiguration config = new SynchronizeMembersEditorConfiguration();
        config.setLeftEditorEnabled(false);
        config.setLeftSelectObjectEnabled(false);
        config.setRightEditorEnabled(true);
        config.setRightSelectedObjectEnabled(true);
        return config;
    }

    public static SynchronizeMembersEditorConfiguration getReadOnlyConfiguration() {
        SynchronizeMembersEditorConfiguration config = new SynchronizeMembersEditorConfiguration();
        config.setLeftEditorEnabled(false);
        config.setLeftSelectObjectEnabled(false);
        config.setRightEditorEnabled(false);
        config.setRightSelectedObjectEnabled(false);
        return config;
    }

    public boolean isLeftEditorEnabled() {
        return isLeftEditorEnabled;
    }

    public void setLeftEditorEnabled(boolean enabled) {
        this.isLeftEditorEnabled = enabled;
    }

    public boolean isLeftSelectObjectEnabled() {
        return isLeftSelectObjectEnabled;
    }

    public void setLeftSelectObjectEnabled(boolean enabled) {
        this.isLeftSelectObjectEnabled = enabled;
    }

    public boolean isRightEditorEnabled() {
        return isRightEditorEnabled;
    }

    public void setRightEditorEnabled(boolean enabled) {
        this.isRightEditorEnabled = enabled;
    }

    public boolean isRightSelectObjectEnabled() {
        return isRightSelectObjectEnabled;
    }

    public void setRightSelectedObjectEnabled(boolean enabled) {
        this.isRightSelectObjectEnabled = enabled;
    }
}
