/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.rse.subsystems;

import com.ibm.etools.systems.subsystems.ISystem;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.impl.AbstractSystemManager;

public class TN5250JSystemManager extends AbstractSystemManager {

    private static TN5250JSystemManager inst;

    protected TN5250JSystemManager() {
        super();
    }

    public static TN5250JSystemManager getTN5250JSystemManager() {
        if (inst == null) inst = new TN5250JSystemManager();
        return inst;
    }

    @Override
    public ISystem createSystemObject(SubSystem subsystem) {
        return new TN5250JSystem(subsystem);
    }

    @Override
    public boolean sharesSystem(SubSystem otherSubSystem) {
        return (otherSubSystem instanceof ITN5250JSubSystem);
    }

    @Override
    public Class getSubSystemCommonInterface(SubSystem subsystem) {
        return ITN5250JSubSystem.class;
    }

}
