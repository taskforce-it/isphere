/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.internal;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public final class UIHelper {

    private UIHelper() {
    }

    public static IWorkbenchPage getActivePage() {

        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return null;
        }

        IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
        if (null == activeWorkbenchWindow) {
            activeWorkbenchWindow = workbench.getWorkbenchWindows()[0];
        }

        return activeWorkbenchWindow.getActivePage();
    }

    public static IWorkbenchPart getActivePart() {

        IWorkbenchPage activePage = UIHelper.getActivePage();
        if (activePage == null) {
            return null;
        }

        return activePage.getActivePart();
    }

    public static IEditorPart getActiveEditor() {

        IWorkbenchPage activePage = getActivePage();
        if (activePage == null) {
            return null;
        }

        return activePage.getActiveEditor();
    }

    public static boolean isDarkMode() {
        MApplication application = (MApplication)PlatformUI.getWorkbench().getService(MApplication.class);
        IEclipseContext context = application.getContext();
        String defaultTheme = (String)context.get("cssTheme");
        IThemeEngine engine = (IThemeEngine)context.get(IThemeEngine.class);
        ITheme activeTheme = engine.getActiveTheme();
        if (activeTheme == null) {
            return false;
        }
        return "org.eclipse.e4.ui.css.theme.e4_dark".equals(activeTheme.getId());
    }

    public static Display getDefaultDisplay() {
        return Display.getDefault();
    }

    public static Shell getActiveShell() {

        Shell shell = null;

        if (PlatformUI.getWorkbench() != null) {
            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
                shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            }
        }

        if (shell == null) {
            if (Display.getCurrent() != null) {
                shell = Display.getCurrent().getActiveShell();
            } else {
                if (Display.getDefault() != null) {
                    shell = Display.getDefault().getActiveShell();
                }
            }
        }

        return shell;
    }

    public static void detachEditor(IEditorPart editorPart, Rectangle editorBounds) {
        detachEditor(editorPart, editorBounds.x, editorBounds.y, editorBounds.width, editorBounds.height);
    }

    public static void detachEditor(IEditorPart editorPart, int posX, int posY, int width, int height) {

        EModelService modelService = (EModelService)editorPart.getSite().getService(EModelService.class);
        MPartSashContainerElement modelPart = (MPart)editorPart.getSite().getService(MPart.class);

        if (modelPart.getCurSharedRef() != null) {
            modelPart = modelPart.getCurSharedRef();
        }

        modelService.detach(modelPart, posX, posY, width, height);
    }

}
