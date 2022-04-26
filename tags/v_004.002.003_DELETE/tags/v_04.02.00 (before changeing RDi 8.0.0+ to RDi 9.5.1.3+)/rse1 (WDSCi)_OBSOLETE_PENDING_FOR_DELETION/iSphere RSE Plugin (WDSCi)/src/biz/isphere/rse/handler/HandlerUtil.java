/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ExecutionException;

public class HandlerUtil {

    public static IWorkbenchSite getActiveSite(ExecutionEvent event) {
        Object o = HandlerUtil.getVariable(event, "activeSite");
        if (o instanceof IWorkbenchSite) {
            return (IWorkbenchSite) o;
        }
        return null;
    }

    public static Shell getActiveShell(ExecutionEvent event) {
        Object o = HandlerUtil.getVariable(event, "activeShell");
        if (o instanceof Shell) {
            return (Shell) o;
        }
        return null;
    }

//    public static Shell getActiveShellChecked(ExecutionEvent event) throws ExecutionException {
//        Object o = HandlerUtil.getVariableChecked(event, "activeShell");
//        if (!(o instanceof Shell)) {
//            HandlerUtil.incorrectTypeFound(event, "activeShell", Shell.class, o.getClass());
//        }
//        return (Shell) o;
//    }
    
    public static String getActivePartId(ExecutionEvent event) {
        Object o = HandlerUtil.getVariable(event, "activePartId");
        if (o instanceof String) {
            return (String) o;
        }
        return null;
    }

    public static ISelection getCurrentSelection(ExecutionEvent event) {
        Object o = HandlerUtil.getVariable(event, "selection");
        if (o instanceof ISelection) {
            return (ISelection)o;
        }
        return null;
    }

    public static Object getVariableChecked(ExecutionEvent event, String name) throws ExecutionException {
        Object o = HandlerUtil.getVariable(event, name);
        if (o == null) {
            HandlerUtil.noVariableFound(event, name);
        }
        return o;
    }

    public static Object getVariable(ExecutionEvent event, String name) {
        if (event.getApplicationContext() instanceof EvaluationContext) {
            Object var = ((EvaluationContext)event.getApplicationContext()).getVariable(name);
            return var == null ? null : var;
        }
        return null;
    }
    
    private static void noVariableFound(ExecutionEvent event, String name) throws ExecutionException {
        throw new ExecutionException("No " + name + " found while executing " + event.getCommand().getId(), null);
    }

    private static void incorrectTypeFound(ExecutionEvent event, String name, Class expectedType, Class wrongType)
            throws ExecutionException {
        throw new ExecutionException(
                "Incorrect type for " + name + " found while executing " + event.getCommand().getId() + ", expected "
                        + expectedType.getName() + " found " + wrongType.getName(), null);
    }
}
