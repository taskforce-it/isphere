/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.widgets.actions;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import biz.isphere.base.internal.ClipboardHelper;
import biz.isphere.base.internal.StringHelper;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.JournalEntry;
import biz.isphere.journalexplorer.core.model.adapters.JOESDProperty;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperties;
import biz.isphere.journalexplorer.core.model.adapters.JournalProperty;
import biz.isphere.journalexplorer.core.model.dao.ColumnsDAO;
import biz.isphere.journalexplorer.core.preferences.Preferences;

public class CopyJournalPropertyToClipboardAction extends Action {

    private static final String NEW_LINE = "\n"; //$NON-NLS-1$
    private static final String QUAL_SIGN = "="; //$NON-NLS-1$

    private IStructuredSelection selection;
    private boolean copyAll;

    private Set<Object> copied;
    private Preferences preferences;

    public CopyJournalPropertyToClipboardAction(boolean copyAll) {
        this(null, copyAll);
    }

    public CopyJournalPropertyToClipboardAction(IStructuredSelection selection, boolean copyAll) {

        this.selection = selection;
        this.copyAll = copyAll;
        this.copied = new HashSet<Object>();
        this.preferences = Preferences.getInstance();

        if (this.copyAll) {
            setText(Messages.CopyAllAction_text);
            if (this.preferences.isTrimValues()) {
                setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(
                    ISphereJournalExplorerCorePlugin.IMAGE_COPY_NAME_VALUE_TRIMMED));
            } else {
                setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(
                    ISphereJournalExplorerCorePlugin.IMAGE_COPY_NAME_VALUE));
            }
        } else {
            setText(Messages.CopyValueAction_text);
            if (this.preferences.isTrimValues()) {
                setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault().getImageDescriptor(
                    ISphereJournalExplorerCorePlugin.IMAGE_COPY_VALUE_TRIMMED));
            } else {
                setImageDescriptor(ISphereJournalExplorerCorePlugin.getDefault()
                    .getImageDescriptor(ISphereJournalExplorerCorePlugin.IMAGE_COPY_VALUE));
            }
        }
    }

    public void setSelectedItems(StructuredSelection selection) {
        this.selection = selection;
    }

    @Override
    public void run() {

        if (selection == null) {
            return;
        }
        // System.out.println("------------------------------");

        StringBuilder buffer = new StringBuilder();
        copied.clear();

        Iterator<?> iterator = selection.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof JournalEntry) {
                JournalEntry journalEntry = (JournalEntry)object;
                if (!isCopied(journalEntry)) {
                    JournalProperties properties = new JournalProperties(journalEntry);
                    addJournalProperties(buffer, properties);
                    copied.add(journalEntry);
                }
            } else if (object instanceof JournalProperties) {
                JournalProperties properties = (JournalProperties)object;
                JournalEntry journalEntry = properties.getJournalEntry();
                if (!isCopied(journalEntry)) {
                    addJournalProperties(buffer, properties);
                    copied.add(journalEntry);
                }
            } else if (object instanceof JOESDProperty) {
                JOESDProperty joesdProperty = (JOESDProperty)object;
                if (!isCopied(joesdProperty)) {
                    Object[] children = joesdProperty.toPropertyArray();
                    addRecordProperties(buffer, children);
                    copied.add(joesdProperty);
                }
            } else if (object instanceof JournalProperty) {
                JournalProperty property = (JournalProperty)object;
                if (!isCopied(property)) {
                    addProperty(buffer, property);
                    copied.add(property);
                }
            }
        }

        if (buffer.length() > 0) {
            ClipboardHelper.setText(buffer.toString());
            // System.out.println(buffer.toString());
        }

    }

    private void addJournalEntryProperties(StringBuilder buffer, JournalEntry journalEntry) {
        addProperty(buffer, ColumnsDAO.ID.name(), journalEntry.getId());
        addProperty(buffer, ColumnsDAO.JOENTL.name(), journalEntry.getEntryLength());
        addProperty(buffer, ColumnsDAO.JOSEQN.name(), journalEntry.getSequenceNumber());
        addProperty(buffer, ColumnsDAO.JOCODE.name(), journalEntry.getJournalCode(), true);
        addProperty(buffer, ColumnsDAO.JOENTT.name(), journalEntry.getEntryType(), true);
        addProperty(buffer, ColumnsDAO.JOCTRR.name(), journalEntry.getEntryLength());
    }

    private void addJournalProperties(StringBuilder buffer, JournalProperties properties) {
        String value = properties.toString();
        addProperty(buffer, Messages.CopyAction_fileProperty, value, false);
        addJournalEntryProperties(buffer, properties.getJournalEntry());
        Object[] children = properties.getJOESDProperty().toPropertyArray();
        addRecordProperties(buffer, children);
    }

    private void addRecordProperties(StringBuilder buffer, Object[] children) {
        for (Object child : children) {
            if (child instanceof JournalProperty) {
                JournalProperty property = (JournalProperty)child;
                if (!isCopied(property)) {
                    addProperty(buffer, property);
                    copied.add(property);
                }
            }
        }
    }

    private boolean isCopied(Object object) {

        if (object instanceof JournalProperty) {
            if (isCopied(((JournalProperty)object).parent)) {
                return true;
            }
        }

        return copied.contains(object);
    }

    private void addProperty(StringBuilder buffer, String name, int value) {
        addProperty(buffer, name, Integer.toString(value), false);
    }

    private void addProperty(StringBuilder buffer, String name, BigInteger value) {
        addProperty(buffer, name, value.toString(), false);
    }

    private void addProperty(StringBuilder buffer, JournalProperty property) {
        addProperty(buffer, property.name, property.value.toString(), !property.isNumeric());
    }

    private void addProperty(StringBuilder buffer, String name, String value, boolean isQuoted) {

        if (buffer.length() > 0) {
            buffer.append(NEW_LINE);
        }

        if (copyAll) {
            buffer.append(name);
            buffer.append(QUAL_SIGN);
        }

        if (preferences.isTrimValues()) {
            value = StringHelper.trimR(value);
        }

        if (isQuoted) {
            buffer.append(StringHelper.addQuotes(value));
        } else {
            buffer.append(value);
        }
    }
}
