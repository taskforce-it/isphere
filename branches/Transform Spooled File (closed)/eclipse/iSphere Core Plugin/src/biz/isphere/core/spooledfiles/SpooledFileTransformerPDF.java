package biz.isphere.core.spooledfiles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;

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
    
    private Set<PageSize> pageSizes = null;
    
    private boolean fitToPage = true;

    public SpooledFileTransformerPDF(SpooledFile spooledFile) {
        super(spooledFile);
        pageSizes = getPageSizes_DIN_A4();
    }
    
    private Set<PageSize> getPageSizes_DIN_A4() {
        TreeSet<PageSize> pageSizes_DIN_A4 = new TreeSet<PageSize>();
        if (fitToPage) {
            return pageSizes_DIN_A4; 
        }
        pageSizes_DIN_A4.add(new PageSize(1.02F, 1.46F)); // A10
        pageSizes_DIN_A4.add(new PageSize(1.46F, 2.05F)); // A9
        pageSizes_DIN_A4.add(new PageSize(2.05F, 2.91F)); // A8
        pageSizes_DIN_A4.add(new PageSize(2.91F, 4.13F)); // A7
        pageSizes_DIN_A4.add(new PageSize(4.13F, 5.83F)); // A6
        pageSizes_DIN_A4.add(new PageSize(5.83F, 8.27F)); // A5
        pageSizes_DIN_A4.add(new PageSize(8.27F, 11.69F)); // A4
        pageSizes_DIN_A4.add(new PageSize(11.69F, 16.54F)); // A3
        pageSizes_DIN_A4.add(new PageSize(16.54F, 23.39F)); // A2
        pageSizes_DIN_A4.add(new PageSize(23.39F, 33.11F)); // A1
        pageSizes_DIN_A4.add(new PageSize(33.11F, 46.81F)); // A0
        return pageSizes_DIN_A4; 
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
        
        Rectangle pageSize = selectRequiredPageSize(getPageWidthInInches(), getPageHeightInInches());
        Document pdf = new Document(pageSize, 0, 0, 0, 0);
        
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
    
    private Rectangle selectRequiredPageSize(float pageWidth, float pageHeight) {
        float x;
        float y;
        boolean isLandscape;
        if (pageWidth > pageHeight) {
            // Landscape
            y = pageWidth;
            x = pageHeight;
            isLandscape = true;
        } else {
            // Portrait
            x = pageHeight;
            y = pageWidth;
            isLandscape = false;
        }
        PageSize pageSize = null;
        for (Iterator<PageSize> iterator = pageSizes.iterator(); iterator.hasNext();) {
            PageSize curentPageSize = iterator.next();
            if (curentPageSize.getWidth() >= x && curentPageSize.getHeight() >= y) {
                pageSize = curentPageSize;
                break;
            }
        }
        
        if (pageSize == null) {
            pageSize =  new PageSize(x, y);
        }
        
        if (isLandscape) {
            return new PageSize(pageSize.getHeight(), pageSize.getWidth()).getDimension();
        } else {
            return new PageSize(pageSize.getWidth(), pageSize.getHeight()).getDimension();
        }
    }
    
    private float getPageWidthInInches() {
        return super.getPageWidth() / getCPI();
    }
    
    private float getPageHeightInInches() {
        return super.getPageHeight() / getLPI();
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
            new PdfOutline(cb.getRootOutline(), destination, Messages.getString("Page") + " " + i);
        }
    }

    private class PageSize implements Comparable<PageSize>{
        
        private Rectangle pageSize;
       
        private int DOTS_PER_INCH = 72;
        
        public PageSize(float width, float height) {
            pageSize = new Rectangle(width, height);
        }
        
        /**
         * Return the page width measured in inches.
         * @return Page width in inches.
         */
        protected float getWidth() {
            return pageSize.getRight() - pageSize.getLeft();
        }
        
        /**
         * Return the page height measured in inches.
         * @return Page height in inches.
         */
        protected float getHeight() {
            return pageSize.getTop() - pageSize.getBottom();
        }
        
        
        /**
         * Return the page dimension measured in dots.
         * @return Page dimension in dots.
         */
        protected Rectangle getDimension() {
            return new Rectangle(getWidth() * DOTS_PER_INCH + 5, getHeight() * DOTS_PER_INCH + 5);
        }

        /**
         * Compares the page width to get a list of paper sizes ordered by
         * width.
         */
        public int compareTo(PageSize pageSize) {
            if (getWidth() == pageSize.getWidth() && getHeight() == pageSize.getHeight()) {
                return 0;
            } else if (getWidth() < pageSize.getWidth()) {
                return -1;
            } else if (getWidth() > pageSize.getWidth()) {
                return 1;
            } else if (getHeight() < pageSize.getHeight()) {
                return -1;
            } else if (getHeight() > pageSize.getHeight()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    
}
