/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

/**
 * This class describes a 4-byte integer sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public class APIInt4FieldDescription extends AbstractAPIFieldDescription {

    /**
     * Constructs a APIInt4FieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     */
    public APIInt4FieldDescription(String name, int offset) {
        super(name, offset, 4);
    }
}
