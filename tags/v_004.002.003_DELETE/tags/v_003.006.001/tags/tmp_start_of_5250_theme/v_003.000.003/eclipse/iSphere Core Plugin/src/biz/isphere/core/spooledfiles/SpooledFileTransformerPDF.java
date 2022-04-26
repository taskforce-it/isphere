/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
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

import org.eclipse.jface.resource.FontRegistry;

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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class SpooledFileTransformerPDF extends AbstractSpooledFileTransformer {

    private static int DOTS_PER_INCH = 72;

    private Document document = null;
    private Font font = null;
    private Set<PageSize> pageSizesPortrait = null;

    public SpooledFileTransformerPDF(String connectionName, SpooledFile spooledFile) {
        super(connectionName, spooledFile);

        pageSizesPortrait = getPageSizes_Portrait();
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
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setLeading(0.0F);
        paragraph.setSpacingBefore(font.getSize());
        document.add(paragraph);
    }

    /**
     * Returns a list of DIN A4 page sizes.
     * <p>
     * The list is empty for <code>fitToPage</code> set to <code>false</code>.
     * 
     * @return
     */
    private Set<PageSize> getPageSizes_Portrait() {

        return getPageSizes();
    }

    private PageSize getPredefinedPageSize(boolean isLandscape) {

        PageSize fitToPageSize = null;

        String pageSizeId = Preferences.getInstance().getSpooledFilePageSize();
        if (!StringHelper.isNullOrEmpty(pageSizeId)) {
            fitToPageSize = findPageSize(pageSizeId);
        }

        if (fitToPageSize == null) {
            fitToPageSize = new PageSize(595, 842, PageSize.PAGE_SIZE_A4);
        }

        if (isLandscape) {
            fitToPageSize = new PageSize(fitToPageSize.getHeight(), fitToPageSize.getWidth(), fitToPageSize.getFormat());
        }

        return fitToPageSize;
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

        pageSizes.add(new PageSize(73, 105, "DIN A10")); // 1.02"  1.46"
        pageSizes.add(new PageSize(105, 148, "DIN A9")); // 1.46", 2.05"
        pageSizes.add(new PageSize(148, 210, "DIN A8")); // 2.05", 2.91"
        pageSizes.add(new PageSize(210, 297, "DIN A7")); // 2.91", 4.13"
        pageSizes.add(new PageSize(297, 420, "DIN A6")); // 4.13", 5.83"
        pageSizes.add(new PageSize(420, 595, "DIN A5")); // 5.83", 8.27"
        pageSizes.add(new PageSize(595, 842, PageSize.PAGE_SIZE_A4)); // 8.27", 11.69"
        pageSizes.add(new PageSize(842, 1191, "DIN A3")); // 11.69", 16.54"
        pageSizes.add(new PageSize(1191, 1684, "DIN A2")); // 16.54", 23.39"
        pageSizes.add(new PageSize(1684, 2384, "DIN A1")); // 23.39", 33.11"
        pageSizes.add(new PageSize(2384, 3370, "DIN A0")); // 33.11", 46.81"

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

        PageSize pageSize = null;
        float fontSize;
        String pageSizeId = Preferences.getInstance().getSpooledFilePageSize();

        if (PageSize.PAGE_SIZE_CALCULATE.equals(pageSizeId)) {
            pageSize = findRequiredPageSize(getPageWidthInDots(), getPageHeightInDots());
            fontSize = getFontSize(pageSize);
        } else if (PageSize.PAGE_SIZE_FONT.equals(pageSizeId)) {
            FontRegistry fontRegistry = ISpherePlugin.getDefault().getWorkbench().getThemeManager().getCurrentTheme().getFontRegistry();
            fontSize = fontRegistry.get("org.eclipse.jface.textfont").getFontData()[0].getHeight();
            pageSize = computePageSize(fontSize);
        } else {
            pageSize = getPredefinedPageSize(isLandscape(getPageWidthInDots(), getPageHeightInDots()));
            fontSize = getFontSize(pageSize);
        }

        font = new Font(Font.FontFamily.COURIER, fontSize, Font.NORMAL, BaseColor.BLACK);

        PageMargins pageMargins = getPageMargins(fontSize);

        Document pdf = new Document();

        PdfWriter writer = PdfWriter.getInstance(pdf, new FileOutputStream(aPath));
        pdf.setPageSize(pageSize.getDimension());
        pdf.setMargins(pageMargins.getLeft(), pageMargins.getRight(), pageMargins.getTop(), pageMargins.getBottom());
        pdf.setMarginMirroring(false);

        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
        writer.setPageEvent(new PageEventHandler());

        pdf.open();

        return pdf;
    }

    /**
     * Returns the font size measured in dots for a given CPI value. The font
     * size is returned with one decimal position.
     * 
     * <pre>
     * Factor verification:    Printed several documents with different sizes
     *                         and measured the length of 43 characters.
     *                         Calculated size of character like this:
     *                           cm / Inch * Dots_per_inch / #Chars
     *                         Calculated Size/Width like this:
     *                           Font_Size / With 
     * 
     * Font Size    cm   Inch    Dots per Inch   #Chars  Width          Size/Width
     *     7       6,3   2,54         72           43    4,153085515    1,685493827
     *     8       7,2   2,54         72           43    4,746383446    1,685493827
     *     9       8,1   2,54         72           43    5,339681377    1,685493827
     *    10       9     2,54         72           43    5,932979308    1,685493827
     *    11       9,9   2,54         72           43    6,526277239    1,685493827
     *    12      10,8   2,54         72           43    7,119575169    1,685493827
     *    13      11,7   2,54         72           43    7,7128731      1,685493827
     *    14      12,6   2,54         72           43    8,306171031    1,685493827
     *    15      13,5   2,54         72           43    8,899468962    1,685493827
     *    16      14,4   2,54         72           43    9,492766893    1,685493827
     * </pre>
     * 
     * @return font size
     * @see https://support.microsoft.com/en-us/kb/76388
     */
    private synchronized float getFontSize(PageSize wantedPageSize) {

        float charHeight = 120 / getCPI();

        PageSize requiredPageSize = findRequiredPageSize(getPageWidthInDots(), getPageHeightInDots());

        float factorHeight = (float)requiredPageSize.getHeight() / (float)wantedPageSize.getHeight();
        float factorWidth = (float)requiredPageSize.getWidth() / (float)wantedPageSize.getWidth();

        if (factorHeight > factorWidth) {
            charHeight = charHeight / factorHeight;
        } else {
            charHeight = charHeight / factorWidth;
        }

        boolean adjustFontSize = Preferences.getInstance().getSpooledFileAdjustFontSize();

        if (adjustFontSize) {
            if (charHeight * getPageHeightInLines() < wantedPageSize.getHeight()) {
                charHeight = wantedPageSize.getHeight() / getPageHeightInLines();
                charHeight = dropDecimals(charHeight);
            }

            float WIDTH_FACTOR = 1.685493827F;

            int tCharHeight = (int)(charHeight + 0.5F); // round up
            float tCharWidth = tCharHeight / WIDTH_FACTOR;
            if (tCharWidth * getPageWidthInChars() > wantedPageSize.getWidth()) {
                tCharWidth = wantedPageSize.getWidth() / getPageWidthInChars();
                charHeight = tCharWidth * WIDTH_FACTOR;
            }
        }

        return dropDecimals(charHeight);
    }

    private PageSize computePageSize(float fontSize) {

        float WIDTH_FACTOR = 1.685493827F;

        PageSize spooledFilePageSize = findRequiredPageSize(getPageWidthInDots(), getPageHeightInDots());

        float tCharWidth = fontSize / WIDTH_FACTOR;

        float tPageWidthInDots = tCharWidth * getPageWidthInChars();
        float tPageHeightInDots = fontSize * getPageHeightInLines();

        int pageWidthInDots = (int)(tPageWidthInDots + 0.5F); // round up
        int pageHeightInDots = (int)(tPageHeightInDots + 0.5F); // round up

        float sizeFactor = (float)pageHeightInDots / (float)pageWidthInDots;
        float suggestedSizeFactor = (float)spooledFilePageSize.getHeight() / (float)spooledFilePageSize.getWidth();

        if (sizeFactor > suggestedSizeFactor) {
            pageWidthInDots = (int)((pageHeightInDots / suggestedSizeFactor) + 0.5F);
        }

        return new PageSize(pageWidthInDots, pageHeightInDots, "*USRDEF");
    }

    private float dropDecimals(float charHeight) {
        return ((int)(charHeight * 10.0F)) / 10.0F;
    }

    private PageMargins getPageMargins(float fontSize) {
        return new PageMargins(0, 0, (int)fontSize, 0);
    }

    /**
     * Computes the page size for a given page dimension. Height and width must
     * be passed in inches.
     * 
     * @param pageWidth - page width in dots
     * @param pageHeight - page height in dots
     * @return page dimension
     */

    private PageSize findRequiredPageSize(int pageWidthInDots, int pageHeightInDots) {

        boolean isLandscape = isLandscape(pageWidthInDots, pageHeightInDots);

        PageSize actualPageSizePortrait;

        if (isLandscape) {
            actualPageSizePortrait = new PageSize(pageHeightInDots, pageWidthInDots, "");
        } else {
            actualPageSizePortrait = new PageSize(pageWidthInDots, pageHeightInDots, "");
        }

        PageSize pageSize = null;
        for (Iterator<PageSize> iterator = pageSizesPortrait.iterator(); iterator.hasNext();) {
            PageSize curentPageSize = iterator.next();
            if (curentPageSize.compareTo(actualPageSizePortrait) > 0) {
                pageSize = curentPageSize;
                break;
            }
        }

        if (pageSize == null) {
            return new PageSize(pageWidthInDots, pageHeightInDots, "*USRDEF");
        } else {
            if (isLandscape) {
                return new PageSize(pageSize.getHeight(), pageSize.getWidth(), pageSize.getFormat());
            } else {
                return new PageSize(pageSize.getWidth(), pageSize.getHeight(), pageSize.getFormat());
            }
        }
    }

    private boolean isLandscape(int pageWidthInDots, int pageHeightInDots) {
        boolean isLandscape;
        if (pageWidthInDots > pageHeightInDots) {
            // Landscape
            isLandscape = true;
        } else {
            // Portrait
            isLandscape = false;
        }
        return isLandscape;
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
    private int getPageWidthInDots() {
        return (int)(super.getPageWidthInChars() / getCPI() * DOTS_PER_INCH);
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
    private int getPageHeightInDots() {
        return (int)super.getPageHeightInLines() / getLPI() * DOTS_PER_INCH;
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

    private static class PageMargins {

        private int left;
        private int right;
        private int top;
        private int bottom;

        public PageMargins(int marginLeftInDots, int marginRightInDots, int marginTopInDots, int marginBottomInDots) {
            this.left = marginLeftInDots;
            this.right = marginRightInDots;
            this.top = marginTopInDots;
            this.bottom = marginBottomInDots;
        }

        /**
         * Return the left margin measured in dots
         * 
         * @return left margin in dots
         */
        public float getLeft() {
            return left;
        }

        /**
         * Return the right margin measured in dots
         * 
         * @return right margin in dots
         */
        public float getRight() {
            return right;
        }

        /**
         * Return the top margin measured in dots
         * 
         * @return top margin in dots
         */
        public float getTop() {
            return top;
        }

        /**
         * Return the bottom margin measured in dots
         * 
         * @return bottom margin in dots
         */
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
        public static final String PAGE_SIZE_FONT = "*FONT"; //$NON-NLS-1$

        private String format;
        private Rectangle pageSize;

        public PageSize(int widthInDots, int heightInDots, String format) {
            this.format = format;
            this.pageSize = new Rectangle(widthInDots, heightInDots);
        }

        /**
         * Return the page width measured in dots.
         * 
         * @return Page width in inches.
         */
        protected int getWidth() {
            return (int)(pageSize.getRight() - pageSize.getLeft());
        }

        /**
         * Return the page height measured in dots.
         * 
         * @return Page height in dots.
         */
        protected int getHeight() {
            return (int)(pageSize.getTop() - pageSize.getBottom());
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
            return format + " (width: " + pageSize.getWidth() + ", height:" + pageSize.getHeight() + ")";
        }
    }
}
