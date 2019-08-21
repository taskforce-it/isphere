/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Version;

import biz.isphere.base.versioncheck.PluginCheck;
import biz.isphere.tn5250j.core.tn5250jeditor.TN5250JEditor;
import biz.isphere.tn5250j.core.tn5250jview.TN5250JView;

public class ProcessSessionFocus {

    // RDi 9.5+ = Eclipse 4.4+
    private static final Version RDI95 = new Version(4, 4, 0);

    private Display display;
    private CTabFolder tabFolderSessions;
    private String mode;
    private java.awt.event.FocusEvent event;

    public ProcessSessionFocus(Display display, CTabFolder tabFolderSessions, String mode, java.awt.event.FocusEvent event) {
        this.display = display;
        this.tabFolderSessions = tabFolderSessions;
        this.mode = mode;
        this.event = event;
    }

    public void start() {
        display.asyncExec(new Runnable() {
            public void run() {
                int color;
                if (mode.equals("*GAINED")) {
                    color = SWT.COLOR_RED;
                } else if (mode.equals("*LOST")) {
                    color = SWT.COLOR_WHITE;
                } else {
                    color = SWT.COLOR_BLACK;
                }
                CTabItem[] tabItems = tabFolderSessions.getItems();
                for (int idx1 = 0; idx1 < tabItems.length; idx1++) {
                    if (!tabItems[idx1].isDisposed()) {
                        ArrayList arrayListCompositeSession = (ArrayList)tabItems[idx1].getData(SessionTabData.COMPOSITE_SESSION);
                        for (int idx2 = 0; idx2 < arrayListCompositeSession.size(); idx2++) {
                            Composite compositeSession = (Composite)arrayListCompositeSession.get(idx2);
                            TN5250JPanel tn5250j = (TN5250JPanel)compositeSession.getData(SessionPanelData.COMPOSITE_TN5250J);
                            if (tn5250j.getSessionGUI() == event.getSource()) {
                                compositeSession.setBackground(Display.getCurrent().getSystemColor(color));
                                if (mode.equals("*GAINED")) {
                                    tabItems[idx1].setData(SessionTabData.LAST_FOCUS, new Integer(idx2));
                                }
                            }
                        }
                    }

                }

                if (mode.equals("*GAINED")) {
                    if (PluginCheck.getPlatformVersion().compareTo(RDI95) > 0) {
                        bugFixRDi95_activateView();
                    }
                }
            }

            /**
             * Bugfix for iSphere ticket #29, relates to RDi 9.5.
             * 
             * <pre>
             * The integrated 5250 terminal has big problems with keyboard shortcuts in RDi 9.5:
             * "5250 key bindings are applied to RSE items"
             * 1. Click an IFS item (a folder) to see the sub-folders.
             * 2. Then click back into 5250.
             * 3. Press F5.
             * 4. => you act on the IFS item
             * </pre>
             * 
             * @see biz.isphere.tn5250j.core.tn5250jpart.TN5250JPart
             */
            private void bugFixRDi95_activateView() {

                TN5250JGUI tn5250jGUI = (TN5250JGUI)event.getSource();
                ITN5250JPart workbenchPart = tn5250jGUI.getTN5250JInfo().getTN5250JPart();
                if (workbenchPart instanceof TN5250JView || workbenchPart instanceof TN5250JEditor) {

                    IWorkbenchPart activePart = getActivePart();
                    if (activePart == null || workbenchPart == activePart) {
                        /*
                         * View is already active. So we do not need to activate
                         * it again.
                         */
                        return;
                    }

                    /*
                     * Carry the control that shall get the focus to the focus
                     * listener of the "tabFolderSessions" control of call
                     * TN5250JPart.
                     */
                    tabFolderSessions.setData(TabFolderSessionsData.TARGET_FOCUS_CONTROL, tn5250jGUI);

                    /*
                     * Activate a SWT control to force Eclipse to activate the
                     * view.
                     * "PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(...);"
                     * does not work, because for unknown reasons the part is
                     * not properly deactivated again. Use: TN5250JPart,
                     * IPartListener2 for logging that.
                     */
                    tabFolderSessions.forceFocus();
                }
            }

            private IWorkbenchPart getActivePart() {
                
                IWorkbench workbench = PlatformUI.getWorkbench();
                if (workbench == null) {
                    return null;
                }
                
                IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
                if (activeWorkbenchWindow == null) {
                    return null;
                }
                
                IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
                if (activePage == null) {
                    return null;
                }
                
                IWorkbenchPart activePart = activePage.getActivePart();
                
                return activePart;
            }
        });
    }
}
