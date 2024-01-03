/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import biz.isphere.core.Messages;

public enum LoadPreviousMemberValue {
    NONE ("None"),
    CONNECTION_LIBRARY_FILE_MEMBER (Messages.Label_Connection_Library_File_Member),
    CONNECTION_LIBRARY_FILE (Messages.Label_Connection_Library_File),
    CONNECTION_LIBRARY (Messages.Label_Connection_Library),
    CONNECTION (Messages.Label_Connection);

    private String label;

    private LoadPreviousMemberValue(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static LoadPreviousMemberValue valueOfLabel(String label) {
        if (NONE.label().equals(label)) {
            return NONE;
        } else if (CONNECTION.label().equals(label)) {
            return CONNECTION;
        } else if (CONNECTION_LIBRARY.label().equals(label)) {
            return CONNECTION_LIBRARY;
        } else if (CONNECTION_LIBRARY_FILE.label().equals(label)) {
            return CONNECTION_LIBRARY_FILE;
        } else if (CONNECTION_LIBRARY_FILE_MEMBER.label().equals(label)) {
            return CONNECTION_LIBRARY_FILE_MEMBER;
        } else {
            throw new IllegalArgumentException("Unknown label: " + label);
        }
    }

    public boolean isConnection() {
        // @formatter:off
        if (this.equals(CONNECTION_LIBRARY_FILE_MEMBER) || 
            this.equals(CONNECTION_LIBRARY_FILE) || 
            this.equals(CONNECTION_LIBRARY) ||
            this.equals(CONNECTION)) {
            return true;
        }
        return false;
        // @formatter:on
    }

    public boolean isLibrary() {
        // @formatter:off
        if (this.equals(CONNECTION_LIBRARY_FILE_MEMBER) || 
            this.equals(CONNECTION_LIBRARY_FILE) || 
            this.equals(CONNECTION_LIBRARY)) {
            return true;
        }
        return false;
        // @formatter:on
    }

    public boolean isFile() {
        // @formatter:off
        if (this.equals(CONNECTION_LIBRARY_FILE_MEMBER) || 
            this.equals(CONNECTION_LIBRARY_FILE)) {
            return true;
        }
        return false;
        // @formatter:on
    }

    public boolean isMember() {
        // @formatter:off
        if (this.equals(CONNECTION_LIBRARY_FILE_MEMBER)) {
            return true;
        }
        return false;
        // @formatter:on
    }
}
