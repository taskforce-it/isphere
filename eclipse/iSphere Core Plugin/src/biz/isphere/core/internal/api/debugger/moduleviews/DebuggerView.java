/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api.debugger.moduleviews;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class DebuggerView {

    private String object;
    private String library;
    private String objType;
    private String module;
    private String type;
    private boolean isMainView;
    private Date timestamp;
    private String description;
    private int number;

    private int id;
    private int lines;

    public DebuggerView(String object, String library, String objType, SDMV0100 sdmv0100) throws UnsupportedEncodingException {

        this.object = object;
        this.library = library;
        this.objType = objType;
        this.module = sdmv0100.getModule();
        this.type = sdmv0100.getViewType();

        if (SDMV0100.MAIN_VIEW.equals(sdmv0100.getMainIndicator())) {
            this.isMainView = true;
        } else {
            this.isMainView = false;
        }

        this.timestamp = sdmv0100.getViewTimestamp();
        this.description = sdmv0100.getViewDescription();
        this.number = sdmv0100.getViewNumber();

        this.id = -1;
        this.lines = -1;
    }

    public boolean isListingView() {

        if (SDMV0100.LISTING_VIEW.equals(getType())) {
            return true;
        }

        return false;
    }

    public String getObject() {
        return object;
    }

    public String getLibrary() {
        return library;
    }

    public String getObjectType() {
        return objType;
    }

    public String getModule() {
        return module;
    }

    public String getType() {
        return type;
    }

    public boolean isMain() {
        return isMainView;
    }

    public boolean isNoMain() {
        return !isMain();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public int getNumber() {
        return number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLines() {
        return lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return getDescription().trim() + " (" + getNumber() + " -> " + getType().trim() + ")";
    }
}
