package biz.isphere.junit;

import junit.framework.Assert;

import org.junit.Test;

import biz.isphere.core.dataareaeditor.QWCRDTAA;

import com.ibm.as400.access.AS400;

public class TestQWCRDTAA {

    @Test
    public void testGetType() throws Exception {
        
        AS400 as400 = new AS400("ghentw.gfd.de", "RADDATZ", "just4you");
        
        QWCRDTAA api = new QWCRDTAA();
        String type = api.getType(as400, "ISPHEREDVP", "ISPHERE");
        
        Assert.assertEquals("*CHAR", type);
        
    }

}
