package biz.isphere.rse.dataspaceeditor.rse;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.internal.RemoteObject;
import biz.isphere.rse.dataspacemonitor.rse.DataSpaceMonitorView;

import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;

public abstract class AbstractMonitorDataSpaceAction implements IObjectActionDelegate {

    private IStructuredSelection structuredSelection;
    private String objectType;

    public AbstractMonitorDataSpaceAction(String objectType) {
        this.objectType = objectType;
    }

    public void run(IAction action) {

        if (structuredSelection != null && !structuredSelection.isEmpty()) {
            Iterator<?> iterator = structuredSelection.iterator();

            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (matchesType(object, objectType)) {
                    IWorkbenchWindow window = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    if (window != null) {
                        IWorkbenchPage page = window.getActivePage();
                        if (page != null) {
                            QSYSRemoteObject qsysRemoteObject = (QSYSRemoteObject)object;
                            openMonitorForObject(qsysRemoteObject, page);
                        }
                    }
                }
            }
        }
    }

    protected void openMonitorForObject(QSYSRemoteObject qsysRemoteObject, IWorkbenchPage page) {
        try {

            page.showView(DataSpaceMonitorView.ID, null, IWorkbenchPage.VIEW_CREATE);
            IViewPart justActivated = page.showView(DataSpaceMonitorView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
            if (justActivated instanceof IDialogView) {

                String connection = IBMiConnection.getConnection(getHost(qsysRemoteObject)).getConnectionName();
                String name = qsysRemoteObject.getName();
                String library = qsysRemoteObject.getLibrary();
                String type = qsysRemoteObject.getType();
                String description = qsysRemoteObject.getDescription();
                RemoteObject remoteObject = new RemoteObject(connection, name, library, type, description);

                ((IDialogView)justActivated).setData(new RemoteObject[] { remoteObject });
            }

        } catch (Exception e) {
            ISpherePlugin.logError(e.getMessage(), e);
            MessageDialog.openError(getShell(), Messages.E_R_R_O_R, e.getLocalizedMessage());
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {

        if (selection instanceof IStructuredSelection) {
            structuredSelection = ((IStructuredSelection)selection);
        } else {
            structuredSelection = null;
        }
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        // nothing to do here
    }

    private boolean matchesType(Object object, String objectType) {
        if ((object instanceof IQSYSResource)) {
            IQSYSResource element = (IQSYSResource)object;
            if (ResourceTypeUtil.isObject(element) && element instanceof QSYSRemoteObject) {
                QSYSRemoteObject qsysObject = (QSYSRemoteObject)element;
                if (objectType.equals(qsysObject.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private IHost getHost(QSYSRemoteObject object) {
        IHost host = object.getRemoteObjectContext().getObjectSubsystem().getHost();
        return host;
    }

    private Shell getShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

}
