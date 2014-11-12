package biz.isphere.junit;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.core.dataareaeditor.QWCRDTAA;

import com.ibm.as400.access.AS400;

public class TestQWCRDTAA {

    private static String system = System.getProperty("isphere.junit.as400");
    private static String user = System.getProperty("isphere.junit.username");
    private static String password = System.getProperty("isphere.junit.password");

    @Test
    public void testGetType() throws Exception {
        
        AS400 as400 = new AS400(system, user, password);
        
        QWCRDTAA api = new QWCRDTAA();
        String type = api.getType(as400, "ISPHEREDVP", "ISPHERE");
        
        Assert.assertEquals("*CHAR", type);
        
    }

}
