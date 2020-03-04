package biz.isphere.adapter.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.rse.core.filters.SystemFilterReference;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectBrief;

public class SystemFilterReferencesPropertyTester extends PropertyTester {

    public static final String PROPERTY_NAMESPACE = "biz.isphere.adapter.propertytester.systemfilterreference";

    public static final String PROPERTY_SUBSYSTEM = "subsystem";

    public boolean test(Object aReceiver, String aProperty, Object[] anArgs, Object anExpectedValue) {

        if (!(aReceiver instanceof SystemFilterReference)) {
            return false;
        }

        SystemFilterReference filter = (SystemFilterReference)aReceiver;
        
        if (anExpectedValue instanceof String) {
            String expectedValue = (String)anExpectedValue;
            if (PROPERTY_SUBSYSTEM.equals(aProperty)) {
                // TODO: remove me, needed only for debugging
                // System.out.println("Property-Tester (subtsystem): " + filter.getSubSystem().getClass().getName() + "=" + expectedValue);
                return expectedValue.equalsIgnoreCase(filter.getSubSystem().getClass().getName());
            }
        }

        return false;
    }

}
