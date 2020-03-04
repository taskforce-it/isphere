package biz.isphere.core.spooledfiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import biz.isphere.core.ISpherePlugin;

import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;

public class SpooledFileTransformerText extends AbstractSpooledFileTransformer {

    private BufferedWriter writer = null;

    public SpooledFileTransformerText(SpooledFile spooledFile) {
        super(spooledFile);
    }

    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFTXT", "WSCST");
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
    }

    protected void resetPrinter() throws IOException {
    }

    protected void formfeed() throws IOException {
        writer.write("- New Page -");
        newLine();
    }

    protected void newLine() throws IOException {
        writer.write(CR_LF);
    }

    protected void print(String text) throws IOException {
        writer.write(text);
    }

}
