package biz.isphere.base.versioncheck;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import biz.isphere.base.ISphereBasePlugin;
import biz.isphere.base.Messages;

public final class PluginCheck {
    
    private PluginCheck() {
    }

    public static boolean check() {
        PluginCheck checker = new PluginCheck();
        final List<Bundle> tIllegalBundles = checker.verifyInstalledBundles();
        if (tIllegalBundles.size() == 0) {
            return false;
        } else {
            new UIJob("") {
                public IStatus runInUIThread(IProgressMonitor arg0) {
                    Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    MessageBox tMessageBox = new MessageBox(parent, SWT.ICON_WARNING);
                    tMessageBox.setText(Messages.Illegal_Bundles_Warning_Message_Text);
                    tMessageBox.setMessage(Messages.Illegal_Bundles_Warning_Message_Message + bundlesAsList(tIllegalBundles));
                    tMessageBox.open();
                    return Status.OK_STATUS;
                }

                private String bundlesAsList(List<Bundle> anIllegalBundles) {
                    StringBuilder tList = new StringBuilder();
                    for (Bundle tBundle : anIllegalBundles) {
                        tList.append("\n");
                        tList.append(tBundle.getSymbolicName());
// TODO: get version for Eclipse 3.2 
//                        tList.append(" (");
//                        tList.append(tBundle.getVersion());
//                        tList.append(")");
                    }
                    return tList.toString();
                }
            }.schedule();
            return true;
        }
    }
    
    private List<Bundle> verifyInstalledBundles() {
        List<Bundle> tIllegalBundles = new ArrayList<Bundle>();
        
        checkAndAddIllegalBundle(tIllegalBundles, "de.taskforce.isphere");
        checkAndAddIllegalBundle(tIllegalBundles, "de.taskforce.isphere.rse");
   
// Does not work for Eclipse 3.2
//        BundleContext tContext = FrameworkUtil.getBundle(ISphereBasePlugin.class).getBundleContext();
//        String tName;
//        for (Bundle tBundle : tContext.getBundles()) {
//            tName = tBundle.getSymbolicName();
//            if (null != tName && tName.toLowerCase().startsWith("de.taskforce")) {
//                tIllegalBundles.add(tBundle);
//            }
//        }

        return tIllegalBundles;
    }

	private void checkAndAddIllegalBundle(List<Bundle> anIllegalBundles, String aBundleID) {
		Bundle bundle = Platform.getBundle(aBundleID);
		if (bundle != null) {
			anIllegalBundles.add(bundle);
		}
	}
    
}
