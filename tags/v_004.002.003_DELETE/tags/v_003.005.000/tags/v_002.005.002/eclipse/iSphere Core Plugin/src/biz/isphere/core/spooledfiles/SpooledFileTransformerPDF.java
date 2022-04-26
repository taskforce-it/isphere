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

    private boolean fitToPage = false;

    public SpooledFileTransformerPDF(SpooledFile spooledFile) {
        super(spooledFile);
        pageSizes = getPageSizes_DIN_A4_Portrait();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the workstation customization object that is used for spooled
     * file conversion to PDF.
     */
    @Override
    protected QSYSObjectPathName getWorkstationCustomizationObject() {
        return new QSYSObjectPathName(ISpherePlugin.getISphereLibrary(), "SPLFPDF", "WSCST");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Produces an empty PDF document.
     */
    @Override
    protected void openPrinter(String target) throws FileNotFoundException, DocumentException {
        document = createPFD(target);
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
        document.add(new Chunk(text, font));
    }

    /**
     * Returns a list of DIN A4 page sizes.
     * <p>
     * The list is empty for <code>fitToPage</code> set to <code>false</code>.
     * 
     * @return
     */
    private Set<PageSize> getPageSizes_DIN_A4_Portrait() {
        TreeSet<PageSize> pageSizes_DIN_A4 = new TreeSet<PageSize>();
        if (fitToPage) {
            return pageSizes_DIN_A4;
        }
        pageSizes_DIN_A4.add(new PageSize(1.02F, 1.46F, "A10")); // A10
        pageSizes_DIN_A4.add(new PageSize(1.46F, 2.05F, "A9")); // A9
        pageSizes_DIN_A4.add(new PageSize(2.05F, 2.91F, "A8")); // A8
        pageSizes_DIN_A4.add(new PageSize(2.91F, 4.13F, "A7")); // A7
        pageSizes_DIN_A4.add(new PageSize(4.13F, 5.83F, "A6")); // A6
        pageSizes_DIN_A4.add(new PageSize(5.83F, 8.27F, "A5")); // A5
        pageSizes_DIN_A4.add(new PageSize(8.27F, 11.69F, "A4")); // A4
        pageSizes_DIN_A4.add(new PageSize(11.69F, 16.54F, "A3")); // A3
        pageSizes_DIN_A4.add(new PageSize(16.54F, 23.39F, "A2")); // A2
        pageSizes_DIN_A4.add(new PageSize(23.39F, 33.11F, "A1")); // A1
        pageSizes_DIN_A4.add(new PageSize(33.11F, 46.81F, "A0")); // A0
        return pageSizes_DIN_A4;
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

    /**
     * Returns the font size measured in dots for a given CPI valie.
     * 
     * @return fonst size
     */
    private float getFontSize() {
        return 120 / getCPI();
    }

    /**
     * Computes the page size for a given page dimension. Height and width must
     * be passed in inches.
     * 
     * @param pageWidth - page width
     * @param pageHeight - page height
     * @return page dimension
     */
    private Rectangle selectRequiredPageSize(float pageWidth, float pageHeight) {
        float width;
        float height;
        boolean isLandscape = false;
        if (pageWidth > pageHeight) {
            // Landscape
            height = pageWidth;
            width = pageHeight;
            isLandscape = true;
        } else {
            // Portrait
            height = pageHeight;
            width = pageWidth;
            isLandscape = false;
        }
        PageSize pageSize = null;
        for (Iterator<PageSize> iterator = pageSizes.iterator(); iterator.hasNext();) {
            PageSize curentPageSize = iterator.next();
            if (curentPageSize.getWidth() >= width && curentPageSize.getHeight() >= height) {
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

    private class PageSize implements Comparable<PageSize> {

        private String format;

        private Rectangle pageSize;

        private int DOTS_PER_INCH = 72;

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
            return pageSize.getRight() - pageSize.getLeft();
        }

        /**
         * Return the page height measured in inches.
         * 
         * @return Page height in inches.
         */
        protected float getHeight() {
            return pageSize.getTop() - pageSize.getBottom();
        }

        /**
         * Return the format of the page.
         * 
         * @return format of the page.
         */
        protected String getFormat() {
            return format;
        }

        /**
         * Return the page dimension measured in dots.
         * 
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

        @Override
        public String toString() {
            return format + " (" + pageSize.getWidth() + " x " + pageSize.getHeight() + ")";
        }

    }

}
