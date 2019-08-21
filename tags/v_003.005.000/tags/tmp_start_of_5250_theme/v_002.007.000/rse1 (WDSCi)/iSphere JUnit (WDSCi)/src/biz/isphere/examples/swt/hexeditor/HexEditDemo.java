/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.examples.swt.hexeditor;

import java.nio.ByteBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.swt.widgets.CaretEvent;
import biz.isphere.core.swt.widgets.CaretListener;
import biz.isphere.core.swt.widgets.HexEditor;

public class HexEditDemo {

    Display d;

    Shell s;

    HexEditDemo() throws Exception {

        d = new Display();
        s = new Shell(d);
        s.setSize(600, 400);
        s.setLocation(550, 400);
        s.setText("A Text Field Example");
        s.setLayout(new GridLayout());

        String sampleData = "The quick brown fox jumps over the lazy dog. Umlaute: צהü";

        StringBuilder dataBuffer = new StringBuilder(256);
        while (dataBuffer.length() + sampleData.length() + 3 < dataBuffer.capacity()) {
            if (dataBuffer.length() != 0) {
                dataBuffer.append(" - ");
            }
            dataBuffer.append(sampleData);
        }

        final HexEditor hexEdit = new HexEditor(s, SWT.NONE, 0, 8, 4);
        hexEdit.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));

        final Label lblCaretOffset = new Label(s, SWT.BORDER);
        lblCaretOffset.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        final Label lblLength = new Label(s, SWT.BORDER);
        lblLength.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        hexEdit.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                lblLength.setText("Bytes used: " + hexEdit.getBytesUsed() + " - Total Buffer size: " + hexEdit.getBufferSize());
            }
        });

        hexEdit.addCaretListener(new CaretListener() {
            public void caretMoved(CaretEvent event) {
                lblCaretOffset.setText("Caret offset: " + event.caretOffset);
            }
        });

        hexEdit.setByteData(dataBuffer.toString().getBytes("utf-8"));
        // hexEdit.adjustBuffer(128, true);

        // hexEdit.setContent(sampleData.getBytes("utf-8"));
        hexEdit.setVaryingMode(false);

        Composite buttonArea = new Composite(s, SWT.BORDER);
        buttonArea.setLayout(new GridLayout(5, true));
        buttonArea.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        Button btnAddData = new Button(buttonArea, SWT.PUSH);
        btnAddData.setLayoutData(new GridData());
        btnAddData.setText("Add");
        btnAddData.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                hexEdit.adjustBuffer(hexEdit.getBufferSize() + 4, true);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button btnSubData = new Button(buttonArea, SWT.PUSH);
        btnSubData.setLayoutData(new GridData());
        btnSubData.setText("Sub");
        btnSubData.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                hexEdit.adjustBuffer(hexEdit.getBufferSize() - 4, true);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        final Button btnVarying = new Button(buttonArea, SWT.CHECK);
        btnVarying.setLayoutData(new GridData());
        btnVarying.setText("Varying");
        btnVarying.setSelection(hexEdit.isVaryingMode());
        btnVarying.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                if (btnVarying.getSelection()) {
                    hexEdit.setVaryingMode(true);
                } else {
                    hexEdit.setVaryingMode(false);
                }
                hexEdit.setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button btnHome = new Button(buttonArea, SWT.PUSH);
        btnHome.setLayoutData(new GridData());
        btnHome.setText("Home");
        btnHome.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                hexEdit.setCaretOffset(0);
                hexEdit.setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button btnEnd = new Button(buttonArea, SWT.PUSH);
        btnEnd.setLayoutData(new GridData());
        btnEnd.setText("End");
        btnEnd.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                if (hexEdit.isVaryingMode()) {
                    hexEdit.setCaretOffset(hexEdit.getBytesUsed() * 2 - 1);
                } else {
                    hexEdit.setCaretOffset(hexEdit.getBufferSize() * 2 - 1);
                }
                hexEdit.setFocus();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        s.open();
        while (!s.isDisposed()) {
            if (!d.readAndDispatch()) d.sleep();
        }
        d.dispose();
    }

    public static byte[] toBytes(final int... intArray) {
        final ByteBuffer bb = ByteBuffer.allocate(4 + (intArray.length * 4));
        for (final int val : intArray) {
            bb.putInt(val);
        }
        return bb.array();
    }

    public static void main(String[] arg) throws Exception {
        new HexEditDemo();
    }

}
