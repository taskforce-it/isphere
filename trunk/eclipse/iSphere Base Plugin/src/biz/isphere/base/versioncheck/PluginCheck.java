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

import biz.isphere.base.Messages;

public final class PluginCheck implements IObsoleteBundles {
    
    private PluginCheck() {
    }

    public static void check() {
        PluginCheck tChecker = new PluginCheck();
        tChecker.performBundleCheck();
    }

    private void performBundleCheck() {
        final List<Bundle> tObsoleteBundles = verifyInstalledBundles();
        if (tObsoleteBundles.size() == 0) {
            return;
        }
        
        new UIJob("OBSOLETE_BUNDLES_WARNING") {
            public IStatus runInUIThread(IProgressMonitor arg0) {
                Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                MessageBox tMessageBox = new MessageBox(parent, SWT.ICON_WARNING);
                tMessageBox.setText(Messages.Obsolete_Bundles_Warning_Message_Text);
                tMessageBox.setMessage(Messages.Obsolete_Bundles_Warning_Message_Message + bundlesAsList(tObsoleteBundles));
                tMessageBox.open();
                return Status.OK_STATUS;
            }

            private String bundlesAsList(List<Bundle> anObsoleteBundles) {
                StringBuilder tList = new StringBuilder();
                for (Bundle tBundle : anObsoleteBundles) {
                    tList.append("\n");
                    tList.append(tBundle.getSymbolicName());
// TODO: get version for Eclipse 3.2 
//                  tList.append(" (");
//                  tList.append(tBundle.getVersion());
//                  tList.append(")");
                }
                return tList.toString();
            }
        }.schedule();
    }
    
    private List<Bundle> verifyInstalledBundles() {
        List<Bundle> tObsoleteBundles = new ArrayList<Bundle>();
        
        checkAndAddObsoleteBundle(tObsoleteBundles, DE_TASKFORCE_ISPHERE);
        checkAndAddObsoleteBundle(tObsoleteBundles, DE_TASKFORCE_ISPHERE_RSE);
   
// Does not work for Eclipse 3.2
//        BundleContext tContext = FrameworkUtil.getBundle(ISphereBasePlugin.class).getBundleContext();
//        String tName;
//        for (Bundle tBundle : tContext.getBundles()) {
//            tName = tBundle.getSymbolicName();
//            if (null != tName && tName.toLowerCase().startsWith("de.taskforce")) {
//                tObsoleteBundles.add(tBundle);
//            }
//        }

        return tObsoleteBundles;
    }

	private void checkAndAddObsoleteBundle(List<Bundle> anObsoleteBundles, String aBundleID) {
		Bundle bundle = Platform.getBundle(aBundleID);
		if (bundle != null) {
			anObsoleteBundles.add(bundle);
		}
	}
    
}
