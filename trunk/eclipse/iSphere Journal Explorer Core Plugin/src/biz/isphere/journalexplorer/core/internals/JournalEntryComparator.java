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

package biz.isphere.journalexplorer.core.internals;

import java.util.Comparator;

import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;

public class JournalEntryComparator implements Comparator<JournalProperties> {

    public int compare(JournalProperties left, JournalProperties right) {

        if (left.getJOESDProperty().compareTo(right.getJOESDProperty()) == 0) {
            return 0;
        } else {
            return -1;
        }

    }
}
