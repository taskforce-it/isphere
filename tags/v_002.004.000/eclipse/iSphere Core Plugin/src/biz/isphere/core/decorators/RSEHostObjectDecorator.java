/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.ibm.etools.iseries.comm.interfaces.IISeriesHostObjectBrief;

/**
 * This class decorates the RSE tree with the object decoration of objects of
 * type <i>IISeriesHostObjectBrief</i>. It shows the object description next to
 * the element.
 * <p>
 * This class has been inspired by <a
 * href="https://www.eclipse.org/articles/Article-Decorators/decorators.html"
 * >Understanding Decorators in Eclipse</a>.
 */
public class RSEHostObjectDecorator implements ILightweightLabelDecorator {

    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
     *      org.eclipse.jface.viewers.IDecoration)
     */
    public void decorate(Object object, IDecoration decoration) {

        // Get the resource
        IISeriesHostObjectBrief tResource = getResource(object);
        if (tResource == null) {
            return;
        }

        try {
            if (tResource.getDescription() != null) {
                decoration.addSuffix(" - \"" + tResource.getDescription().trim() + "\"");
            }
            return;
        } catch (Exception e) {
            // ignore all errors
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener arg0) {
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        /*
         * Disposal of images present in the image registry can be performed in
         * this method
         */
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
     *      java.lang.String)
     */
    public boolean isLabelProperty(Object arg0, String arg1) {
        return false;
    }

    /**
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener arg0) {
    }

    /**
     * Returns the resource for the given input object, or null if there is no
     * resource associated with it.
     * 
     * @param object the object to find the resource for
     * @return the resource for the given object, or null
     */
    private IISeriesHostObjectBrief getResource(Object object) {
        if (object instanceof IISeriesHostObjectBrief) {
            return (IISeriesHostObjectBrief)object;
        }
        return null;
    }

}
