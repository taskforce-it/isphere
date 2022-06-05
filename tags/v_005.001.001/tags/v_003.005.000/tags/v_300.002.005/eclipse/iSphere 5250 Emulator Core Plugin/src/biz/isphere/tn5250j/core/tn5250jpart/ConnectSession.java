/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

public class ConnectSession extends Thread {

    private TN5250JPanel tn5250j;

    public ConnectSession(TN5250JPanel tn5250j) {
        this.tn5250j = tn5250j;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        tn5250j.getSession5250().connect();
    }

}
