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

import biz.isphere.core.preferencepages.IPreferences;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.Messages;

public class SpooledFilesSaveToDirectoryAsTextAction extends AbstractSpooledFileSaveToDirectoryAction {

    public SpooledFilesSaveToDirectoryAsTextAction() {
        super(Messages.Save_as_Text, IPreferences.OUTPUT_FORMAT_TEXT, ISphereRSEPlugin.getDefault().getImageRegistry().getDescriptor(
            ISphereRSEPlugin.IMAGE_TEXT));
    }

}
