/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import java.nio.ByteBuffer;

public final class ByteHelper {

    public static byte[] copyOfRange(byte[] bytes, int offset, int length) {
        byte[] subBytes = new byte[length];
        ByteBuffer.wrap(bytes, offset, length).get(subBytes, 0, length);
        return subBytes;
    }

}
