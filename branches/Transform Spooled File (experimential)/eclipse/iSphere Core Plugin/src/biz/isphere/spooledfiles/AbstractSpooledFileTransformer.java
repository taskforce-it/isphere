package biz.isphere.spooledfiles;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import biz.isphere.ccsid.CcsidUtil;

import com.ibm.as400.access.PrintObject;
import com.ibm.as400.access.PrintParameterList;
import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public abstract class AbstractSpooledFileTransformer implements ISpooledFileTransformer {

    protected static final String CR_LF = "\r\n";

    protected static final String FF = "\f";

    private SpooledFile spooledFile;
    
    private DecimalFormat jobNumberFormat;

    public AbstractSpooledFileTransformer(SpooledFile spooledFile) {
        this.spooledFile = spooledFile;
        this.jobNumberFormat = new DecimalFormat("000000");
    }

    protected String getName() {
        return spooledFile.getName();
    }

    protected String getJob() {
        return jobNumberFormat.format(Integer.parseInt(spooledFile.getJobNumber())) + "/" + spooledFile.getJobUser() + "/" + spooledFile.getJobName();
    }
    
    protected String getUserData() {
        
        String userdata;
        try {
            userdata = spooledFile.getStringAttribute(PrintObject.ATTR_USERDATA);
        } catch(Exception e) {
            userdata = null;
        }
        
        if (userdata == null) {
            return "";
        }
        
        return userdata;
    }
    
    protected float getPageHeight() {
        
        Float length;
        try {
            length = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGELEN);
        } catch(Exception e) {
            length = null;
        }
        
        if (length == null) {
            return 66;
        }
        
        return length.floatValue();
    }

    protected float getPageWidth() {
        
        Float width;
        try {
            width = spooledFile.getFloatAttribute(PrintObject.ATTR_PAGEWIDTH);
        } catch(Exception e) {
            width = null;
        }
        
        if (width == null) {
            return 132;
        }
        
        return width.floatValue();
    }

    protected int getLPI() {
        
        Integer lpi;
        try {
            lpi = spooledFile.getIntegerAttribute(PrintObject.ATTR_LPI);
        } catch(Exception e) {
            lpi = null;
        }
        
        if (lpi == null) {
            return 6;
        }
        
        return lpi.intValue();
    }

    protected float getCPI() {
        
        Float cpi;
        try {
            cpi = spooledFile.getFloatAttribute(PrintObject.ATTR_CPI);
        } catch (Exception e) {
            cpi = null;
        }
        
        if (cpi == null) {
            return 10;
        }
        
        return cpi.floatValue();
    }

    public boolean transformSpooledFile(String target) throws Exception {

        BufferedReader reader = null;

        boolean cleanUp = false;

        try {

            QSYSObjectPathName wscst = getWorkstationCustomizationObject();

            PrintParameterList transformParameters = new PrintParameterList();
            transformParameters.setParameter(PrintObject.ATTR_WORKSTATION_CUST_OBJECT, wscst.getPath());
            transformParameters.setParameter(PrintObject.ATTR_MFGTYPE, "*WSCST");

            InputStream in = spooledFile.getTransformedInputStream(transformParameters);
            int ccsid = spooledFile.getIntegerAttribute(PrintObject.ATTR_JOBCCSID).intValue();

            CcsidUtil util = new CcsidUtil();
            String ascii = util.getAsciiCodepage(ccsid);

            if (ascii != null) {
                reader = new BufferedReader(new InputStreamReader(in, ascii));
            } else {
                reader = new BufferedReader(new InputStreamReader(in));
            }

            openPrinter(target);
            initPrinter();

            String line;
            boolean isDelayedFormfeed = false;
            while ((line = reader.readLine()) != null) {
                if (isDelayedFormfeed) {
                    formfeed();
                    isDelayedFormfeed = false;
                }
                if (line.startsWith(FF)) {
                    formfeed();
                    if (line.length() > 1) {
                        print(line.substring(1));
                        newLine();
                    }
                } else if (line.endsWith(FF)) {
                    if (line.length() > 1) {
                        print(line.substring(0, line.length() - 1));
                        newLine();
                    }
                    // Delay FF unitl the next line printed.
                    isDelayedFormfeed = true;
                } else {
                    print(line);
                    newLine();
                }
            }

            resetPrinter();

            cleanUp = true;

        } finally {
            if (reader != null) {
                reader.close();
            }

            closePrinter();
        }

        return cleanUp;
    }

    abstract protected QSYSObjectPathName getWorkstationCustomizationObject();

    abstract protected void openPrinter(String target) throws Exception;

    abstract protected void closePrinter() throws Exception;

    abstract protected void initPrinter() throws Exception;

    abstract protected void resetPrinter() throws Exception;

    abstract protected void formfeed() throws Exception;

    abstract protected void newLine() throws Exception;

    abstract protected void print(String text) throws Exception;
}
