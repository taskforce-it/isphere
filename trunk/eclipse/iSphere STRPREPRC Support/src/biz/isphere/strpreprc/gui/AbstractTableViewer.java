/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.strpreprc.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public abstract class AbstractTableViewer<T> {

    private Shell shell;
    private TableViewer tableViewer;
    private List<ModifyListener> modifyListeners;

    public AbstractTableViewer(Composite parent) {
        this.shell = parent.getShell();

        tableViewer = createViewer(parent);

        getTable().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.keyCode == 127) {
                    performDelete();
                }
            }
        });
    }

    public void setInput(T[] commands) {
        tableViewer.setInput(commands);
        
        fireModifiedEvent();
    }

    public T[] getInput() {
        return (T[])tableViewer.getInput();
    }

    protected abstract TableViewer createViewer(Composite parent);

    protected abstract T[] createArray(int size);

    protected Shell getShell() {
        return shell;
    }

    protected void performDelete() {

        if (tableViewer.getSelection().isEmpty()) {
            return;
        }

        if (MessageDialog.openQuestion(shell, "Delete items", "Delete selected items?")) {
            // Delete items
            ISelection selection = tableViewer.getSelection();
            if (selection instanceof StructuredSelection) {
                StructuredSelection selectedItems = (StructuredSelection)selection;

                List<T> tempList = new ArrayList<T>(Arrays.asList(getInput()));
                Iterator<T> iterator = selectedItems.iterator();
                while (iterator.hasNext()) {
                    tempList.remove(iterator.next());
                }

                setInput(tempList.toArray(createArray(tempList.size())));
                
                fireModifiedEvent();
            }
        }
    }
    
    public void addModifyListener(ModifyListener listener) {

        if (modifyListeners == null) {
            modifyListeners = new ArrayList<ModifyListener>();
        }

        if (!modifyListeners.contains(listener)) {
            modifyListeners.add(listener);
        }
    }

    public void removeModifyListener(ModifyListener listener) {
        modifyListeners.remove(listener);
    }
    
    private void fireModifiedEvent() {
        
        if (modifyListeners == null ){
            return;
        }
        
        for (ModifyListener listener : modifyListeners) {
            listener.modified(new ModifyEvent(tableViewer));
        }
    }

    private Table getTable() {
        return tableViewer.getTable();
    }
    
    public static interface ModifyListener extends EventListener {
        /**
         * Notifies the listener that the content has just been changed
         */
        public void modified(ModifyEvent event);
    }

    public static class ModifyEvent extends EventObject {

        private static final long serialVersionUID = -5601303889330609238L;

        public ModifyEvent(Object source) {
            super(source);
        }
    }
}
