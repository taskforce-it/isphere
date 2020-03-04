package biz.isphere.adapter.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.rse.core.model.IHost;

public class IHostPropertyTester extends PropertyTester {

    public static final String PROPERTY_NAMESPACE = "biz.isphere.adapter.propertytester.ihost";

    public static final String PROPERTY_ID = "id";

    public IHostPropertyTester() {
        return;
    }

    public boolean test(Object aReceiver, String aProperty, Object[] anArgs, Object anExpectedValue) {

        if (!(aReceiver instanceof IHost)) {
            return false;
        }

        IHost host = (IHost)aReceiver;

        if (anExpectedValue instanceof String) {
            String expectedValue = (String)anExpectedValue;
            if (PROPERTY_ID.equals(aProperty)) {
                String id = host.getSystemType().getId();
                return expectedValue.equals(id);
            }
        }

        return false;
    }

}
