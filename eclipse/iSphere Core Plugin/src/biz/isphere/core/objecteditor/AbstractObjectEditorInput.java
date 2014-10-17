package biz.isphere.core.objecteditor;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.AS400;

public abstract class AbstractObjectEditorInput implements IEditorInput, IObjectEditor {

    private AS400 as400;
    private String connection;
    private String library;
    private String objectName;
    private String objectType;
    private String mode;
    private Image titleImage;

    public AbstractObjectEditorInput(AS400 anAS400, String aConnection, String aLibrary, String anObjectName, String anObjectType, String aMode,
        String anImageID) {
        as400 = anAS400;
        connection = aConnection;
        library = aLibrary;
        objectName = anObjectName;
        objectType = anObjectType;
        mode = aMode;
        titleImage = ISpherePlugin.getDefault().getImageRegistry().get(anImageID);
    }

    public AS400 getAS400() {
        return as400;
    }

    public String getConnection() {
        return connection;
    }

    public String getObjectLibrary() {
        return library;
    }

    public String getObjectName() {
        return objectName;
    }

    public Image getTitleImage() {
        return titleImage;
    }

    public String getName() {
        return getObjectName() + "." + objectType;
    }

    public String getToolTipText() {
        return "\\\\" + getConnection() + "\\QSYS.LIB\\" + getObjectLibrary() + ".LIB\\" + getObjectName() + "." + objectType;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public int hashCode() {
        /*
         * Siehe: http://www.ibm.com/developerworks/library/j-jtp05273/
         */
        int hash = 3;
        hash = hash * 17 + connection.hashCode();
        hash = hash * 17 + library.hashCode();
        hash = hash * 17 + objectName.hashCode();
        return hash;

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IObjectEditor other = (IObjectEditor)obj;
        if (!getConnection().equals(other.getConnection()) || !getObjectLibrary().equals(other.getObjectLibrary())
            || !getObjectName().equals(other.getObjectName())) return false;
        return true;
    }

}
