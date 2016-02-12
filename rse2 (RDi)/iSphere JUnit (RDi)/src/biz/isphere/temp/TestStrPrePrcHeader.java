package biz.isphere.temp;

import biz.isphere.core.clcommands.CLFormatter;
import biz.isphere.strpreprc.model.StrPrePrcHeader;

import com.ibm.as400.access.AS400;

public class TestStrPrePrcHeader {

    private static String system = System.getProperty("isphere.junit.as400");
    private static String user = System.getProperty("isphere.junit.username");
    private static String password = System.getProperty("isphere.junit.password");

    public static void main(String[] args) {
        TestStrPrePrcHeader main = new TestStrPrePrcHeader();

        try {
            main.run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run(String[] args) throws Exception {

        StrPrePrcHeader header = new StrPrePrcHeader();
        if (!header.loadFromResource("biz/isphere/temp/strpreprc_test.rpgle")) {
            System.out.println("*** ERROR loading header ***");
            return;
        }

        AS400 as400 = new AS400(system, user, password);
        CLFormatter formatter = new CLFormatter(as400);
//        formatter = null;

        int count = 0;
        String[] lines = header.produceHeader(formatter);
        for (String line : lines) {
            count++;
            System.out.println(count + ":\t" + line);
        }

    }

}