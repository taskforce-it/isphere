package biz.isphere.junit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Properties;

import junit.framework.TestCase;

public class CheckNLSMessages extends TestCase {

    private String[] locales = new String[] { "en", "de", "nl" };

    public void testCoreMessages() throws Exception {
        matchMessagesWithProperties(biz.isphere.base.Messages.class, "messages{0}.properties");
        matchMessagesWithProperties(biz.isphere.core.Messages.class, "messages{0}.properties");
        matchMessagesWithProperties(biz.isphere.rse.Messages.class, "messages{0}.properties");
        matchMessagesWithProperties(biz.isphere.lpex.tasktags.Messages.class, "messages{0}.properties");
    }

    private void matchMessagesWithProperties(Class<?> clazz, String propertiesFile) throws Exception {
        for (String locale : locales) {
            // FIXME: loading Messages for locale does not work
            // Locale.setDefault(new Locale(locale));
            Object messages = getInstance(clazz);
            String resourcePath = "/" + clazz.getPackage().getName().replace('.', '/') + "/";
            String resource;
            if ("en".equalsIgnoreCase(locale)) {
                resource = resourcePath + propertiesFile.replaceAll("\\{0\\}", "");
            } else {
                resource = resourcePath + propertiesFile.replaceAll("\\{0\\}", "_" + locale);
            }
            System.out.println("Testing locale: " + resource);
            Properties properties = new Properties();
            properties.load(messages.getClass().getResourceAsStream(resource));
            matchMessagesWithPropertiesFile(messages, properties, new Locale(locale));
        }
    }

    private void matchMessagesWithPropertiesFile(Object messages, Properties properties, Locale currentLocale) throws IllegalAccessException {
        Field[] fields = messages.getClass().getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            System.out.println("  " + fieldName);
            String text = properties.getProperty(fieldName);
            if (currentLocale.getCountry().equalsIgnoreCase(Locale.getDefault().getCountry())) {
                assertEquals(text, field.get(null));
            } else {
                assertNotNull(text);
                if (text != null) {
                    assertTrue("Length of message must be greater than zero.", text.length() > 0);
                }
            }
            properties.remove(fieldName);
        }
        assertEquals(0, properties.size());
    }

    private Object getInstance(Class<?> aClass) throws Exception, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = aClass.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        Object object = constructor.newInstance(new Object[0]);
        return object;
    }
}
