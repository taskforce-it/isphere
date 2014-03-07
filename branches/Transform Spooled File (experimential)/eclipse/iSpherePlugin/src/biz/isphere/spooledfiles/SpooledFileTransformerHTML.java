package biz.isphere.spooledfiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biz.isphere.ISpherePlugin;

import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public class SpooledFileTransformerHTML extends AbstractSpooledFileTransformer {

    private BufferedWriter writer = null;

    public SpooledFileTransformerHTML(SpooledFile spooledFile) {
        super(spooledFile);
    }

    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFHTML", "WSCST");
    }

    protected void openPrinter(String target) throws IOException {
        writer = new BufferedWriter(new FileWriter(target));
    }

    protected void closePrinter() throws IOException {
        if (writer != null) {
            writer.close();
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
        writer.write(text);
    }

}
