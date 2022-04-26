/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;

public class UnknownFieldDescription extends CharacterFieldDescription {

    private static final long serialVersionUID = 4913807811105581261L;

    public UnknownFieldDescription(AS400Text dataType, String name) {
        super(dataType, name);
    }

}
