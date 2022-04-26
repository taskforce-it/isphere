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

package de.taskforce.tn5250j.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.tn5250j.TN5250JPlugin;

public class TN5250JCorePlugin extends AbstractUIPlugin {

	private static TN5250JCorePlugin plugin;
	private static URL installURL;
	public static final String PLUGIN_ID = "de.taskforce.tn5250j.core";
	public static final String BASIC = "XO498BDE993DET";
	public static final String IMAGE_CMONE = "cmone.bmp";
	public static final String IMAGE_TASKFORCE = "TaskForce.bmp";
	public static final String IMAGE_ERROR = "error.gif";
	public static final String IMAGE_INFO = "info.gif";
	public static final String IMAGE_TN5250JSPLASH = "tn5250jSplash.jpg";
	public static final String IMAGE_PLUS = "plus.gif";
	public static final String IMAGE_MINUS = "minus.gif";
	public static final String IMAGE_ON = "on.gif";
	public static final String IMAGE_OFF = "off.gif";

	public TN5250JCorePlugin() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		installURL = context.getBundle().getEntry("/");
		
		String directory1 = getTN5250JHomeDirectory();
		File directory1TN5250J = new File(directory1);
		if (!directory1TN5250J.exists()) {
			directory1TN5250J.mkdir();
		}
		
		String directory2 = getTN5250JPluginDirectory();
		File directory2TN5250J = new File(directory2);
		if (!directory2TN5250J.exists()) {
			directory2TN5250J.mkdir();
		}
		
		initializePreferenceStoreDefaults();
		
		TN5250JPlugin.setTN5250JInstallation("TN5250J Core - Version 3.0.2");
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static TN5250JCorePlugin getDefault() {
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
		reg.put(IMAGE_CMONE, getImageDescriptor(IMAGE_CMONE));
		reg.put(IMAGE_TASKFORCE, getImageDescriptor(IMAGE_TASKFORCE));
		reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
		reg.put(IMAGE_INFO, getImageDescriptor(IMAGE_INFO));
		reg.put(IMAGE_TN5250JSPLASH, getImageDescriptor(IMAGE_TN5250JSPLASH));
		reg.put(IMAGE_PLUS, getImageDescriptor(IMAGE_PLUS));
		reg.put(IMAGE_MINUS, getImageDescriptor(IMAGE_MINUS));
		reg.put(IMAGE_ON, getImageDescriptor(IMAGE_ON));
		reg.put(IMAGE_OFF, getImageDescriptor(IMAGE_OFF));
	}
	
	protected void initializePreferenceStoreDefaults(){
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.PORT", "23");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.CODEPAGE", "");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.SCREENSIZE", "132");
	//	getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.ENHANCEDMODE", "Y");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.AREA", "*VIEW");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.MSACTIVE", "N");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.MSHSIZE", "0");
		getPreferenceStore().setDefault("DE.TASKFORCE.TN5250J.MSVSIZE", "0");
	}
	
	public static URL getInstallURL() {
		return installURL;
	}
	
	public static String getTN5250JHomeDirectory() {
		return System.getProperty("user.home") + File.separator + ".tn5250j";
	}
	
	public static String getTN5250JPluginDirectory() {
		return getTN5250JHomeDirectory() + File.separator + "de.taskforce.tn5250j";
	}
	
}
