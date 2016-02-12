package biz.isphere.strpreprc.lpex;

import com.ibm.lpex.alef.LpexPreload;

/**
 * This class is a dummy that is installed in plugin.xml. All it has to do is to
 * activate the plug-in class when it is loaded.
 * 
 * @author Thomas Raddatz
 */
public class Preload implements LpexPreload {

    public Preload() {
        return;
    }

    public void preload() {

        System.out.println("*** Initialising Lpex editor ... ***");
        MenuExtension menuExtension = new MenuExtension();
        menuExtension.initializeLpexEditor();

        return;
    }
}
