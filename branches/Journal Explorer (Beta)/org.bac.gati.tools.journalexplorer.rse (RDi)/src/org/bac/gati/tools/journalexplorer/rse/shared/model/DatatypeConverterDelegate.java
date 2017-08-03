/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.bac.gati.tools.journalexplorer.rse.shared.model;

import javax.xml.bind.DatatypeConverter;

import org.bac.gati.tools.journalexplorer.rse.base.interfaces.IDatatypeConverterDelegate;

public class DatatypeConverterDelegate implements IDatatypeConverterDelegate {

    @Override
    public byte[] parseHexBinary(String paramString) {
        return DatatypeConverter.parseHexBinary(paramString);
    }

}
