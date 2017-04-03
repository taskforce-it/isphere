/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import java.util.Vector;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.tn5250j.rse.actions.NewDesignerSessionAction;
import biz.isphere.tn5250j.rse.actions.NewSessionAction;

import com.ibm.etools.systems.dftsubsystem.impl.DefaultSubSystemFactoryImpl;
import com.ibm.etools.systems.subsystems.*;
import com.ibm.etools.systems.model.*;

public class TN5250JSubSystemFactory extends DefaultSubSystemFactoryImpl {

    public TN5250JSubSystemFactory() {
        super();
    }

    @Override
    protected SubSystem createSubSystemInternal(SystemConnection conn) {
        return new TN5250JSubSystem();
    }

    @Override
    public boolean supportsFilters() {
        return false;
    }

    @Override
    protected Vector getAdditionalSubSystemActions(SubSystem subSystem, Shell shell) {

        Vector<IAction> actions = new Vector<IAction>();

        IAction newSessionAction = new NewSessionAction(shell);
        actions.add(newSessionAction);

        IAction newDesignerSessionAction = new NewDesignerSessionAction(subSystem.getSystemProfileName(), subSystem.getSystemConnectionName(), shell);
        actions.add(newDesignerSessionAction);

        return actions;

    }

}
