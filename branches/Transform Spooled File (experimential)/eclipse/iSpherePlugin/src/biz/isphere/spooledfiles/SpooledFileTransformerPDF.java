package biz.isphere.spooledfiles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import biz.isphere.ISpherePlugin;
import biz.isphere.Messages;

import com.ibm.as400.access.QSYSObjectPathName;
import com.ibm.as400.access.SpooledFile;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class SpooledFileTransformerPDF extends AbstractSpooledFileTransformer {

    private Document document = null;

    private Font font = null;

    public SpooledFileTransformerPDF(SpooledFile spooledFile) {
        super(spooledFile);
    }

    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFPDF", "WSCST");
    }

    protected void openPrinter(String target) throws FileNotFoundException, DocumentException {
        document = createPFD(target);
    }

    protected void closePrinter() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    /**
     * Adds the document meta data.
     */
    protected void initPrinter() throws IOException {
        addMetaData(document);
    }

    private Document createPFD(String aPath) throws FileNotFoundException, DocumentException {
        
        font = new Font(Font.FontFamily.COURIER, getFontSize(), Font.NORMAL, BaseColor.BLACK);
        
        Rectangle pagesize = new Rectangle(getPageWidth(), getPageHeight());
        Document pdf = new Document(pagesize, 0, 0, 0, 0);
        
        PdfWriter writer = PdfWriter.getInstance(pdf, new FileOutputStream(aPath));
        writer.setInitialLeading(72 / getLPI());
        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
        writer.setPageEvent(new PageEventHandler());

        pdf.open();
        return pdf;
    }
    
    private float getFontSize() {
        return 120 / getCPI();
    }
    
    protected float getPageWidth() {
        return super.getPageWidth() / getCPI() * 72;
    }
    
    protected float getPageHeight() {
        return super.getPageHeight() / getLPI() * 72;
    }

    private void addMetaData(Document aPDF) {
        aPDF.addTitle("Spooled file: " + getName());
        aPDF.addAuthor("Job: " + getJob());
        aPDF.addSubject("User data: " + getUserData());
        aPDF.addCreator(getCreator());
    }

    private String getCreator() {
        return ISpherePlugin.getDefault().getName() + " v" + ISpherePlugin.getDefault().getVersion(); 
    }

    private void startNewPage(Document aPDF) throws DocumentException {
        aPDF.newPage();
    }

    protected void resetPrinter() throws IOException {
    }

    protected void formfeed() throws DocumentException {
        startNewPage(document);
    }

    protected void newLine() throws DocumentException {
        document.add(Chunk.NEWLINE);
    }

    protected void print(String text) throws DocumentException {
        document.add(new Chunk(text, font));
    }
    
    private class PageEventHandler extends PdfPageEventHelper {
        private int i;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            super.onEndPage(writer, document);
            i++;
            PdfContentByte cb = writer.getDirectContent();
            PdfDestination destination = new PdfDestination(PdfDestination.FITH);
            new PdfOutline(cb.getRootOutline(), destination, Messages.getString("Page") + i);
        }
    }

}
