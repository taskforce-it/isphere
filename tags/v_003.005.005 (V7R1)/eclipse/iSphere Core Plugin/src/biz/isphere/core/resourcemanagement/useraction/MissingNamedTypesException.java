/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import biz.isphere.core.Messages;

public class MissingNamedTypesException extends Exception {

    private static final long serialVersionUID = 930462098100705749L;

    public MissingNamedTypesException(String[] missingNamedTypes) {
        super(Messages
            .bind(Messages.Cannot_import_user_actions_because_the_following_named_types_are_missing_colon, arrayToString(missingNamedTypes)));
    }

    private static String arrayToString(String[] missingNamedTypes) {

        StringBuilder buffer = new StringBuilder();

        for (String namedType : missingNamedTypes) {
            if (buffer.length() > 0) {
                buffer.append(" ");
            }
            buffer.append(namedType);
        }

        return buffer.toString();
    }
}
