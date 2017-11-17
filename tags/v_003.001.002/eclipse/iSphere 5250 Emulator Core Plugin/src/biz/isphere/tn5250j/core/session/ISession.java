/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.tn5250j.core.session;

public interface ISession {

    public static final String SESSION_VIEW_ID = "biz.isphere.tn5250j.rse.sessionsview.SessionsView"; //$NON-NLS-1$
    public static final String DESIGNER_VIEW_ID = "biz.isphere.tn5250j.rse.designerview.DesignerView"; //$NON-NLS-1$ 

    public static final String ISPHERE_PRODUCT_LIBRARY = "%ISPHERE%";
    public static final String DESIGNER = "_DESIGNER";

    public final static String AREA_VIEW = "*VIEW"; //$NON-NLS-1$
    public final static String AREA_EDITOR = "*EDITOR"; //$NON-NLS-1$

    public final static String SIZE_132 = "132"; //$NON-NLS-1$
    public final static String SIZE_80 = "80"; //$NON-NLS-1$

    public static final String GROUP_BY_NOTHING = "GROUP_BY_NOTHING";
    public static final String GROUP_BY_CONNECTION = "GROUP_BY_CONNECTION";
    public static final String GROUP_BY_SESSION = "GROUP_BY_SESSION";

}
