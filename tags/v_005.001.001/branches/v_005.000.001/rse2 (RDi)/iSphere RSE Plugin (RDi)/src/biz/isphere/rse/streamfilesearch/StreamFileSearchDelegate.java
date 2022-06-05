/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.streamfilesearch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.streamfilesearch.AbstractStreamFileSearchDelegate;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

public class StreamFileSearchDelegate extends AbstractStreamFileSearchDelegate {

    private IBMiConnection connection;

    public StreamFileSearchDelegate(Shell shell, IBMiConnection connection) {
        this(shell, connection, null);
    }

    public StreamFileSearchDelegate(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        super(shell, monitor);

        this.connection = connection;
    }

}
