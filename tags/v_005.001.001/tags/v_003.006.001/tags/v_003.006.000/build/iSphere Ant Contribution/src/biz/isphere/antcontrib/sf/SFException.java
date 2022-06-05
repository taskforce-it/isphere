/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.antcontrib.sf;

public class SFException extends Exception {

    private static final long serialVersionUID = 6599141787050301995L;

    public SFException(String message) {
        super(message);
    }

    public SFException(String message, Throwable e) {
        super(message, e);
    }

}
