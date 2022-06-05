/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.exception;

import biz.isphere.core.Messages;
import biz.isphere.core.internal.Version;

public class InvalidVersionNumberException extends Exception {

    private static final long serialVersionUID = 4762556050709197752L;

    public InvalidVersionNumberException(String versionNumber) {
        super(Messages.bind(Messages.Invalid_version_number_A_The_version_number_does_not_match_the_pattern_B, versionNumber,
            Version.VERSION_NUMBER_PATTERN));
    }

}
