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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import biz.isphere.build.nls.NLS;
import biz.isphere.build.nls.configuration.Configuration;
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

    public static void main(String[] args) {
        NLSExporter main = new NLSExporter();

        try {
            if (args.length >= 1) {
                Configuration.getInstance().setConfigurationFile(args[0]);
            }
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        Configuration config = Configuration.getInstance();
        String[] projectNames = config.getProjects();
        Workbook workbook = new HSSFWorkbook();
        for (String projectName : projectNames) {
            EclipseProject project = new EclipseProject(projectName);
            project.loadNLSPropertiesFiles(config.getFiles());
            addToExcelSheet(workbook, project);
        }
        saveWorkbook(workbook, config.getExcelFile());

        LogUtil.print("Finished Excel Export");
    }

    private void saveWorkbook(Workbook workbook, File file) {

        LogUtil.print("Saving Excel workbook to: " + file.getAbsolutePath());

        try {
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            LogUtil.error("Failed to save workbook to: " + file.getAbsolutePath());
        }
    }

    private void addToExcelSheet(Workbook workbook, EclipseProject project) throws Exception {

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

        for (String languageID : project.getLanguageIDs()) {
            addTitleCell(titleRow, languageID);
        }

        // data row
        NLSResourceBundle[] bundles = project.getBundles();
        for (NLSResourceBundle bundle : bundles) {
            for (String key : bundle.getKeys()) {
                Row dataRow = addDataRow(sheet, bundle.getID(), key);
                for (NLSTextEntry value : bundle.getValues(key)) {
                    addLanguageCell(dataRow, value.getText(), value.isProtected());
                }
                ;
            }
        }

        // column widths
        sheet.setColumnWidth(0, sheet.getColumnWidth(0) * 5);

        for (int i = 2; i < 6; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 4);
        }

        // protect sheet
        sheet.protectSheet("");

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