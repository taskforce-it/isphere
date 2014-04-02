package biz.isphere.base;

import org.eclipse.ui.IStartup;

import biz.isphere.base.versioncheck.PluginCheck;

public class StartUp implements IStartup {

	public void earlyStartup() {

        PluginCheck.check();
		
	}
	
}

