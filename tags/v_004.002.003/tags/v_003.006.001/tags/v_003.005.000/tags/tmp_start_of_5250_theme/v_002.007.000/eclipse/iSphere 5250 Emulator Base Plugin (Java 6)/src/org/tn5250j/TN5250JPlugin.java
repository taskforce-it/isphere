// ISPHERE - NEW - START

package org.tn5250j;

import java.util.ArrayList;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class TN5250JPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.tn5250j";
	private static TN5250JPlugin plugin;
	private static ArrayList<String> tn5250jInstallations = new ArrayList<String>();

	public TN5250JPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		TN5250JPlugin.setTN5250JInstallation("TN5250J - Version 0.7.5.2");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static TN5250JPlugin getDefault() {
		return plugin;
	}
	
	public static ArrayList getTN5250JInstallations() {
		return tn5250jInstallations;
	}
	
	public static void setTN5250JInstallation(String installation) {
		tn5250jInstallations.add(installation);
	}

}

//ISPHERE - NEW - END