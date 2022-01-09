/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.tn5250j.keyboard;

public enum HostKey {
    PF1 ("[pf1]"),
    PF2 ("[pf2]"),
    PF3 ("[pf3]"),
    PF4 ("[pf4]"),
    PF5 ("[pf5]"),
    PF6 ("[pf6]"),
    PF7 ("[pf7]"),
    PF8 ("[pf8]"),
    PF9 ("[pf9]"),
    PF10 ("[pf10]"),
    PF11 ("[pf11]"),
    PF12 ("[pf12]"),
    PF13 ("[pf13]"),
    PF14 ("[pf14]"),
    PF15 ("[pf15]"),
    PF16 ("[pf16]"),
    PF17 ("[pf17]"),
    PF18 ("[pf18]"),
    PF19 ("[pf19]"),
    PF20 ("[pf20]"),
    PF21 ("[pf21]"),
    PF22 ("[pf22]"),
    PF23 ("[pf23]"),
    PF24 ("[pf24]");

    private String label;

    private HostKey(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
