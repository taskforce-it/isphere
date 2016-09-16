/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.filter;

public class FilterQualifier {

    private boolean singleFilterPool;

    public FilterQualifier(boolean singleFilterPool) {
        this.singleFilterPool = singleFilterPool;
    }

    public boolean isSingleFilterPool() {
        return singleFilterPool;
    }

}
