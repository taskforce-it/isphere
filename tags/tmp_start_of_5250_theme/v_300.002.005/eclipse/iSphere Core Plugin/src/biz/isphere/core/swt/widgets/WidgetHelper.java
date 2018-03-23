/*******************************************************************************
 * Copyright (c) 2004, 2017 Plum Canary Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Plum Canary Corporation - initial API and implementation
 *     iSphere Project Team
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * The utility class that provides miscellaneous {@link Widget}operations.
 */
public final class WidgetHelper {
    /**
     * Hooks the given widget to listen event of the given type. The widget will
     * be automatically unhooked when disposed.
     * 
     * @param widget the widget
     * @param event the event type
     * @param listener the event listener
     */
    public static void hook(Widget widget, int event, Listener listener) {
        hook(widget, new int[] { event }, listener);
    }

    /**
     * Hooks the given widget to listen events of the given types. The widget
     * will be automatically unhooked when disposed.
     * 
     * @param widget the widget
     * @param events the event types
     * @param listener the event listener
     */
    public static void hook(final Widget widget, final int[] events, final Listener listener) {
        assert widget != null : "Argument \"widget\" cannot be null"; //$NON-NLS-1$
        assert events != null : "Argument \"events\" cannot be null"; //$NON-NLS-1$
        assert listener != null : "Argument \"listener\" cannot be null"; //$NON-NLS-1$

        if (events.length == 0) {
            return;
        }

        for (int i = 0; i < events.length; i++) {
            widget.addListener(events[i], listener);
        }

        widget.addListener(SWT.Dispose, new Listener() {
            /*
             * (non-Javadoc)
             * @see
             * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.
             * widgets.Event)
             */
            public void handleEvent(Event event) {
                unhook(widget, events, listener);
            }
        });
    }

    /**
     * @param widget
     * @return
     */
    public static boolean isVisualized(Widget widget) {
        return widget != null && !widget.isDisposed();
    }

    /**
     * Unhooks the given widget for the given event type.
     * 
     * @param widget the widget
     * @param event the event type
     * @param listener the event listener
     */
    public static void unhook(Widget widget, int event, Listener listener) {
        unhook(widget, new int[] { event }, listener);
    }

    /**
     * Unhooks the given widget for the given event types.
     * 
     * @param widget the widget
     * @param events the event types
     * @param listener the event listener
     */
    public static void unhook(Widget widget, int[] events, Listener listener) {
        assert widget != null : "Argument \"widget\" cannot be null"; //$NON-NLS-1$
        assert events != null : "Argument \"events\" cannot be null"; //$NON-NLS-1$
        assert listener != null : "Argument \"listener\" cannot be null"; //$NON-NLS-1$

        if (events.length == 0) {
            return;
        }

        for (int i = 0; i < events.length; i++) {
            widget.removeListener(events[i], listener);
        }
    }

    /**
     * Adds a focus listener to a given composite and all its children.
     * 
     * @param composite composite, the listener is added
     * @param listener listener that is added to the composite
     */
    public static void addFocusListener(Composite composite, FocusListener listener) {
        addFocusListenerRecursively(composite.getChildren(), listener);
    }

    private static void addFocusListenerRecursively(Control[] children, FocusListener listener) {

        for (Control child : children) {
            child.addFocusListener(listener);
            if (child instanceof Composite) {
                Control[] grandChildren = ((Composite)child).getChildren();
                addFocusListenerRecursively(grandChildren, listener);
            }
        }
    }

    /**
     * Adds a typed listener to a given composite and all its children.
     * 
     * @param composite composite, the listener is added
     * @param eventType the type of event to listen for
     * @param listener the listener which should be notified when the event
     *        occurs
     */
    public static void addListener(Composite composite, int eventType, TypedListener listener) {
        composite.addListener(eventType, listener);
        addListenerRecursively(composite.getChildren(), eventType, listener);
    }

    private static void addListenerRecursively(Control[] children, int eventType, TypedListener listener) {

        for (Control child : children) {
            child.addListener(eventType, listener);
            if (child instanceof Composite) {
                Control[] grandChildren = ((Composite)child).getChildren();
                addListenerRecursively(grandChildren, eventType, listener);
            }
        }
    }

    /**
     * Private constructor to block instantiation.
     */
    private WidgetHelper() {
    }
}
