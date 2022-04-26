/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal.viewmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import biz.isphere.core.ISpherePlugin;

/**
 * This class manages pinned views, which have to be restored when the IDE is
 * started again. A pinnable view must at least call {@link #add(IPinnableView)}
 * from its constructor and {@link #remove(IPinnableView)} from the dispose()
 * method.
 * <p>
 * The view can implement @link {@link IPinnableView#getPinProperties()} to
 * provide custom properties that are required to restore the state of the view.
 * These properties are stored in 'dialog_settings.xml'.
 * 
 * @author Thomas Raddatz
 */
public abstract class AbstractViewManager implements IViewManager {

    private static final String IS_PINNED = "isPinned";

    private static final String CONTENT_ID = "contentId";

    private static final String VIEW_PIN_PROPERTY = "view.";

    private List<IPinnableView> monitorViews;

    private String name;

    private IDialogSettings settings;

    private boolean isLoadingView;

    /**
     * Produces a view manager for a given view.
     * 
     * @param name - name of the settings section. Should be one of the
     *        constants defined in {@link IViewManager}.
     */
    protected AbstractViewManager(String name) {
        this.name = name;
        this.monitorViews = new ArrayList<IPinnableView>();

        IPageService pageService = (IPageService)PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        pageService.addPerspectiveListener(new PerspectiveAdapter() {
            @Override
            public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
                super.perspectiveChanged(page, perspective, partRef, changeId);
                if (IWorkbenchPage.CHANGE_VIEW_HIDE.equals(changeId)) {
                    IViewReference viewRef = (IViewReference)partRef;
                    IPinnableView view = (IPinnableView)viewRef.getView(false);
                    view.setPinned(false);
                }
            }
        });
    }

    /**
     * Primarily ensures that the IRSEPersitanceManager has been initialized.
     * Otherwise we cannot load any remote objects.
     */
    public abstract boolean isInitialized(int timeout);

    /**
     * Returns the view that matches a given ID and secondary ID. If no matching
     * view is found, a new view is created.
     * 
     * @param viewId - ID of the view
     * @param contentId - ID that uniquely identified the content that is
     *        displayed by the view
     */
    public synchronized IPinnableView getView(String viewId, String contentId) throws PartInitException {

        IPinnableView view = null;
        boolean visible = false;

        this.isLoadingView = true;

        try {

            /*
             * Search all known views for the selected object
             */
            for (IPinnableView monitorView : monitorViews) {
                String viewedObject = monitorView.getContentId();
                if (viewedObject != null && viewedObject.equals(contentId)) {
                    view = monitorView;
                    break;
                }
            }

            /*
             * Search all known views for an unpinned view.
             */
            if (view == null) {
                for (IPinnableView monitorView : monitorViews) {
                    if (!monitorView.isPinned()) {
                        view = monitorView;
                        break;
                    }
                }
            }

            IWorkbenchPage page = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
            if (view == null) {
                visible = true;
                String secondaryId = createSecondaryId();
                view = (IPinnableView)page.showView(viewId, secondaryId, IWorkbenchPage.VIEW_ACTIVATE);
            }

            if (!visible && view != null) {
                page.showView(viewId, view.getViewSite().getSecondaryId(), IWorkbenchPage.VIEW_VISIBLE);
            }

        } finally {
            this.isLoadingView = false;
        }

        return view;
    }

    private String createSecondaryId() {

        int count = 0;
        String id = null;
        do {
            id = "secondaryId_" + count;
            count++;
        } while (secondaryIdExists(id));

        return id;
    }

    private boolean secondaryIdExists(String id) {

        for (IPinnableView view : monitorViews) {
            if (id.equals(view.getViewSite().getSecondaryId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a view to the view manager. Usually this method is called from the
     * constructor of the view.
     * 
     * @param view - view, that is added to the manager
     */
    public void add(IPinnableView view) {
        monitorViews.add(view);
    }

    /**
     * Removes a view from the view manager and saves the state of the view.
     * This method is intended to be called from the dispose() method of a view.
     * 
     * @param view - view, that is removed from the manager
     */
    public void remove(IPinnableView view) {
        saveViewStatus(view);
        monitorViews.remove(view);
    }

    /**
     * Returns the 'pinned' state of a given view.
     * 
     * @param view - view, whose 'pinned' state is returned
     */
    public boolean isPinned(IPinnableView view) {
        IDialogSettings settings = getDialogSettings();
        return settings.getBoolean(getKey(view, IS_PINNED));
    }

    /**
     * Returns <code>true</code> while the view manager is loading a view.
     * <p>
     * This method is intended to be used with
     * {@link ViewPart#createPartControl(org.eclipse.swt.widgets.Composite)} to
     * test whether or not the view is loaded by the view manager or during the
     * Eclipse start-up process. If the view is loaded by the view manager, it
     * must not check and restore the 'pinned' state.
     */
    public boolean isLoadingView() {
        return isLoadingView;
    }

    /**
     * Returns the ID of the content that is displayed by a given view.
     * 
     * @param view - view, whose content ID is returned
     * @return content ID of the displayed content
     */
    public String getContentId(IPinnableView view) {
        IDialogSettings settings = getDialogSettings();
        return settings.get(getKey(view, CONTENT_ID));
    }

    /**
     * Returns the view specific properties that are required to restore the
     * state of the view.
     * 
     * @param view - view, whose properties are returned
     * @param pinKeys - keys of the values that are retrieved and returned
     */
    public Map<String, String> getPinProperties(IPinnableView view, Set<String> pinKeys) {

        IDialogSettings settings = getDialogSettings();
        Map<String, String> pinProperties = new HashMap<String, String>();
        for (String key : pinKeys) {
            String value = settings.get(getKey(view, VIEW_PIN_PROPERTY + key));
            pinProperties.put(key, value);
        }

        return pinProperties;
    }

    /**
     * Saves the status of a given view.
     * 
     * @param view - view, whose status is saved
     */
    private void saveViewStatus(IPinnableView view) {

        IDialogSettings settings = getDialogSettings();
        settings.put(getKey(view, IS_PINNED), new Boolean(view.isPinned()));
        settings.put(getKey(view, CONTENT_ID), view.getContentId());

        Map<String, String> pinProperties = view.getPinProperties();
        Iterator<String> iter = pinProperties.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            String value = pinProperties.get(key);
            settings.put(getKey(view, VIEW_PIN_PROPERTY + key), value);
        }
    }

    /**
     * Returns the final key that is required to get a value from
     * 'dialog_settings.xml'.
     * 
     * @param view - view the key is associated to
     * @param subKey - sub key that uniquely identifies a specific property
     * @return key value
     */
    private String getKey(IPinnableView view, String subKey) {
        return view.getViewSite().getSecondaryId() + ":" + subKey;
    }

    /**
     * Returns the settings that are used by this view manager.
     * 
     * @return settings of the view manager, aka the pinned view.
     */
    private IDialogSettings getDialogSettings() {
        if (settings == null) {
            IDialogSettings workbenchSettings = ISpherePlugin.getDefault().getDialogSettings();
            settings = workbenchSettings.getSection(name);
            if (settings == null) {
                settings = workbenchSettings.addNewSection(name);
            }
        }
        return settings;
    }
}