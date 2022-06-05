/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

public class JobNotFoundException extends Exception {

    private static final long serialVersionUID = 8470572378546941786L;

    public JobNotFoundException(String message) {
        super(message);
    }

}
