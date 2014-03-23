package biz.isphere.core.spooledfiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public class SpooledFileTransformerHTML extends AbstractSpooledFileTransformer {

    private BufferedWriter writer = null;
    
    private int count;

    public SpooledFileTransformerHTML(SpooledFile spooledFile) {
        super(spooledFile);
    }

    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFHTML", "WSCST");
    }

    protected void openPrinter(String target) throws IOException {
        writer = new BufferedWriter(new FileWriter(target));
        count = 0;
    }

    protected void closePrinter() throws IOException {
        if (writer != null) {
            writer.close();
            count = 0;
        }
    }

    protected void initPrinter() throws IOException {
        writer.write("<html><head><title></title></head><body><table><tr><td><pre>");
    }

    protected void resetPrinter() throws IOException {
        writer.write("</pre></td></tr></table></body></html>");
    }

    protected void formfeed() throws IOException {
        writer.write("<hr/>");
    }

    protected void newLine() throws IOException {
        writer.write(CR_LF);
    }

    protected void print(String text) throws IOException {
        if (count == 0 && text.startsWith("<INIT_PRINTER/></b></u></b></u>")) {
            writer.write(text.substring("<INIT_PRINTER/></b></u></b></u>".length()));
        } else {
            writer.write(text);
        }
        count++;
    }

}
