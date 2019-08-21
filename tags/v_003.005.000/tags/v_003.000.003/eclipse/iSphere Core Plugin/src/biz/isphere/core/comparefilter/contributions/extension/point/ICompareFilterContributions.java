/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.comparefilter.contributions.extension.point;

public interface ICompareFilterContributions {

    public String[] getFileExtensions();

    public String[] getDefaultFileExtensions();

    public void setFileExtensions(String[] extensions);

    public String getImportExportLocation();

    public void setImportExportLocation(String location);
}
