// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.rse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.tn5250j.TN5250JPlugin;

import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class TN5250JRSEPlugin extends AbstractUIPlugin {

	private static TN5250JRSEPlugin plugin;
	private static URL installURL;
	public static final String PLUGIN_ID = "de.taskforce.tn5250j.rse";
	public static final String IMAGE_TN5250J = "tn5250j.gif";
	public static final String IMAGE_INFO = "info.gif";
	public static final String IMAGE_NEW = "new.gif";
	public static final String IMAGE_CHANGE = "change.gif";
	public static final String IMAGE_DISPLAY = "display.gif";
	public static final String IMAGE_DELETE = "delete.gif";
	public static final String IMAGE_DESIGNER = "designer.gif";
	public static final String IMAGE_EDIT_DESIGNER = "edit_designer.gif";
	public static final String IMAGE_BROWSE_DESIGNER = "browse_designer.gif";
	
	public TN5250JRSEPlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		installURL = context.getBundle().getEntry("/");
		
		IAdapterManager manager = Platform.getAdapterManager();
		de.taskforce.tn5250j.rse.model.RSESessionAdapterFactory factory = new de.taskforce.tn5250j.rse.model.RSESessionAdapterFactory();
		manager.registerAdapters(factory, de.taskforce.tn5250j.rse.model.RSESession.class);
		
		initializePreferenceStoreDefaults();
		
		TN5250JPlugin.setTN5250JInstallation("TN5250J for RDi 8.0+ - Version 3.0.2");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static TN5250JRSEPlugin getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/"; 
		try {
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_TN5250J, getImageDescriptor(IMAGE_TN5250J));
		reg.put(IMAGE_INFO, getImageDescriptor(IMAGE_INFO));
		reg.put(IMAGE_NEW, getImageDescriptor(IMAGE_NEW));
		reg.put(IMAGE_CHANGE, getImageDescriptor(IMAGE_CHANGE));
		reg.put(IMAGE_DISPLAY, getImageDescriptor(IMAGE_DISPLAY));
		reg.put(IMAGE_DELETE, getImageDescriptor(IMAGE_DELETE));
		reg.put(IMAGE_DESIGNER, getImageDescriptor(IMAGE_DESIGNER));
		reg.put(IMAGE_EDIT_DESIGNER, getImageDescriptor(IMAGE_EDIT_DESIGNER));
		reg.put(IMAGE_BROWSE_DESIGNER, getImageDescriptor(IMAGE_BROWSE_DESIGNER));
	}
	
	protected void initializePreferenceStoreDefaults(){
	}
	
	public static URL getInstallURL() {
		return installURL;
	}

	public static String getRSEConnectionDirectory() {
		return TN5250JCorePlugin.getTN5250JPluginDirectory();
	}

	public static String getRSESessionDirectory(String connection) {
		return getRSEConnectionDirectory() + File.separator + connection;
	}
	
}
