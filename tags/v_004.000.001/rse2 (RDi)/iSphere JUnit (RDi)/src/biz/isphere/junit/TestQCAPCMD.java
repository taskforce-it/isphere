/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.junit;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.core.api.qcapcmd.CPOP0100;
import biz.isphere.core.api.qcapcmd.QCAPCMD;

import com.ibm.as400.access.AS400;

public class TestQCAPCMD {

    private AS400 as400;

    public TestQCAPCMD() {

        String hostname = System.getProperty("isphere.junit.as400");
        String user = System.getProperty("isphere.junit.username");
        String password = System.getProperty("isphere.junit.password");

        as400 = new AS400(hostname, user, password);
    }

    @Test
    public void testCheckCommand() throws Exception {

        boolean rc;
        CPOP0100 cpop0100;
        QCAPCMD qcapcmd = new QCAPCMD(as400);

        cpop0100 = new CPOP0100(as400);
        rc = qcapcmd.execute("DLYJOB DLY(1)", cpop0100);
        Assert.assertTrue(rc);

        cpop0100 = new CPOP0100(as400, CPOP0100.CMD_CHECK_CL_STATEMENT);
        rc = qcapcmd.execute("DLYJOB DLY(60)", cpop0100);
        Assert.assertTrue(rc);

    }

    @Test
    public void testFormatCommand() throws Exception {

        boolean rc;
        QCAPCMD qcapcmd = new QCAPCMD(as400);
        rc = qcapcmd.execute("crtbndrpg LIBRARY/PROGRAM QGPL/QRPGLESRC *PGM ", CPOP0100.checkCLStatement(as400));
        Assert.assertTrue(rc);

        String formattedCommand = qcapcmd.getChangedCommand();
        Assert.assertEquals("CRTBNDRPG PGM(LIBRARY/PROGRAM) SRCFILE(QGPL/QRPGLESRC) SRCMBR(*PGM)", formattedCommand);
    }
}
