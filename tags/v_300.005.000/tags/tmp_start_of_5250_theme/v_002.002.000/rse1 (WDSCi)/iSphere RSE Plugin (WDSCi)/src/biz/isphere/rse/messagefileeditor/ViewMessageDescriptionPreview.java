package biz.isphere.rse.messagefileeditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import biz.isphere.core.messagefileeditor.AbstractViewMessageDescriptionPreview;
import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.rse.internal.MessageFormatter;

import com.ibm.etools.iseries.comm.interfaces.IISeriesMessageDescription;

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
                } else if (tItem instanceof IISeriesMessageDescription) {
                    IISeriesMessageDescription tMessageDescription = (IISeriesMessageDescription)tItem;
                    messagePreview.setText(formatter.format(tMessageDescription));
                }
            }
        };

        getSite().getPage().addSelectionListener(selectionListener);
        
        return selectionListener;
    }

}
