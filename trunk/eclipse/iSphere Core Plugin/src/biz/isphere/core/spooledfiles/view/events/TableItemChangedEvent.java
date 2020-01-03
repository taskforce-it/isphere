/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.view.events;

import biz.isphere.core.spooledfiles.SpooledFile;

public class TableItemChangedEvent {

    public enum EventType {
        CHANGED,
        HOLD,
        RELEASED,
        MESSAGE,
        DELETED;
    }

    private SpooledFile spooledFile;
    private EventType eventType;

    public TableItemChangedEvent(SpooledFile spooledFile, EventType eventType) {
        this(spooledFile, eventType, -1);
    }

    public TableItemChangedEvent(SpooledFile spooledFile, EventType eventType, int itemIndex) {
        this.spooledFile = spooledFile;
        this.eventType = eventType;
    }

    public SpooledFile getSpooledFile() {
        return spooledFile;
    }

    public boolean isEvent(EventType eventType) {
        return this.eventType == eventType;
    }
}
