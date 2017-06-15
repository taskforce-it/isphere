/*******************************************************************************
 * Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Task Force IT-Consulting GmbH - initial API and implementation
 *******************************************************************************/

package biz.isphere.rse.internal;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import biz.isphere.core.internal.IControlDecoration;

/**
 * iSphere Implementation for {@link ControlDecoration}.
 * <p>
 * This class has been added for backporting {@link ControlDecoration} to the
 * iSphere WDSCi plugin. For the RDi plugin we need the inherited class to get a
 * class that implements {@link IControlDecoration}
 */
public class RSEControlDecoration extends ControlDecoration implements IControlDecoration {

    public RSEControlDecoration(Control control, int position) {
        super(control, position);
    }

    public RSEControlDecoration(Control control, int position, Composite composite) {
        super(control, position, composite);
    }

}
