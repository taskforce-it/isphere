/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.Comparator;

import biz.isphere.core.spooledfiles.BasicSpooledFileSortComparator;
import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.SpooledFileAttributes;

/**
 * Class for comparing spooled files when sorting them.
 */
public class SpooledFileSortComparator implements Comparator<Object> {

    private BasicSpooledFileSortComparator comparator;

    public SpooledFileSortComparator(SpooledFileAttributes sortByField) {
        comparator = new BasicSpooledFileSortComparator(sortByField, false);
    }

    public int compare(Object o1, Object o2) {
        return comparator.compare(getSpooledFile(o1), getSpooledFile(o2));
    }

    private SpooledFile getSpooledFile(Object object) {

        if (object instanceof SpooledFileResource) {
            SpooledFileResource resource = (SpooledFileResource)object;
            return resource.getSpooledFile();
        }

        return null;
    }

}
