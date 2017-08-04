/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.model.adapters;

public class JournalProperty implements Comparable<JournalProperty> {

    public String name;
    public Object value;
    public Object parent;
    public boolean highlighted;

    public JournalProperty(String name, Object value, Object parent) {
        this.name = name;
        this.value = value;
        this.parent = parent;
    }

    public int compareTo(JournalProperty comparable) {

        if (this.name.equals(comparable.name) && this.value.equals(comparable.value)) {
            this.highlighted = comparable.highlighted = false;
            return 0;
        } else {
            this.highlighted = comparable.highlighted = true;
            return -1;
        }
    }

}
