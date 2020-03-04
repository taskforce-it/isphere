/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.dataqueue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataqueue.retrieve.description.RDQD0100;
import biz.isphere.core.internal.ISeries;
import biz.isphere.core.preferences.Preferences;

public class DataQueuePropertySource implements IPropertySource {

    protected static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
    protected static final String PROPERTY_LIBRARY = "library"; //$NON-NLS-1$
    protected static final String PROPERTY_OBJECT_TYPE = "objectType"; //$NON-NLS-1$
    protected static final String PROPERTY_TEXT = "text"; //$NON-NLS-1$
    protected static final String PROPERTY_NUMBER_OF_MESSAGES = "numberOfMessages"; //$NON-NLS-1$
    protected static final String PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_ALLOWED = "maximumNumberOfMessagesAllowed"; //$NON-NLS-1$
    protected static final String PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_SPECIFIED = "maximumNumberOfSpecified"; //$NON-NLS-1$
    protected static final String PROPERTY_SEQUENCE = "sequence"; //$NON-NLS-1$
    protected static final String PROPERTY_FORCE = "force"; //$NON-NLS-1$
    protected static final String PROPERTY_IS_AUTO_RECLAIM = "isAutoReclaim"; //$NON-NLS-1$
    protected static final String PROPERTY_LAST_RECLAIM_DATE = "lastReclaimDate"; //$NON-NLS-1$
    protected static final String PROPERTY_IS_KEYED = "isKeyed"; //$NON-NLS-1$
    protected static final String PROPERTY_KEY_LENGTH = "keyLength"; //$NON-NLS-1$

    private final IPropertyDescriptor[] propertiesTable;

    private RDQD0100 rdqd0100;

    public DataQueuePropertySource(RDQD0100 rdqd0100) {
        this.rdqd0100 = rdqd0100;

        propertiesTable = createPropertiesTable(rdqd0100);
    }

    private IPropertyDescriptor[] createPropertiesTable(RDQD0100 rdqd0100) {

        List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();

        try {

            properties.add(createPropertyDescriptor(PROPERTY_NAME, Messages.Name));
            properties.add(createPropertyDescriptor(PROPERTY_LIBRARY, Messages.Library));
            properties.add(createPropertyDescriptor(PROPERTY_OBJECT_TYPE, Messages.Type));
            properties.add(createPropertyDescriptor(PROPERTY_TEXT, Messages.Text));
            properties.add(createPropertyDescriptor(PROPERTY_NUMBER_OF_MESSAGES, Messages.Number_of_messages));
            properties.add(createPropertyDescriptor(PROPERTY_IS_KEYED, Messages.Keyed));

            properties.add(createPropertyDescriptor(PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_ALLOWED, Messages.Maximum_number_of_messages_allowed, true));
            properties.add(createPropertyDescriptor(PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_SPECIFIED, Messages.Maximum_number_of_messages_specified,
                true));
            properties.add(createPropertyDescriptor(PROPERTY_SEQUENCE, Messages.Sequence, true));
            properties.add(createPropertyDescriptor(PROPERTY_FORCE, Messages.Force_to_storage, true));
            properties.add(createPropertyDescriptor(PROPERTY_IS_AUTO_RECLAIM, Messages.Automatically_reclaimed, true));

            if (rdqd0100.isKeyed()) {
                properties.add(createPropertyDescriptor(PROPERTY_KEY_LENGTH, Messages.Key_length, true));
            }

            if (rdqd0100.isAutomaticReclaim()) {
                properties.add(createPropertyDescriptor(PROPERTY_LAST_RECLAIM_DATE, Messages.Last_reclaimed_at, true));
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to create properties table of data DataQueuePropertySource.", e); //$NON-NLS-1$
        }

        return properties.toArray(new IPropertyDescriptor[properties.size()]);
    }

    private PropertyDescriptor createPropertyDescriptor(String name, String text, boolean isAdvanced) {

        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(name, text);
        if (isAdvanced) {
            propertyDescriptor.setFilterFlags(new String[] { IPropertySheetEntry.FILTER_ID_EXPERT });
        }
        return propertyDescriptor;
    }

    private PropertyDescriptor createPropertyDescriptor(String name, String text) {

        return createPropertyDescriptor(name, text, false);
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {

        return propertiesTable;
    }

    public Object getPropertyValue(Object name) {

        try {

            if (name.equals(PROPERTY_NAME)) {
                return rdqd0100.getDataQueueNameUsed();
            } else if (name.equals(PROPERTY_LIBRARY)) {
                return rdqd0100.getDataQueueLibraryUsed();
            } else if (name.equals(PROPERTY_OBJECT_TYPE)) {
                return ISeries.DTAQ;
            } else if (name.equals(PROPERTY_TEXT)) {
                return rdqd0100.getTextDescription();
            } else if (name.equals(PROPERTY_NUMBER_OF_MESSAGES)) {
                return rdqd0100.getNumberOfMessages();
            } else if (name.equals(PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_ALLOWED)) {
                return rdqd0100.getMaximumNumberOfEntriesAllowed();
            } else if (name.equals(PROPERTY_MAXIMUM_NUMBER_OF_MESSAGES_SPECIFIED)) {
                return rdqd0100.getMaximumNumberOfEntriesSpecified();
            } else if (name.equals(PROPERTY_IS_KEYED)) {
                return rdqd0100.isKeyed();
            } else if (name.equals(PROPERTY_KEY_LENGTH)) {
                return rdqd0100.getKeyLength();
            } else if (name.equals(PROPERTY_SEQUENCE)) {
                return rdqd0100.getSequence();
            } else if (name.equals(PROPERTY_FORCE)) {
                return rdqd0100.isForceToStorage();
            } else if (name.equals(PROPERTY_IS_AUTO_RECLAIM)) {
                return rdqd0100.isAutomaticReclaim();
            } else if (name.equals(PROPERTY_LAST_RECLAIM_DATE)) {
                if (rdqd0100.getLastReclaimDateAndTime() == null) {
                    return null;
                } else {
                    return Preferences.getInstance().getDateFormatter().format(rdqd0100.getLastReclaimDateAndTime());
                }
            }

        } catch (Throwable e) {
            ISpherePlugin.logError("Failed to retrieve data queue property value: " + name, e); //$NON-NLS-1$
        }

        return null;
    }

    public boolean isPropertySet(Object paramObject) {
        return false;
    }

    public void resetPropertyValue(Object paramObject) {
    }

    public void setPropertyValue(Object paramObject1, Object paramObject2) {
    }
}
