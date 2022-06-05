/*******************************************************************************
 * Copyright (c) 2012-2020 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.spooledfiles;

import java.util.ArrayList;

import biz.isphere.core.internal.exception.CanceledByUserException;
import biz.isphere.rse.handler.DeleteSpooledFileHandler;

public class SpooledFileDeleteAction extends AbstractSpooledFileAction {

    private ArrayList<SpooledFileResource> spooledFileResources;

    @Override
    public void init() {
        spooledFileResources = new ArrayList<SpooledFileResource>();
    }

    @Override
    public String execute(SpooledFileResource spooledFileResource) throws CanceledByUserException {

        spooledFileResource.getSpooledFile().setData(spooledFileResource);

        spooledFileResources.add(spooledFileResource);

        return null;

    }

    @Override
    public String finish() {

        DeleteSpooledFileHandler handler = new DeleteSpooledFileHandler(getShell());
        handler.deleteSpooledFiles(spooledFileResources);

        return null;
    }

}