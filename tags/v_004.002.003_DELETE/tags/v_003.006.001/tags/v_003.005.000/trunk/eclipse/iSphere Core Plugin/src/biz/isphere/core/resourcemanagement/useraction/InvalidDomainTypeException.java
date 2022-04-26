/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.resourcemanagement.useraction;

import biz.isphere.core.Messages;

public class InvalidDomainTypeException extends Exception {

    private static final long serialVersionUID = 5324295097308573931L;

    public InvalidDomainTypeException(String domainName) {
        super(Messages.bind(Messages.Cannot_import_user_actions_because_the_domain_A_found_in_the_XML_file_does_not_match_the_selected_domain,
            domainName));
    }
}
