package biz.isphere.rse.dataqueue.rse;

import biz.isphere.core.dataqueue.rse.AbstractDataQueueMonitorView;
import biz.isphere.core.dataspaceeditordesigner.rse.AbstractDropDataObjectListerner;
import biz.isphere.core.dataspaceeditordesigner.rse.IDialogView;
import biz.isphere.core.internal.viewmanager.IViewManager;
import biz.isphere.rse.ISphereRSEPlugin;
import biz.isphere.rse.dataspaceeditor.rse.DropDataObjectListener;

import com.ibm.as400.access.AS400;
import com.ibm.etools.iseries.core.api.ISeriesConnection;
import com.ibm.etools.systems.core.messages.SystemMessageException;

public class DataQueueMonitorView extends AbstractDataQueueMonitorView {

    @Override
    protected AbstractDropDataObjectListerner createDropListener(IDialogView editor) {
        return new DropDataObjectListener(editor);
    }

    @Override
    protected IViewManager getViewManager() {
        return ISphereRSEPlugin.getDefault().getViewManager(IViewManager.DATA_QUEUE_MONITOR_VIEWS);
    }

    @Override
    protected AS400 getSystem(String connectionName) {
        
        try {
            ISeriesConnection connection = ISeriesConnection.getConnection(connectionName);
            return connection.getAS400ToolboxObject(getShell());
        } catch (SystemMessageException e) {
            return null;
        }
    }

}
