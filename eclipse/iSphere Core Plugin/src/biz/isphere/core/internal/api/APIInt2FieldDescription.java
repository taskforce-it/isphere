/*******************************************************************************
 * Copyright (c) 2023-2023 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.api;

/**
 * This class describes a 2-byte integer sub field of an API format.
 * 
 * @author Thomas Raddatz
 */
public class APIInt2FieldDescription extends AbstractAPIFieldDescription {

    /**
     * Constructs a APIInt2FieldDescription object.
     * 
     * @param name - field name
     * @param offset - offset to the field data
     */
    public APIInt2FieldDescription(String name, int offset) {
        super(name, offset, 2);
    }
}
