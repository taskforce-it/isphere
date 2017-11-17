/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.base.interfaces;

import com.ibm.as400.access.FieldDescription;

public interface IJoesdParserDelegate {

    public FieldDescription getDateFieldDescription(String name, String format, String separator);

    public FieldDescription getTimeFieldDescription(String name, String format, String separator);

    public FieldDescription getTimestampFieldDescription(String name);

    public FieldDescription getDecRealFieldDescription(String name);

    public FieldDescription getDecDoubleFieldDescription(String name);

}
