package biz.isphere.core.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteObject;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceFile;

public class QSYSRemoteObjectPropertyTester extends PropertyTester {

    public static final String PROPERTY_NAMESPACE = "biz.isphere.core.propertytester.qsysremoteobject";
    
    public static final String PROPERTY_TYPE = "type";
    
    public static final String PROPERTY_SUBTYPE = "subtype";
    
    public boolean test(Object aReceiver, String aProperty, Object[] anArgs, Object anExpectedValue) {
        
        if (!(aReceiver instanceof QSYSRemoteObject)) {
            return false;
        }

        QSYSRemoteObject sourceFile = (QSYSRemoteObject)aReceiver;

        if (anExpectedValue instanceof String) {
            String expectedValue = (String)anExpectedValue;
            if (PROPERTY_TYPE.equals(aProperty)) {
                return sourceFile.getType().equalsIgnoreCase(expectedValue);
            } else if (PROPERTY_SUBTYPE.equals(aProperty)) {
                return sourceFile.getSubType().equalsIgnoreCase(expectedValue);
            }
        }
        
        return false;
    }

}
