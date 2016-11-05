/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobloganalyzer.jobs;

import biz.isphere.jobloganalyzer.editor.DroppedLocalFile;

public interface IDropFileListener {

    public void dropJobLog(DroppedLocalFile droppedLocalFile, Object target);

}
