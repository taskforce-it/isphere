/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

public class JobLogNotLoadedException extends Exception {

    private static final long serialVersionUID = 7185214712627033235L;

    public JobLogNotLoadedException(String message) {
        super(message);
    }

}
