/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefileeditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.messagefileeditor.AbstractViewMessageDescriptionPreview;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.rse.internal.MessageFormatter;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteMessageDescription;

public class ViewMessageDescriptionPreview extends AbstractViewMessageDescriptionPreview {

    private Text messagePreview;

    protected ISelectionListener registerSelectionListener(Text aMessagePreview) {

        messagePreview = aMessagePreview;

        ISelectionListener selectionListener = new ISelectionListener() {
            MessageFormatter formatter = new MessageFormatter();

            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (!(selection instanceof IStructuredSelection)) {
                    return;
                }

                IStructuredSelection tSelection = (IStructuredSelection)selection;
                Object tItem = tSelection.getFirstElement();
                if (tItem instanceof MessageDescription) {
                    MessageDescription tMessageDescription = (MessageDescription)tItem;
                    messagePreview.setText(formatter.format(tMessageDescription));
                } else if (tItem instanceof QSYSRemoteMessageDescription) {
                    QSYSRemoteMessageDescription tMessageDescription = (QSYSRemoteMessageDescription)tItem;
                    messagePreview.setText(formatter.format(tMessageDescription));
                }
            }
        };

        getSite().getPage().addSelectionListener(selectionListener);
        
        return selectionListener;
    }

}
