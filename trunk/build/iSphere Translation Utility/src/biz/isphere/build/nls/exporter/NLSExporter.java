/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import biz.isphere.build.nls.NLS;
import biz.isphere.build.nls.configuration.Configuration;
import biz.isphere.build.nls.exception.JobCanceledException;
import biz.isphere.build.nls.model.EclipseProject;
import biz.isphere.build.nls.model.NLSResourceBundle;
import biz.isphere.build.nls.model.NLSTextEntry;
import biz.isphere.build.nls.utils.LogUtil;

/**
 * Class to start the export of language strings for translation purposes.
 * <p>
 * By default the export configuration is read from <code>nls.properties</code>,
 * but can be overridden at the command line.
 * 
 * @author Thomas Raddatz
 */
public class NLSExporter {

    private static final String STYLE_HEADLINE = "headline";

    private static final String STYLE_TITLE = "title";

    private static final String STYLE_LANGUAGE = "language";

    private static final String STYLE_LANGUAGE_PROTECTED = "language_protected";

    private static final String STYLE_DATA_PROTECTED = "data_protected";

    private Map<String, CellStyle> styles = null;

    /**
     * Main method of the exporter utility. Valid optional arguments are:
     * <p>
     * - name of the configuration properties file
     * 
     * @param args
     */
    public static void main(String[] args) {
        NLSExporter main = new NLSExporter();

        try {
            if (args.length >= 1) {
                Configuration.getInstance().setConfigurationFile(args[0]);
            }
            main.run();
        } catch (JobCanceledException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void run() throws JobCanceledException {
        Configuration config = Configuration.getInstance();
        String[] projectNames = config.getProjects();
        Workbook workbook = new HSSFWorkbook();
        for (String projectName : projectNames) {
            EclipseProject project = new EclipseProject(projectName);
            project.loadNLSPropertiesFiles(config.getFiles());
            addToExcelSheet(workbook, project);
        }
        saveWorkbook(workbook, config.getExportFile());

        LogUtil.print("Finished Excel Export");
    }

    private void saveWorkbook(Workbook workbook, File file) throws JobCanceledException {

        LogUtil.print("Saving Excel workbook to: " + file.getAbsolutePath());

        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            LogUtil.error("Failed to save workbook to: " + file.getAbsolutePath());
            throw new JobCanceledException(e.getLocalizedMessage());
        }
    }

    private void addToExcelSheet(Workbook workbook, EclipseProject project) throws JobCanceledException {

        LogUtil.print("Adding to Excel workbook: " + project);

        // cell styles
        styles = createStyles(workbook);

        // Excel sheet
        Sheet sheet = workbook.createSheet(project.getName());

        // headline row
        Row headlineRow = sheet.createRow(getNextRowNum(sheet));
        headlineRow.setHeightInPoints(12.75f);
        addHeadlineCell(headlineRow, project.getName());

        // title row
        Row titleRow = sheet.createRow(getNextRowNum(sheet));
        titleRow.setHeightInPoints(12.75f);

        addTitleCell(titleRow, NLS.PATH);
        addTitleCell(titleRow, NLS.KEY);

        for (String languageID : project.getLanguageKeys()) {
            addTitleCell(titleRow, languageID);
        }

        // data row
        NLSResourceBundle[] bundles = project.getBundles();
        Row firstDataRow = null;
        Row lastDataRow = null;
        for (NLSResourceBundle bundle : bundles) {
            for (String key : bundle.getKeys()) {
                Row dataRow = addDataRow(sheet, bundle.getID(), key);
                if (firstDataRow == null) {
                    firstDataRow = dataRow;
                }
                lastDataRow = dataRow;
                for (NLSTextEntry value : bundle.getValues(key)) {
                    addLanguageCell(dataRow, value.getText(), value.isProtected());
                }
                ;
            }
        }

        addCellFormatting(sheet, firstDataRow, lastDataRow);

        // column widths
        sheet.setColumnWidth(0, sheet.getColumnWidth(0) * 5);

        for (int i = 2; i < 6; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 4);
        }

        // protect sheet
         sheet.protectSheet("");

    }

    /*
     * This cell formatting approach works as designed, but is fairly slow on
     * opening the workbook.
     */
    private void addCellFormatting(Sheet sheet, Row firstDataRow, Row lastDataRow) {

        for (int r = firstDataRow.getRowNum(); r <= lastDataRow.getRowNum(); r++) {
            Row row = sheet.getRow(r);
            Cell defaultLanguageCell = row.getCell(2);
            for (int c = 3; c < row.getLastCellNum(); c++) {
                createCellFormattingRule(sheet, defaultLanguageCell, row.getCell(c));
            }
        }
    }

    private void createCellFormattingRule(Sheet sheet, Cell defaultLanguageCell, Cell cell) {

        SheetConditionalFormatting formating = sheet.getSheetConditionalFormatting();

        // Create formatting rule
        ConditionalFormattingRule rule = formating.createConditionalFormattingRule(ComparisonOperator.EQUAL,
            getExcelCellCoordinates(defaultLanguageCell));

        // Create formatting pattern which is applied to the cell when the rule
        // evaluates to true.
        PatternFormatting pattern = rule.createPatternFormatting();
        pattern.setFillBackgroundColor(HSSFColor.AQUA.index);

        // Bind rule to cell and sheet
        int rowNum = cell.getRowIndex();
        int columnNum = cell.getColumnIndex();
        CellRangeAddress[] regions = { new CellRangeAddress(rowNum, rowNum, columnNum, columnNum) };
        formating.addConditionalFormatting(regions, rule);

    }

    private String getExcelCellCoordinates(Cell cell) {
        StringBuilder coordinates = new StringBuilder();
        String columnLetter = CellReference.convertNumToColString(cell.getColumnIndex());
        coordinates.append("$");
        coordinates.append(columnLetter);
        coordinates.append("$");
        coordinates.append(cell.getRowIndex() + 1);
        return coordinates.toString();
    }

    /*
     * What am I doing wrong?
     */
    /*
     * This cell formatting rule approach does not work, because Excel shows
     * 'F7' as the cell with the default language string, whereas 'C4' is
     * expected. The problem seems to be related to the way
     * addConditionalFormatting() works. Changing the return value of
     * getExcelCellCoordinates() to fixed 'A1' gets closer but not close enough.
     * For 'A1' Excel shows 'D4' instead of 'C4'.
     */
    private void addCellFormatting_doesNotWork(Sheet sheet, Row firstDataRow, Row lastDataRow) {
        createCellFormattingRule_doesNotWork(sheet, firstDataRow, lastDataRow);
    }

    private void createCellFormattingRule_doesNotWork(Sheet sheet, Row firstDataRow, Row lastDataRow) {

        Cell defaultLanguageCell = firstDataRow.getCell(2);
        Cell topLeftLanguageCell = firstDataRow.getCell(3);
        Cell bottomRightLanguageCell = lastDataRow.getCell(lastDataRow.getLastCellNum() - 1);

        SheetConditionalFormatting formating = sheet.getSheetConditionalFormatting();

        // Create formatting rule
        ConditionalFormattingRule rule = formating.createConditionalFormattingRule(ComparisonOperator.EQUAL,
            getExcelCellCoordinates_1(defaultLanguageCell));

        // Create formatting pattern which is applied to the cell when the rule
        // evaluates to true.
        PatternFormatting pattern = rule.createPatternFormatting();
        pattern.setFillBackgroundColor(HSSFColor.AQUA.index);

        // Bind rule to cell and sheet
        CellRangeAddress[] regions = { new CellRangeAddress(firstDataRow.getRowNum(), lastDataRow.getRowNum(), topLeftLanguageCell.getColumnIndex(),
            bottomRightLanguageCell.getColumnIndex()) };
        formating.addConditionalFormatting(regions, rule);

        System.out.println(rule.getFormula1());
    }

    private String getExcelCellCoordinates_1(Cell cell) {
        StringBuilder coordinates = new StringBuilder();
        String columnLetter = CellReference.convertNumToColString(cell.getColumnIndex());
        coordinates.append(columnLetter);
        coordinates.append(cell.getRowIndex() + 1);
        // return coordinates.toString();
        return "A1";
    }

    private Row addDataRow(Sheet sheet, String id, String key) {
        Row row = sheet.createRow(getNextRowNum(sheet));
        addDataCell(row, id);
        addDataCell(row, key);
        return row;
    }

    private void addDataCell(Row row, String value) {
        Cell titleCell = row.createCell(getNextCellNum(row));
        titleCell.setCellValue(value);
        titleCell.setCellStyle(styles.get(STYLE_DATA_PROTECTED));
    }

    private void addLanguageCell(Row row, String value, boolean isProtected) {
        Cell dataCell = row.createCell(getNextCellNum(row));
        dataCell.setCellValue(value);
        if (isProtected) {
            dataCell.setCellStyle(styles.get(STYLE_LANGUAGE_PROTECTED));
        } else {
            dataCell.setCellStyle(styles.get(STYLE_LANGUAGE));
        }
    }

    private void addTitleCell(Row row, String title) {
        Cell titleCell = row.createCell(getNextCellNum(row));
        titleCell.setCellValue(title);
        titleCell.setCellStyle(styles.get(STYLE_TITLE));
    }

    private void addHeadlineCell(Row row, String headline) {
        Cell titleCell = row.createCell(getNextCellNum(row));
        titleCell.setCellValue(headline);
        titleCell.setCellStyle(styles.get(STYLE_HEADLINE));
    }

    private int getNextCellNum(Row row) {
        if (row.getLastCellNum() < 0) {
            return 0;
        }
        return row.getLastCellNum();
    }

    private int getNextRowNum(Sheet sheet) {
        return sheet.getLastRowNum() + 1;
    }

    /**
     * Create a library of cell styles
     */
    private Map<String, CellStyle> createStyles(Workbook workbook) {
        Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
        CellStyle style;

        Font headlineFont = workbook.createFont();
        headlineFont.setFontHeightInPoints((short)10);
        headlineFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headlineFont.setItalic(true);
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(headlineFont);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setLocked(true);
        styles.put(STYLE_HEADLINE, style);

        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short)10);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(titleFont);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setLocked(true);
        styles.put(STYLE_TITLE, style);

        Font dataFont = workbook.createFont();
        dataFont.setFontHeightInPoints((short)10);
        dataFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(dataFont);
        style.setLocked(true);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styles.put(STYLE_DATA_PROTECTED, style);

        styles.put(STYLE_LANGUAGE, createLanguageStyle(workbook, false));
        styles.put(STYLE_LANGUAGE_PROTECTED, createLanguageStyle(workbook, true));

        return styles;
    }

    private CellStyle createLanguageStyle(Workbook workbook, boolean isProtected) {
        CellStyle style;
        Font languageFont = workbook.createFont();
        languageFont.setFontHeightInPoints((short)10);
        languageFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(languageFont);
        style.setLocked(isProtected);

        if (isProtected) {
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        return style;
    }

}