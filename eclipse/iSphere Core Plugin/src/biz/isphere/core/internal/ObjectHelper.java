/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ObjectHelper {

    /**
     * Serializes and deserializes a given object to create a deep-cloned
     * object.
     * <p>
     * Inspired by:
     * <p>
     * <code>http://weblogs.java.net/blog/emcmanus/archive/2007/04/ cloning_java_ob.html</code>
     * 
     * @param object - object that is cloned
     * @return deep-clone of <i>object</i>
     */
    public static <T> T cloneVO(T object) {
        try {
            ByteArrayOutputStream tArrayOut = new ByteArrayOutputStream();
            ObjectOutputStream tStreamOut = new ObjectOutputStream(tArrayOut);
            tStreamOut.writeObject(object);
            byte[] tBytes = tArrayOut.toByteArray();

            ByteArrayInputStream tArrayIn = new ByteArrayInputStream(tBytes);
            ObjectInputStream tStreamIn = new ObjectInputStream(tArrayIn);

            @SuppressWarnings("unchecked")
            T tClone = (T)tStreamIn.readObject();
            return tClone;
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone object of type '" + object.getClass().getName(), e);
        }
    }

}
