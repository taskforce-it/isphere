/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eclipse.albireo.core.SwingControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import biz.isphere.tn5250j.core.preferences.Preferences;
import biz.isphere.tn5250j.core.session.Session;

/**
 * This class creates a session panel, that is about to be added to a session
 * tab of an "iSphere 5250 Sessions" view.
 */
public class CreateSessionPanel {

    private ITN5250JPart tn5250jPart;

    public TN5250JPanel run(Composite compositeControl, ArrayList<Composite> arrayListCompositeSession,
        ArrayList<TN5250JPanel> arrayListTabItemTN5250J, Session session, TN5250JInfo tn5250jInfo) {

        ITN5250JPart tn5250jPart = tn5250jInfo.getTN5250JPart();

        this.tn5250jPart = tn5250jPart;

        Composite compositeBorder = new Composite(compositeControl, SWT.NONE);
        compositeBorder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        compositeBorder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        FillLayout fillLayoutBorder = new FillLayout();
        fillLayoutBorder.marginWidth = 3;
        fillLayoutBorder.marginHeight = 3;
        compositeBorder.setLayout(fillLayoutBorder);

        ScrolledComposite sc = new ScrolledComposite(compositeBorder, SWT.H_SCROLL | SWT.V_SCROLL);

        final Composite compositeSession = new Composite(sc, SWT.NONE);
        compositeSession.setLayout(new FillLayout());

        // Set the child as the scrolled content of the ScrolledComposite
        sc.setContent(compositeSession);

        // Set the minimum size
        Preferences preferences = Preferences.getInstance();
        if (preferences.isMinimalSessionSizeEnabled()) {
            int hSize;
            try {
                hSize = preferences.getMinimalSessionHorizontalSize();
            } catch (NumberFormatException e1) {
                hSize = 0;
            }
            int vSize;
            try {
                vSize = preferences.getMinimalSessionVerticalSize();
            } catch (NumberFormatException e1) {
                vSize = 0;
            }
            sc.setMinSize(hSize, vSize);
        } else {
            sc.setMinSize(0, 0);
        }

        // Expand both horizontally and vertically
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        final TN5250JPanel tn5250j = tn5250jInfo.getTN5250JPanel(session, tn5250jPart.getTabFolderSessions().getShell());

        tn5250j.addScreenListener();

        tn5250j.getSessionGUI().addFocusListener(new java.awt.event.FocusListener() {
            public void focusGained(java.awt.event.FocusEvent event) {
                processFocusGained(event);
            }

            public void focusLost(java.awt.event.FocusEvent event) {
                processFocusLost(event);
            }
        });

        SwingControl swingControl = new SwingControl(compositeSession, SWT.NONE) {
            @Override
            protected JComponent createSwingComponent() {

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(tn5250j.getSessionGUI());

                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        tn5250j.getSessionGUI().getFocusForMe();
                    }
                });

                return panel;

            }

            @Override
            public Composite getLayoutAncestor() {
                return compositeSession;
            }
        };

        swingControl.addTraverseListener(new TraverseListener() {
            /**
             * This one is hard to explain. Actually there is no explanation for
             * it. All I know is that for whatever reasons, the following keys
             * influence the way a [tab] is processed afterwards:
             * 
             * <pre>
             * '  = apostrophe
             * °  = degree sign
             * "  = double quote (quotion mark)
             * `  = grave accent
             * ~  = tilde
             * </pre>
             * 
             * When a [tab] or Shift+[tab] key is pressed right after one of
             * these keys, the traverse listener is called instead of the key
             * listener. I assume that the problem is very deep in the Swing
             * framework or even at OS level.
             * <p>
             * Therefore traversal is disallowed at all.
             * 
             * @see iSphere ticker #29.
             * @author Thomas Raddatz, 24.10.2016
             */
            public void keyTraversed(TraverseEvent paramTraverseEvent) {
                paramTraverseEvent.doit = false;
            }
        });

        swingControl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                Composite sourceComposite = (Composite)keyEvent.getSource();
                final TN5250JPanel sourceTN5250J = (TN5250JPanel)sourceComposite.getData(SessionPanelData.COMPOSITE_TN5250J);
                if (keyEvent.keyCode == SWT.F10) {
                    keyEvent.doit = false;
                    if ((keyEvent.stateMask & SWT.SHIFT) == 0) {
                        sourceTN5250J.getSession5250().getScreen().sendKeys("[pf10]");
                    } else {
                        sourceTN5250J.getSession5250().getScreen().sendKeys("[pf22]");
                    }
                } else if (keyEvent.keyCode == SWT.TAB) {
                    keyEvent.doit = false;
                    if ((keyEvent.stateMask & SWT.SHIFT) == 0) {
                        sourceTN5250J.getSession5250().getScreen().sendKeys("[tab]");
                    } else {
                        sourceTN5250J.getSession5250().getScreen().sendKeys("[backtab]");
                    }
                } else if ((keyEvent.stateMask & SWT.CTRL) != 0 && keyEvent.keyCode == SWT.F12) {
                    keyEvent.doit = false;
                    HandleBindingService.getInstance().toggleKeyFilterEnablement();
                }
            }
        });
        swingControl.setData(SessionPanelData.COMPOSITE_TN5250J, tn5250j);

        compositeBorder.setData(SessionPanelData.SCROLLED_COMPOSITE, sc);
        compositeBorder.setData(SessionPanelData.COMPOSITE_TN5250J, tn5250j);

        arrayListCompositeSession.add(compositeBorder);
        arrayListTabItemTN5250J.add(tn5250j);
        tn5250jPart.addTN5250JPanel(tn5250j);

        if (tn5250jPart.isMultiSession()) {
            tn5250jPart.setRemoveSession(true);
        }

        return tn5250j;

    }

    private void processFocusGained(java.awt.event.FocusEvent event) {
        if (tn5250jPart.getTabFolderSessions().isDisposed()) {
            return;
        }
        new ProcessSessionFocus(tn5250jPart.getTabFolderSessions().getDisplay(), tn5250jPart.getTabFolderSessions(), "*GAINED", event).start();
    }

    private void processFocusLost(java.awt.event.FocusEvent event) {
        if (tn5250jPart.getTabFolderSessions().isDisposed()) {
            return;
        }
        new ProcessSessionFocus(tn5250jPart.getTabFolderSessions().getDisplay(), tn5250jPart.getTabFolderSessions(), "*LOST", event).start();
    }

}
