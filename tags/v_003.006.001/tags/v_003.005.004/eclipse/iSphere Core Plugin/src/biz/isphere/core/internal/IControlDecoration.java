/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * This interface has been added for backporting class <i>ControlDecoration</i>
 * for the <i>iSphere WDSCi Plugin</i>.
 * 
 * @see biz.isphere.rse.internal.RSEControlDecoration
 */
public interface IControlDecoration {

    public Control getControl();

    public Image getImage();

    public void setImage(Image image);

    public void hide();

    public void show();

    public void setMarginWidth(int marginWidth);

    public boolean isVisible();
}
