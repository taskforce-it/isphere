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
    private long totalElapsed;
    private long factor;
    private String unit;

    public TimeTaken(String message) {
        this(message, false);
    }

    public TimeTaken(String message, boolean isNanos) {

        this.startNanons = System.nanoTime();
        this.totalElapsed = -1;

        if (isNanos) {
            this.factor = 1;
            this.unit = "nanos";
        } else {
            this.factor = 1000000;
            this.unit = "mSecs";
        }

        if (message.indexOf("%d") < 0) {
            this.message = message + " took: %d " + unit;
        } else {
            this.message = message;
        }
    }

    public static TimeTaken start(String message) {
        return new TimeTaken(message);
    }

    public void stop() {
        print(String.format(message, timeElapsed()));
    }

    public void stop(int count) {
        stop();
        print(String.format("Time per entry; %d %s", totalElapsed / count, unit));
    }

    private void print(String message) {
        // System.out.println(message);
    }

    private long timeElapsed() {

        long elapsed = (System.nanoTime() - startNanons) / factor;
        this.totalElapsed += elapsed;

        return elapsed;
    }
}
