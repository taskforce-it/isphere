/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataspaceeditordesigner.model;

@SuppressWarnings("serial")
public class DDecimal extends AbstractDWidget {

    private int fraction;

    DDecimal(String label, int offset, int length, int fraction) {
        super(label, offset, length);
        this.fraction = fraction;
    }

    public int getFraction() {
        return fraction;
    }
}
