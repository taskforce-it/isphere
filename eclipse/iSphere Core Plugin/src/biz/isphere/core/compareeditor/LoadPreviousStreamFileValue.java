/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import biz.isphere.core.Messages;

public enum LoadPreviousStreamFileValue {
    NONE ("None"),
    CONNECTION_DIRECTORY_FILE (Messages.Label_Connection_Directory_File),
    CONNECTION_DIRECTORY (Messages.Label_Connection_Directory),
    CONNECTION (Messages.Label_Connection);

    private String label;

    private LoadPreviousStreamFileValue(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static LoadPreviousStreamFileValue valueOfLabel(String label) {
        if (NONE.label().equals(label)) {
            return NONE;
        } else if (CONNECTION.label().equals(label)) {
            return CONNECTION;
        } else if (CONNECTION_DIRECTORY.label().equals(label)) {
            return CONNECTION_DIRECTORY;
        } else if (CONNECTION_DIRECTORY_FILE.label().equals(label)) {
            return CONNECTION_DIRECTORY_FILE;
        } else {
            throw new IllegalArgumentException("Unknown label: " + label);
        }
    }

    public boolean isConnection() {
        // @formatter:off
        if (this.equals(CONNECTION_DIRECTORY_FILE) || 
            this.equals(CONNECTION_DIRECTORY) ||
            this.equals(CONNECTION)) {
            return true;
        }
        return false;
        // @formatter:on
    }

    public boolean isDirectory() {
        // @formatter:off
        if (this.equals(CONNECTION_DIRECTORY_FILE) || 
            this.equals(CONNECTION_DIRECTORY)) {
            return true;
        }
        return false;
        // @formatter:on
    }

    public boolean isFile() {
        // @formatter:off
        if (this.equals(CONNECTION_DIRECTORY_FILE)) {
            return true;
        }
        return false;
        // @formatter:on
    }
}
