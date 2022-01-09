/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.search;

public enum MatchOption {
    ALL ("*ALL"),
    ANY ("*ANY"),
    LINE ("*LINE"),
    MSGID ("*MSGID");

    /**
     * ID, that is used for storing the last used value and that is passed to
     * service program FNDSTR. The ID must not be changed! It must match the
     * values defined in /include member SRCHOPTS_T.
     */
    private String id;

    /**
     * Label of the match option. It is used in the GUI and for Excel exports.
     * It can be changed, if applicable. At the moment it equals the ID.
     */
    private String label;

    private MatchOption(String id) {
        this.id = id;
        this.label = id;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
