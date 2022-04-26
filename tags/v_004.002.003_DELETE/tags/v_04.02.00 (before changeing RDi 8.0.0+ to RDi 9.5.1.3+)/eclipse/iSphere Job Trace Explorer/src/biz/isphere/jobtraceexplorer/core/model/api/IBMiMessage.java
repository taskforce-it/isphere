/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.model.api;

import com.ibm.as400.access.AS400Message;

public class IBMiMessage {

    private String id;
    private String text;
    private String help;

    public IBMiMessage(AS400Message as400Message) {
        this(as400Message.getID(), as400Message.getText(), as400Message.getHelp());
    }

    public IBMiMessage(String id, String text) {
        this(id, text, null);
    }

    public IBMiMessage(String id, String text, String help) {

        this.id = id;
        this.text = text;
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

    public String getID() {
        return id;
    }

    public String getText() {
        return text;
    }
}
