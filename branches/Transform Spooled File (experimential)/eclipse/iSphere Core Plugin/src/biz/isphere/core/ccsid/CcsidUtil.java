package biz.isphere.core.ccsid;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Properties;

public final class CcsidUtil {

    private Properties map;
    private DecimalFormat ccsidFormat = new DecimalFormat("##000");

    public String getAsciiCodepage(int ebcdic) {
        return getEbcdicAsciiMap().getProperty(ccsidFormat.format(ebcdic));
    }
    
    private Properties getEbcdicAsciiMap() {
        if (map != null) {
            return map;
        }

        InputStream reader = null;

        try {
            map = new Properties();
            reader = getClass().getClassLoader().getResourceAsStream("biz/isphere/core/ccsid/ebcdicAsciiMapping.txt");
            map.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return map;
    }

}