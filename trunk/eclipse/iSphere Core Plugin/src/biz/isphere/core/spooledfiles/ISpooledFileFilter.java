/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

public interface ISpooledFileFilter {

    public static final String ALL = "*";
    public static final String EXACTLY = "*EXACTLY";
    public static final String TODAY = "*TODAY";
    public static final String YESTERDAY = "*YESTERDAY";
    public static final String LASTWEEK = "*LASTWEEK";
    public static final String LASTMONTH = "*LASTMONTH";

}
