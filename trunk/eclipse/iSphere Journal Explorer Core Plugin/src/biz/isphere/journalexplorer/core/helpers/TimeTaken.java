/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.helpers;

public class TimeTaken {

    private String message;
    private long startNanons;
    private long elapsed;

    public TimeTaken(String message) {

        if (message.indexOf("%d") < 0) {
            this.message = message + " took: %d mSecs";
        } else {
            this.message = message;
        }

        this.startNanons = System.nanoTime();
        this.elapsed = -1;
    }

    public static TimeTaken start(String message) {
        return new TimeTaken(message);
    }

    public void stop() {
        System.out.println(String.format(message, timeElapsed()));
    }

    public void stop(int count) {
        stop();
        System.out.println(String.format("Time per entry; %d mSecs", timeElapsed() / count));
    }

    private long timeElapsed() {
        if (elapsed < 0) {
            elapsed = (System.nanoTime() - startNanons) / 1000000;
        }
        return elapsed;
    }
}
