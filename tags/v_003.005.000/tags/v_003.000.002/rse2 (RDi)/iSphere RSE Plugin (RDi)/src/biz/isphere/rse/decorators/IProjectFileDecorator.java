/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import biz.isphere.core.ISpherePlugin;

import com.ibm.etools.iseries.perspective.internal.model.impl.ISeriesNativeObject;
import com.ibm.etools.iseries.perspective.model.AbstractISeriesNativeMember;
import com.ibm.etools.iseries.projects.core.IIBMiPropertyKeyConstants;

/**
 * This class decorates the RSE tree with the object decoration of objects of
 * type <i>IISeriesHostObjectBrief</i>. It shows the object description next to
 * the element.
 * <p>
 * This class has been inspired by <a
 * href="https://www.eclipse.org/articles/Article-Decorators/decorators.html"
 * >Understanding Decorators in Eclipse</a>.
 */
public class IProjectFileDecorator implements ILightweightLabelDecorator {

    public static final String ID = "biz.isphere.rse.decorators.IProjectFileDecorator";

    /**
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
     *      org.eclipse.jface.viewers.IDecoration)
     */
    public void decorate(Object object, IDecoration decoration) {

        try {

            String description = "";

            if ((object instanceof ISeriesNativeObject)) {
                ISeriesNativeObject iSeriesNativeObject = (ISeriesNativeObject)object;
                if (iSeriesNativeObject.isSourceFile()) {
                    description = iSeriesNativeObject.getPropertiesModel().getProperty(IIBMiPropertyKeyConstants.TEXT_DESCRIPTION);
                }
            } else if ((object instanceof AbstractISeriesNativeMember)) {
                AbstractISeriesNativeMember iSeriesNativeMember = (AbstractISeriesNativeMember)object;
                if (iSeriesNativeMember.getIsLocal()) {
                    description = iSeriesNativeMember.getPropertiesModel().getProperty(IIBMiPropertyKeyConstants.TEXT_DESCRIPTION);
                }
            } else {
                return;
            }

            if (description != null) {
                decoration.addSuffix(" - \"" + description.trim() + "\"");
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("*** Could not decorate object ***", e);
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
}
