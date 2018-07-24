/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

public class MissingNamedTypesException extends Exception {

    private static final long serialVersionUID = 930462098100705749L;

    private String[] missingNamedTypes;

    public MissingNamedTypesException(String[] missingNamedTypes) {
        this.missingNamedTypes = missingNamedTypes;
    }

    @Override
    public String getLocalizedMessage() {

        StringBuilder buffer = new StringBuilder();

        buffer.append("Cannot import user actions, because the following named types are missing:");
        for (String namedType : missingNamedTypes) {
            buffer.append(" ");
            buffer.append(namedType);
        }

        return buffer.toString();
    }
}
