/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

public abstract class AbstractResult<M> {

    private long startTime;

    private int countTotal;
    private int countSkipped;
    private int countProcessed;
    private int countErrors;
    private long averageTime;

    private M cancelErrorId;
    private String cancelMessage;

    public AbstractResult() {
        startTime = System.currentTimeMillis();
    }

    public boolean isError() {
        if (countErrors > 0) {
            return true;
        }
        return false;
    }

    public void addTotal() {
        countTotal++;
    }

    public int getTotal() {
        return countTotal;
    }

    public void addSkipped() {
        countSkipped++;
    }

    public int getSkipped() {
        return countSkipped;
    }

    public void addProcessed() {
        countProcessed++;
    }

    public int getProcessed() {
        return countProcessed;
    }

    public void addError() {
        countErrors++;
    }

    public int getErrors() {
        return countErrors;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setCancel(M errorId, String message) {
        cancelErrorId = errorId;
    }

    public M getCancelErrorId() {
        return cancelErrorId;
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void finished() {
        averageTime = (System.currentTimeMillis() - startTime) / countTotal;
    }
}
