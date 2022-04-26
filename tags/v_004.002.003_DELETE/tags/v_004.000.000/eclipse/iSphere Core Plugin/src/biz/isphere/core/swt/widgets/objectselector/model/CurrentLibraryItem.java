/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets.objectselector.model;

import org.eclipse.swt.graphics.Image;

import com.ibm.as400.access.AS400;

public class CurrentLibraryItem extends LibraryItem {

    public CurrentLibraryItem(AS400 system, String libraryName, Image image, String objectTypeFilter) {
        super(system, libraryName, image, objectTypeFilter);
    }
}
