/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.command;

import java.util.Comparator;

public class CommandXmlComparator implements Comparator<RSECommand> {

    public int compare(RSECommand o1, RSECommand o2) {

        if (o1 == null && o2 == null) {
            return 0;
        } else if (o2 == null) {
            return 1;
        } else if (o1 == null) {
            return -1;
        }

        if (o1.getOrder() > o2.getOrder()) {
            return 1;
        } else if (o1.getOrder() < o2.getOrder()) {
            return -1;
        } else {
            o1.getLabel().compareTo(o2.getLabel());
        }

        return 0;
    }

}
