package biz.isphere.rcp;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ISphereRCPExampleApplication implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		ISphereRCPExampleApplication.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		ISphereRCPExampleApplication.context = null;
	}

}
