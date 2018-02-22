/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.lpex.tasktags.model;

public class LPEXTask {

    public static final String ID = "biz.isphere.lpex.tasktags.model.LPEXTask";

    @SuppressWarnings("unused")
    private String tag;

    private String message;

    private int line;

    private Integer priority;

    private int charStart;

    public LPEXTask(String aTag, String aMessage, int aLine, Integer aPriority) {
        this(aTag, aMessage, aLine, aPriority, -1);
    }

    public LPEXTask(String aTag, String aMessage, int aLine, Integer aPriority, int aCharStart) {
        tag = aTag;
        message = aMessage;
        line = aLine;
        priority = aPriority;
        charStart = aCharStart;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public Integer getPriority() {
        return priority;
    }

    public int getCharStart() {
        return charStart;
    }

    public int getCharEnd() {
        if (charStart == -1) {
            return -1;
        }
        return charStart + getMessage().length();
    }

    @Override
    public String toString() {
        return "" + getMessage() + "(priority=" + priority + ")";
    }
}