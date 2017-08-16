/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.rse.shared.model;

import biz.isphere.journalexplorer.base.interfaces.IJoesdParserDelegate;

import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.CharacterFieldDescription;
import com.ibm.as400.access.FieldDescription;

public final class JoesdParserDelegate implements IJoesdParserDelegate {

    public FieldDescription getDateFieldDescription(String name) {
        return new CharacterFieldDescription(new AS400Text(10), name);
    }

    public FieldDescription getTimeFieldDescription(String name) {
        return new CharacterFieldDescription(new AS400Text(8), name);
    }

    public FieldDescription getTimestampFieldDescription(String name) {
        return new CharacterFieldDescription(new AS400Text(32), name);
    }

}
