/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

/**
 * This class describes a character sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public class APICharFieldDescription extends AbstractAPIFieldDescription {

    /**
     * Constructs a APICharFieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     * @param length - length of the field data
     */
    public APICharFieldDescription(String name, int offset, int length) {
        super(name, offset, length);
    }
}
