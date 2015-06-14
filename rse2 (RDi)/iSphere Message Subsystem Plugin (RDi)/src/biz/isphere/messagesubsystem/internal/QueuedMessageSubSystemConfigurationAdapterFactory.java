package biz.isphere.messagesubsystem.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.rse.ui.subsystems.ISubSystemConfigurationAdapter;

public class QueuedMessageSubSystemConfigurationAdapterFactory implements IAdapterFactory {

    private ISubSystemConfigurationAdapter configurationAdapter;

    public QueuedMessageSubSystemConfigurationAdapterFactory() {

        this.configurationAdapter = new QueuedMessageSubSystemConfigurationAdapter();
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {

        if ((adaptableObject instanceof QueuedMessageSubSystemFactory)) {
            return configurationAdapter;
        }

        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return new Class<?>[] { ISubSystemConfigurationAdapter.class };
    }

    public void registerWithManager(IAdapterManager manager) {

        manager.registerAdapters(this, QueuedMessageSubSystemFactory.class);
    }
}
