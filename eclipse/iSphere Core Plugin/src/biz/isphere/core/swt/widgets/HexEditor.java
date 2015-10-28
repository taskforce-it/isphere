/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TypedListener;

import biz.isphere.core.swt.widgets.internal.HexEditorInternal;

public class HexEditor extends HexEditorInternal {

    /**
     * SwtHexEditor implements a Control for editing hex data. It is able to
     * show and edit a tabbed nibble view and/or an ASCII view. SwtHexEditor is
     * only able to work on buffers with constant length. So the initial buffer
     * size has to be provided. However it is possible to change the buffer
     * afterwards.
     * 
     * @param parent - parent composite where to insert
     * @param style -The composite's style argument use NO_ASCII to prevent the
     *        ASCII part from showing up.
     */
    public HexEditor(Composite parent, int style) {
        this(parent, style, DEFAULT_BUFFER_SIZE, DEFAULT_BYTES_PER_LINE, DEFAULT_BYTES_GROUPED);
    }

    /**
     * SwtHexEditor implements a Control for editing hex data. It is able to
     * show and edit a tabbed nibble view and/or an ASCII view. SwtHexEditor is
     * only able to work on buffers with constant length. So the initial buffer
     * size has to be provided. However it is possible to change the buffer
     * afterwards.
     * 
     * @param parent - parent composite where to insert
     * @param style -The composite's style argument use NO_ASCII to prevent the
     *        ASCII part from showing up.
     * @param bufferSize - initial buffer size
     */
    public HexEditor(Composite parent, int style, int bufferSize) {
        this(parent, style, bufferSize, DEFAULT_BYTES_PER_LINE, DEFAULT_BYTES_GROUPED);
    }

    /**
     * SwtHexEditor implements a Control for editing hex data. It is able to
     * show and edit a tabbed nibble view and/or an ASCII view. SwtHexEditor is
     * only able to work on buffers with constant length. So the initial buffer
     * size has to be provided. However it is possible to change the buffer
     * afterwards.
     * 
     * @param parent - parent composite where to insert
     * @param style -The composite's style argument use NO_ASCII to prevent the
     *        ASCII part from showing up.
     * @param bufferSize - initial buffer size
     * @param bytesPerLine - number of hex bytes displayed per line
     * @param bytesGrouped - number of hex bytes grouped. Each group is
     *        delimited by a hyphen ('-')
     */
    public HexEditor(Composite parent, int style, int bufferSize, int bytesPerLine, int bytesGrouped) {
        super(parent, style, bufferSize, bytesPerLine, bytesGrouped);
    }

    /**
     * same as setByteData( byte[] ), however interpreting boolean as byte[1]
     * 
     * @param bs
     */
    public void setByteData(int b) {

        byte[] byteData = new byte[] { (byte)((b >> 0) & 0xFF), (byte)((b >> 8) & 0xFF), (byte)((b >> 16) & 0xFF), (byte)((b >> 24) & 0xFF) };
        setByteData(byteData);
    }

    /**
     * same as setByteData( byte[] ), however interpreting short as byte[2]
     * 
     * @param bs
     */
    public void setByteData(short s) {

        byte[] byteData = new byte[] { (byte)((s >> 0) & 0xFF), (byte)((s >> 8) & 0xFF) };
        setByteData(byteData);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified
     * when the receiver's text is modified, by sending it one of the messages
     * defined in the <code>ModifyListener</code> interface.
     * 
     * @param listener the listener which should be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *            thread that created the receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see removeModifyListener
     */
    public void addModifyListener(ModifyListener listener) {

        checkWidget();
        if (listener != null) {
            TypedListener typedListener = new TypedListener(listener);
            addListener(SWT.Modify, typedListener);
        }
    }

    /**
     * Removes the listener from the collection of listeners who will be
     * notified when the receiver's text is modified.
     * 
     * @param listener the listener which should no longer be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *            thread that created the receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see #addModifyListener
     */
    public void removeModifyListener(ModifyListener listener) {

        checkWidget();
        if (listener != null) {
            removeListener(SWT.Modify, listener);
        }
    }
}
