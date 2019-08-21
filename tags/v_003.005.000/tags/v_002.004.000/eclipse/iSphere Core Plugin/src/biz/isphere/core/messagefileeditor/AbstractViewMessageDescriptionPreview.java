package biz.isphere.core.messagefileeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

public abstract class AbstractViewMessageDescriptionPreview extends ViewPart {

    public static final String ID = "biz.isphere.rse.messagefileeditor.ViewMessageDescriptionPreview";
    private ISelectionListener selectionListener;
    private Text tMessagePreview;

    public AbstractViewMessageDescriptionPreview() {
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

        selectionListener = registerSelectionListener(tMessagePreview);
    }

    protected abstract ISelectionListener registerSelectionListener(Text aMessagePreview);

    public void deregisterSelectionListener() {
        getSite().getPage().removeSelectionListener(selectionListener);
    }

    public void dispose() {
        deregisterSelectionListener();
    }

    @Override
    public void setFocus() {
        tMessagePreview.setFocus();
    }

}
