/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import biz.isphere.base.internal.StringHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;

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

    // Unprintable margin, roughly 0.4F = 1cm
    private static final float UNPRINTABLE_MARGIN = 0.4F;

    private int DOTS_PER_INCH = 72;

    private Document document = null;
    private Font font = null;
    private Set<PageSize> pageSizes = null;
    private boolean fitToPage = false;
    private String leftMargin;

    public SpooledFileTransformerPDF(String connectionName, SpooledFile spooledFile) {
        super(connectionName, spooledFile);

        if (PageSize.PAGE_SIZE_CALCULATE.equals(Preferences.getInstance().getSpooledFilePageSize())) {
            fitToPage = false;
        } else {
            fitToPage = true;
        }
        
        pageSizes = getPageSizes_Portrait();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the workstation customization object that is used for spooled
     * file conversion to PDF.
     */
    @Override
    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(getISphereLibrary(), "SPLFPDF", "WSCST");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Produces an empty PDF document.
     */
    @Override
    protected void openPrinter(String target) throws FileNotFoundException, DocumentException {
        document = createPFD(target);
        skipToStartOfPage();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the PDF document.
     */
    @Override
    protected void closePrinter() throws IOException {
        if (document != null) {
            document.close();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Adds the PDF meta data.
     */
    @Override
    protected void initPrinter() throws IOException {
        addMetaData(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetPrinter() throws IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void formfeed() throws DocumentException {
        document.newPage();
        skipToStartOfPage();
    }

    private void skipToStartOfPage() throws DocumentException {
        // Unprintable margin, roughly 0.4F = 1cm
        int lineCount = (int)round(getLPI() * (document.topMargin()));
        while (lineCount > 0) {
            document.add(Chunk.NEWLINE);
            lineCount--;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void newLine() throws DocumentException {
        document.add(Chunk.NEWLINE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void print(String text) throws DocumentException {

        if (leftMargin == null) {
            // Unprintable margin, roughly 0.4F = 1cm
            int marginCount = (int)round(getCPI() * (document.leftMargin()));
            leftMargin = StringHelper.getFixLength("", marginCount);
        }

        Chunk chunk = new Chunk(leftMargin + text, font);
        document.add(chunk);
    }

    /**
     * Returns a list of DIN A4 page sizes.
     * <p>
     * The list is empty for <code>fitToPage</code> set to <code>false</code>.
     * 
     * @return
     */
    private Set<PageSize> getPageSizes_Portrait() {

        if (fitToPage) {

            PageSize fitToPageSize = null;

            String pageSizeId = Preferences.getInstance().getSpooledFilePageSize();
            if (!StringHelper.isNullOrEmpty(pageSizeId)) {
                fitToPageSize = findPageSize(pageSizeId);
            }

            if (fitToPageSize == null) {
                fitToPageSize = new PageSize(595.0F, 842.0F, PageSize.PAGE_SIZE_A4);
            }

            TreeSet<PageSize> pageSizes = new TreeSet<PageSize>();
            pageSizes.add(fitToPageSize); // A4
            return pageSizes;
        }

        return getPageSizes();
    }

    private PageSize findPageSize(String pageSizeId) {

        Set<PageSize> pageSizes = getPageSizes();
        for (PageSize currentPageSize : pageSizes) {
            if (currentPageSize.getFormat().equals(pageSizeId)) {
                return currentPageSize;
            }
        }

        return null;
    }

    public static Set<PageSize> getPageSizes() {

        Set<PageSize> pageSizes = new TreeSet<PageSize>();

        pageSizes.add(new PageSize(73.0F, 105.0F, "DIN A10")); // 1.02"  1.46"
        pageSizes.add(new PageSize(105.0F, 148.0F, "DIN A9")); // 1.46", 2.05"
        pageSizes.add(new PageSize(148.0F, 210.0F, "DIN A8")); // 2.05", 2.91"
        pageSizes.add(new PageSize(210.0F, 297.0F, "DIN A7")); // 2.91", 4.13"
        pageSizes.add(new PageSize(297.0F, 420.0F, "DIN A6")); // 4.13", 5.83"
        pageSizes.add(new PageSize(420.0F, 595.0F, "DIN A5")); // 5.83", 8.27"
        pageSizes.add(new PageSize(595.0F, 842.0F, PageSize.PAGE_SIZE_A4)); // 8.27", 11.69"
        pageSizes.add(new PageSize(842.0F, 1191.0F, "DIN A3")); // 11.69", 16.54"
        pageSizes.add(new PageSize(1191.0F, 1684.0F, "DIN A2")); // 16.54", 23.39"
        pageSizes.add(new PageSize(1684.0F, 2384.0F, "DIN A1")); // 23.39", 33.11"
        pageSizes.add(new PageSize(2384.0F, 3370.0F, "DIN A0")); // 33.11", 46.81"

        return pageSizes;
    }

    /**
     * Produces an empty PDF document when printing starts.
     * 
     * @param aPath - path to the PDF file
     * @return empty PDF document.
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    private Document createPFD(String aPath) throws FileNotFoundException, DocumentException {

        Rectangle pageSize = findRequiredPageSize(getPageWidthInInches(), getPageHeightInInches());
        PageMargins pageMargins = getPageMargins();
        font = new Font(Font.FontFamily.COURIER, getFontSize(pageSize, pageMargins), Font.NORMAL, BaseColor.BLACK);

        Document pdf = new Document();

        PdfWriter writer = PdfWriter.getInstance(pdf, new FileOutputStream(aPath));
        pdf.setPageSize(pageSize);
        pdf.setMargins(pageMargins.getLeft(), pageMargins.getRight(), pageMargins.getTop(), pageMargins.getBottom());
        pdf.setMarginMirroring(true);

        writer.setInitialLeading(DOTS_PER_INCH / getLPI());
        writer.setInitialLeading(round((font.getSize() * 1.1F), 1));
        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
        writer.setPageEvent(new PageEventHandler());

        pdf.open();

        return pdf;
    }

    /**
     * Returns the font size measured in dots for a given CPI value.
     * <pre>
     * Relationship between font size and average character width:
     * Courier 8, width = 7, factor = 1,143
     * Courier 9, width = 7, factor = 1,286
     * Courier 10, width = 8, factor = 1,25
     * Courier 11, width = 9, factor = 1,22
     * Courier 12, width = 10, factor = 1,2
     * Courier 13, width = 10, factor = 1,3
     * Courier 14, width = 11, factor = 1,273
     * Courier 15, width = 12, factor = 1,25
     * Courier 16, width = 13, factor = 1,231
     * Courier 18, width = 14, factor = 1,286
     * </pre>
     * 
     * @return font size
     * @see https://support.microsoft.com/en-us/kb/76388
     */
    private float getFontSize(Rectangle pageSize, PageMargins pageMargins) {

        float charWidth = (pageSize.getWidth() - (pageMargins.left + pageMargins.right)) / getPageWidth();
        float charHeight = round(charWidth * 1.25F, 1);
        
        return charHeight;
    }

    private PageMargins getPageMargins() {
        return new PageMargins(UNPRINTABLE_MARGIN, UNPRINTABLE_MARGIN, UNPRINTABLE_MARGIN, UNPRINTABLE_MARGIN);
    }

    /**
     * Computes the page size for a given page dimension. Height and width must
     * be passed in inches.
     * 
     * @param pageWidth - page width
     * @param pageHeight - page height
     * @return page dimension
     */
    private Rectangle findRequiredPageSize(float pageWidthInInch, float pageHeightInInch) {

        boolean isLandscape;
        if (pageWidthInInch > pageHeightInInch) {
            // Landscape
            isLandscape = true;
        } else {
            // Portrait
            isLandscape = false;
        }

        if (fitToPage) {
            PageSize requestedPageSize = pageSizes.iterator().next();
            if (isLandscape) {
                requestedPageSize = new PageSize(requestedPageSize.getHeight(), requestedPageSize.getWidth(), requestedPageSize.getFormat());
            }
            return requestedPageSize.getDimension();
        }

        float pageHeight = round(pageHeightInInch * DOTS_PER_INCH);
        float pageWidth = round(pageWidthInInch * DOTS_PER_INCH);

        if (isLandscape) {
            pageHeight = round(pageWidthInInch * DOTS_PER_INCH);
            pageWidth = round(pageHeightInInch * DOTS_PER_INCH);
        } else {
            pageHeight = round(pageHeightInInch * DOTS_PER_INCH);
            pageWidth = round(pageWidthInInch * DOTS_PER_INCH);
        }

        PageSize pageSize = null;
        for (Iterator<PageSize> iterator = pageSizes.iterator(); iterator.hasNext();) {
            PageSize curentPageSize = iterator.next();
            if (curentPageSize.getWidth() >= pageWidth && curentPageSize.getHeight() >= pageHeight) {
                pageSize = curentPageSize;
                break;
            }
        }

        if (pageSize == null) {
            return new PageSize(pageWidth, pageHeight, "*USRDEF").getDimension();
        } else {
            if (isLandscape) {
                return new PageSize(pageSize.getHeight(), pageSize.getWidth(), pageSize.getFormat()).getDimension();
            } else {
                return new PageSize(pageSize.getWidth(), pageSize.getHeight(), pageSize.getFormat()).getDimension();
            }
        }
    }

    /**
     * Returns the page width in inches for the actual CPI value of the
     * document.
     * <p>
     * This method sould possibly moved to
     * {@link AbstractSpooledFileTransformer}.
     * 
     * @return page width in inches
     */
    private float getPageWidthInInches() {
        return super.getPageWidth() / getCPI();
    }

    /**
     * Returns the page height in inches for the actual CPI value of the
     * document.
     * <p>
     * This method sould possibly moved to
     * {@link AbstractSpooledFileTransformer}.
     * 
     * @return page width in inches
     */
    private float getPageHeightInInches() {
        return super.getPageHeight() / getLPI();
    }

    /**
     * Adds some PDF meta data to the document.
     * 
     * @param aPDF - PDF document
     */
    private void addMetaData(Document aPDF) {
        aPDF.addTitle("Spooled file: " + getName());
        aPDF.addAuthor("Job: " + getJob());
        aPDF.addSubject("User data: " + getUserData());
        aPDF.addCreator(getCreator());
    }

    /**
     * Returns the creator of the document. For now the creator is the
     * combination of the name and version number of this plugin.
     * <p>
     * (Could be safely changed)
     * 
     * @return
     */
    private String getCreator() {
        return ISpherePlugin.getDefault().getName() + " v" + ISpherePlugin.getDefault().getVersion();
    }

    private static float round(float value) {
        return round(value, 0);
    }

    private static float round(float value, int decPos) {
        return (float)(Math.round(value * Math.pow(10, decPos)) / Math.pow(10, decPos));
    }

    // -----------------------------------
    // Private internal classes
    // -----------------------------------

    private class PageEventHandler extends PdfPageEventHelper {
        private int i;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            super.onEndPage(writer, document);
            i++;
            PdfContentByte cb = writer.getDirectContent();
            PdfDestination destination = new PdfDestination(PdfDestination.FITH);
            new PdfOutline(cb.getRootOutline(), destination, Messages.Page + " " + i);
        }
    }

    private static class PageMargins {

        private float left;
        private float right;
        private float top;
        private float bottom;

        public PageMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
            this.left = marginLeft;
            this.right = marginRight;
            this.top = marginTop;
            this.bottom = marginBottom;
        }

        public float getLeft() {
            return left;
        }

        public float getRight() {
            return right;
        }

        public float getTop() {
            return top;
        }

        public float getBottom() {
            return bottom;
        }

        @Override
        public String toString() {
            return "margins (left: " + left + ", right: " + right + "top: " + top + ", bottom: " + bottom + ")";
        }

    }

    public static class PageSize implements Comparable<PageSize> {

        public static final String PAGE_SIZE_A4 = "DIN A4"; //$NON-NLS-1$
        public static final String PAGE_SIZE_CALCULATE = "*CALCULATE"; //$NON-NLS-1$

        private String format;

        private Rectangle pageSize;

        public PageSize(float aWidth, float aHeight, String aFormat) {
            format = aFormat;
            pageSize = new Rectangle(aWidth, aHeight);
        }

        /**
         * Return the page width measured in inches.
         * 
         * @return Page width in inches.
         */
        protected float getWidth() {
            return round((pageSize.getRight() - pageSize.getLeft()), 2);
        }

        /**
         * Return the page height measured in inches.
         * 
         * @return Page height in inches.
         */
        protected float getHeight() {
            return round((pageSize.getTop() - pageSize.getBottom()), 2);
        }

        /**
         * Return the format of the page.
         * 
         * @return format of the page.
         */
        public String getFormat() {
            return format;
        }

        /**
         * Return the page dimension measured in dots.
         * 
         * @return Page dimension in dots.
         */
        protected Rectangle getDimension() {
            return pageSize;
            // return new Rectangle(getWidth(), getHeight());
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

        @Override
        public String toString() {
            return format + " (" + pageSize.getWidth() + " x " + pageSize.getHeight() + ")";
        }

    }

}
