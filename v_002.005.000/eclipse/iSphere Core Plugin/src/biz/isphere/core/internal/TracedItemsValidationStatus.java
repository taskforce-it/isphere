/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;

/**
 * This class is used to keep track of the number of invalid items in a
 * dedicated context, such as a dialog.
 */
public class TracedItemsValidationStatus {

    private Map<Object, Boolean> tracedItems;
    private int invalidItems;

    public TracedItemsValidationStatus() {
        tracedItems = new HashMap<Object, Boolean>();
        invalidItems = 0;
    }

    /**
     * Updates the status of a given item (e.g. a {@link Control}) or adds the
     * item to the list of traced items.
     * 
     * @param object - object, whose validation status is changed.
     * @param isValid - validation status of the object.
     */
    public void addOrUpdateItemStatus(Object object, boolean isValid) {

        Boolean isValidOld = tracedItems.get(object);
        if (isValidOld == null && isValid) {
            return;
        }

        if (isValidOld != null && isValid == isValidOld) {
            return;
        }

        if (isValid) {
            invalidItems--;
        } else {
            invalidItems++;
        }

        tracedItems.put(object, Boolean.valueOf(isValid));

        assert invalidItems >= 0 : "Number of invalid items must be greator or equal zero."; //$NON-NLS-1$
        assert invalidItems <= tracedItems.size() : "Number of invalid items must be lower or equal to the size of the map of traced items."; //$NON-NLS-1$
    }

    /**
     * Returns the overall validation status of the items being traced.
     * 
     * @return overall validation status
     */
    public boolean isValid() {
        if (invalidItems == 0) {
            return true;
        }
        return false;
    }

}
