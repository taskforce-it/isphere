/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.build.nls.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import biz.isphere.build.nls.NLS;
import biz.isphere.build.nls.configuration.Configuration;
import biz.isphere.build.nls.exception.JobCanceledException;
import biz.isphere.build.nls.model.EclipseProject;
import biz.isphere.build.nls.model.NLSPropertiesFile;
import biz.isphere.build.nls.model.NLSResourceBundle;
import biz.isphere.build.nls.utils.LogUtil;

/**
 * Class to start the import of language strings for translation purposes.
 * <p>
 * By default the import configuration is read from <code>nls.properties</code>,
 * but can be overridden at the command line.
 * <p>
 * Today the importer imports all data from the Excel sheet except for the
 * strings of the default language.
 * 
 * @author Thomas Raddatz
 */
public class NLSImporter {

    /**
     * Main method of the importer utility. Valid optional arguments are:
     * <p>
     * - name of the configuration properties file
     * 
     * @param args
     */
    public static void main(String[] args) {
        NLSImporter main = new NLSImporter();

        try {
            if (args.length > 0) {
                Configuration.getInstance().setConfigurationFile(args[0]);
            }

            String[] languageIds = Configuration.getInstance().getImportLanguageIDs();

            main.run(languageIds);

        } catch (JobCanceledException e) {
            e.printStackTrace();
        }
    }

    private void run(String[] languageIds) throws JobCanceledException {

        LogUtil.print("\nStarting Excel Import for languages: " + buildLanguageIdsString(languageIds));

        Workbook workbook = loadWorkbook(Configuration.getInstance().getImportFile());
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            EclipseProject project = loadFromExcelSheet(sheet, workbook, languageIds);
            project.updateNLSPropertiesFiles();
        }

        LogUtil.print("Finished Excel Export for languages: " + buildLanguageIdsString(languageIds));
    }

    private EclipseProject loadFromExcelSheet(Sheet sheet, Workbook wb, String[] languageIds) throws JobCanceledException {

        LogUtil.print("Loading data from Excel sheet: " + sheet.getSheetName());

        Cell cell = findHeadlineCell(sheet);

        String projectName = cell.getStringCellValue(); // sheet.getSheetName();
        EclipseProject project = new EclipseProject(projectName, languageIds);

        Row firstDataRow = findFirstDataRow(sheet);
        while (firstDataRow != null) {
            Row lastDataRow = findLastDataRow(sheet, firstDataRow);
            importPropertiesFileFromExcelSheet(project, sheet, firstDataRow, lastDataRow);
            firstDataRow = getNextDataRow(sheet, lastDataRow);
        }
        return project;
    }

    private void importPropertiesFileFromExcelSheet(EclipseProject project, Sheet sheet, Row firstDataRow, Row lastDataRow)
        throws JobCanceledException {

        String relativePath = firstDataRow.getCell(0).getStringCellValue();
        NLSResourceBundle bundle = project.getOrCreateBundle(relativePath);

        NLSPropertiesFile nlsDefaultLanguageFile = null;
        String[] languageKeys = getLanguagesKeys(sheet);
        for (String languageKey : languageKeys) {
            NLSPropertiesFile nlsFile = new NLSPropertiesFile(project.getPath(), relativePath, languageKey);
            if (nlsFile.exists()) {
                if (nlsFile.isDefaultLanguage()) {
                    nlsDefaultLanguageFile = nlsFile;
                } else {
                    LogUtil.print(" File added: " + nlsFile.toString());
                }
                bundle.add(nlsFile);
            } else {
                LogUtil.print(" File ignored: not found: " + nlsFile.toString());
            }
        }

        if (bundle.isEmpty()) {
            return;
        }

        for (int i = firstDataRow.getRowNum(); i <= lastDataRow.getRowNum(); i++) {
            String key = getKey(sheet.getRow(i));
            String[] values = getValues(sheet.getRow(i), languageKeys.length);

            for (int x = 0; x < languageKeys.length; x++) {
                NLSPropertiesFile nlsFile = bundle.getNLSFile(languageKeys[x]);
                if (!nlsFile.isDefaultLanguage()) {
                    if (bundle.isSelectedForImport(nlsFile.getLanguage(), Configuration.getInstance().getImportLanguageIDs())) {
                        if (nlsFile.getText(key) == null) {
                            // LogUtil.print("Property ignored: because not found: "
                            // + key);
                        } else {
                            if (nlsFile.getText(key).equals(values[x])) {
                                // ignored: not changed
                            } else {
                                String dftLangValue = nlsDefaultLanguageFile.getText(key);
                                if (dftLangValue.equals(nlsFile.getText(key))) {
                                    LogUtil.print("   Property translated: [" + key + "]  '" + replaceNewLineChar(nlsFile.getText(key)) + "' ==> '"
                                        + replaceNewLineChar(values[x]) + "'");
                                    nlsFile.setProperty(key, values[x]);
                                } else {
                                    LogUtil.print("   Property updated ..: [" + key + "]  '" + replaceNewLineChar(nlsFile.getText(key)) + "' ==> '"
                                        + replaceNewLineChar(values[x]) + "'");
                                    nlsFile.setProperty(key, values[x]);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String replaceNewLineChar(String value) {
        return value.replaceAll("\\n", "\\\\n");
    }

    private String getKey(Row row) {
        return row.getCell(1).getStringCellValue();
    }

    private String[] getValues(Row row, int numValues) {
        List<String> values = new ArrayList<String>();
        int i = 2;
        while (numValues > 0) {
            values.add(row.getCell(i).getStringCellValue());
            numValues--;
            i++;
        }
        return values.toArray(new String[values.size()]);
    }

    private Row getNextDataRow(Sheet sheet, Row row) {
        if (row.getRowNum() < sheet.getLastRowNum()) {
            return sheet.getRow(row.getRowNum() + 1);
        }
        return null;
    }

    private Cell findHeadlineCell(Sheet sheet) {
        for (Iterator<Row> iterator = sheet.iterator(); iterator.hasNext();) {
            Row row = iterator.next();
            for (Iterator<?> cellIterator = row.iterator(); cellIterator.hasNext();) {
                Object object = cellIterator.next();
                if (object instanceof HSSFCell) {
                    HSSFCell cell = (HSSFCell)object;
                    if (cell.getCellStyle().getFillForegroundColor() == IndexedColors.LIGHT_YELLOW.getIndex()
                        && cell.getCellStyle().getFont(sheet.getWorkbook()).getBoldweight() == Font.BOLDWEIGHT_BOLD) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }

    private Row findFirstDataRow(Sheet sheet) {
        for (Iterator<Row> iterator = sheet.iterator(); iterator.hasNext();) {
            Row row = iterator.next();
            if (isHeadLineRow(row) && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

    private Row findLastDataRow(Sheet sheet, Row firstDataRow) {
        String startPath = firstDataRow.getCell(0).getStringCellValue();
        Row lastRow = null;
        for (int i = firstDataRow.getRowNum(); i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null) {
                String currPath = row.getCell(0).getStringCellValue();
                if (!startPath.equals(currPath)) {
                    return lastRow;
                } else {
                    lastRow = row;
                }
            }
        }
        return lastRow;
    }

    private boolean isHeadLineRow(Row row) {
        Cell cell = row.getCell(0);
        if (cell != null && NLS.PATH.equals(cell.getStringCellValue())) {
            return true;
        }
        return false;
    }

    private String[] getLanguagesKeys(Sheet sheet) throws JobCanceledException {
        Set<String> languages = new TreeSet<String>();

        for (Iterator<Row> iterator = sheet.iterator(); iterator.hasNext();) {
            Row row = iterator.next();
            if (isHeadLineRow(row)) {
                for (int i = 2; i < row.getLastCellNum(); i++) {
                    String langID = row.getCell(i).getStringCellValue();
                    languages.add(langID);
                }
            }
        }
        return languages.toArray(new String[languages.size()]);

    }

    private Workbook loadWorkbook(File file) throws JobCanceledException {

        LogUtil.print("Loading Excel workbook from: " + file.getAbsolutePath());

        try {
            InputStream in = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(in);
            in.close();
            return workbook;
        } catch (Exception e) {
            throw new JobCanceledException("Failed to load workbook from: " + file.getAbsolutePath());
        }

    }

    private String buildLanguageIdsString(String[] languageIds) {

        StringBuilder buffer = new StringBuilder();
        for (String languageId : languageIds) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(languageId);
        }

        return buffer.toString();
    }

}