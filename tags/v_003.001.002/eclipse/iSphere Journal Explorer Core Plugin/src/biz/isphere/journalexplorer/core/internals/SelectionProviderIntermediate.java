/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.internals;

import java.util.ArrayList;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * IPostSelectionProvider implementation that delegates to another
 * ISelectionProvider or IPostSelectionProvider. The selection provider used for
 * delegation can be exchanged dynamically. Registered listeners are adjusted
 * accordingly. This utility class may be used in workbench parts with multiple
 * viewers.
 * 
 * @author Marc R. Hoffmann
 */
public class SelectionProviderIntermediate implements IPostSelectionProvider {

    private final ListenerList selectionListeners = new ListenerList();
    private final ListenerList postSelectionListeners = new ListenerList();

    private ArrayList<ISelectionProvider> delegates;

    public SelectionProviderIntermediate() {
        this.delegates = new ArrayList<ISelectionProvider>();
    }

    private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

        public void selectionChanged(SelectionChangedEvent event) {

            if (SelectionProviderIntermediate.this.delegates.contains(event.getSelectionProvider())) {
                fireSelectionChanged(getAllDelegatesSelection());
            }
        }
    };

    private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {

        public void selectionChanged(SelectionChangedEvent event) {

            if (SelectionProviderIntermediate.this.delegates.contains(event.getSelectionProvider())) {
                firePostSelectionChanged(getAllDelegatesSelection());
            }
        }
    };

    private IStructuredSelection getAllDelegatesSelection() {
        ArrayList<ISelection> selection = new ArrayList<ISelection>();

        for (ISelectionProvider provider : this.delegates) {
            if (provider.getSelection() != null) {
                selection.add(provider.getSelection());
            }
        }

        return new StructuredSelection(selection);
    }

    /**
     * Sets a new selection provider to delegate to. Selection listeners
     * registered with the previous delegate are removed before.
     * 
     * @param newDelegate new selection provider
     */
    public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {

        if (this.delegates.contains(newDelegate)) {
            return;
        } else if (newDelegate != null) {

            newDelegate.addSelectionChangedListener(this.selectionListener);

            if (newDelegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider)newDelegate).addPostSelectionChangedListener(this.postSelectionListener);
            }

            fireSelectionChanged(getAllDelegatesSelection());
            firePostSelectionChanged(getAllDelegatesSelection());
            this.delegates.add(newDelegate);
        }
    }

    public void removeSelectionProviderDelegate(ISelectionProvider oldDelegate) {

        if (oldDelegate != null && this.delegates.contains(oldDelegate)) {
            oldDelegate.removeSelectionChangedListener(this.selectionListener);

            if (oldDelegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider)oldDelegate).removePostSelectionChangedListener(this.postSelectionListener);
            }

            oldDelegate.setSelection(null);
            fireSelectionChanged(getAllDelegatesSelection());
            firePostSelectionChanged(getAllDelegatesSelection());
            this.delegates.remove(oldDelegate);
        }
    }

    protected void fireSelectionChanged(ISelection selection) {
        this.fireSelectionChanged(this.selectionListeners, selection);
    }

    protected void firePostSelectionChanged(ISelection selection) {
        fireSelectionChanged(postSelectionListeners, selection);
    }

    private void fireSelectionChanged(ListenerList list, ISelection selection) {

        /*
         * Fire event only once and not for all tabs.
         */
        // for (ISelectionProvider provider : this.delegates) {

        if (delegates.isEmpty()) {
            return;
        }

        SelectionChangedEvent event = new SelectionChangedEvent(delegates.get(0), selection);

        Object[] listeners = list.getListeners();

        for (int i = 0; i < listeners.length; i++) {
            ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
            listener.selectionChanged(event);
        }
    }

    // }

    // /
    // / IPostSelectionProvider implemented methods
    // /
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        this.selectionListeners.add(listener);
    }

    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        this.selectionListeners.remove(listener);
    }

    public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
        this.postSelectionListeners.add(listener);
    }

    public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
        this.postSelectionListeners.remove(listener);
    }

    public ISelection getSelection() {

        ArrayList<Object> selectedObjects = new ArrayList<Object>();

        for (ISelectionProvider provider : this.delegates) {
            selectedObjects.add(provider.getSelection());
        }

        if (selectedObjects.size() > 0) {
            return new StructuredSelection(selectedObjects);
        } else {
            return null;
        }
    }

    public void setSelection(ISelection selection) {
    }
}
