package biz.isphere.rse.messagefileeditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ibm.etools.iseries.comm.interfaces.IISeriesMessageDescription;

import biz.isphere.core.messagefileeditor.MessageDescription;
import biz.isphere.rse.internal.MessageFormatter;

public class ViewMessageDescriptionPreview extends ViewPart {

    public static final String ID = "biz.isphere.rse.messagefileeditor.ViewMessageDescriptionPreview";
    private ISelectionListener selectionListener;
    private Text tMessagePreview;

    public ViewMessageDescriptionPreview() {
    }

    @Override
    public void createPartControl(Composite parent) {

        ScrolledComposite tScrollable = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        tScrollable.setLayout(new GridLayout(1, false));
        tScrollable.setLayoutData(new GridData(GridData.FILL_BOTH));
        tScrollable.setExpandHorizontal(true);
        tScrollable.setExpandVertical(true);
        tScrollable.setAlwaysShowScrollBars(true);

        Composite tTextArea = new Composite(tScrollable, SWT.NONE);
        tTextArea.setLayout(new FillLayout());
        tScrollable.setContent(tTextArea);

        tMessagePreview = new Text(tTextArea, SWT.MULTI);
        tMessagePreview.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));

        tScrollable.setMinSize(tTextArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        tTextArea.layout(true);

        registerSelectionListener();
    }

    private void registerSelectionListener() {

        selectionListener = new ISelectionListener() {
            MessageFormatter formatter = new MessageFormatter();

            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (!(selection instanceof IStructuredSelection)) {
                    return;
                }

                IStructuredSelection tSelection = (IStructuredSelection)selection;
                Object tItem = tSelection.getFirstElement();
                if (tItem instanceof MessageDescription) {
                    MessageDescription tMessageDescription = (MessageDescription)tItem;
                    tMessagePreview.setText(formatter.format(tMessageDescription));
                } else if (tItem instanceof IISeriesMessageDescription) {
                    IISeriesMessageDescription tMessageDescription = (IISeriesMessageDescription)tItem;
                    tMessagePreview.setText(formatter.format(tMessageDescription));
                }
            }
        };

        getSite().getPage().addSelectionListener(selectionListener);
    }

    public void dispose() {
        getSite().getPage().removeSelectionListener(selectionListener);
    }

    @Override
    public void setFocus() {
    }

}
