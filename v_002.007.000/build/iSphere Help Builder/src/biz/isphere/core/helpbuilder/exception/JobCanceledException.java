/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.helpbuilder.exception;

public class JobCanceledException extends Exception {

    private static final long serialVersionUID = -1908156636924144824L;

    public JobCanceledException(String text) {
        super(text);
    }

    public JobCanceledException(String text, Throwable e) {
        super(text, e);
    }
}
