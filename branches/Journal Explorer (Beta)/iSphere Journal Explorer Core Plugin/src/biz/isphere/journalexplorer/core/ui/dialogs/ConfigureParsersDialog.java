/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Initial idea and development: Isaac Ramirez Herrera
 * Continued and adopted to iSphere: iSphere Project Team
 *******************************************************************************/

package biz.isphere.journalexplorer.core.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import biz.isphere.base.jface.dialogs.XDialog;
import biz.isphere.journalexplorer.base.interfaces.IMetaTable;
import biz.isphere.journalexplorer.core.ISphereJournalExplorerCorePlugin;
import biz.isphere.journalexplorer.core.Messages;
import biz.isphere.journalexplorer.core.model.MetaDataCache;
import biz.isphere.journalexplorer.core.model.MetaTable;
import biz.isphere.journalexplorer.core.ui.labelproviders.ParserColumnLabel;

public class ConfigureParsersDialog extends XDialog {

    private static final String JOURNALED_OBJECT = "JOURNALED_OBJECT";
    private static final String PARSER_LIBRARY = "PARSER_LIBRARY";
    private static final String PARSER_NAME = "PARSER_NAME";
    private static final String PARSING_OFFSET = "PARSING_OFFSET";
    private static final String[] COLUMN_NAMES = new String[] { JOURNALED_OBJECT, PARSER_LIBRARY, PARSER_NAME, PARSING_OFFSET };

    TableViewer tableViewer;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public ConfigureParsersDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite container = (Composite)super.createDialogArea(parent);
        FillLayout fl_container = new FillLayout(SWT.HORIZONTAL);
        fl_container.marginHeight = 10;
        fl_container.marginWidth = 10;
        container.setLayout(fl_container);

        createTableViewer(container);
        loadValues();

        return container;
    }

    private void createTableViewer(Composite container) {

        tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parent, Object elements) {

                if (elements instanceof MetaTable) {
                    MetaTable metaTable = (MetaTable)elements;
                    if (!metaTable.isHidden()) {
                        return true;
                    }
                }

                return false;
            }
        });

        Table table = tableViewer.getTable();

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // /
        // / journaledObject column
        // /
        TableColumn journaledObject = new TableColumn(table, SWT.NONE);
        journaledObject.setWidth(150);
        journaledObject.setText(Messages.ConfigureParsersDialog_JournalObject);
        // journaledObject.setLabelProvider(new ColumnLabelProvider() {
        // @Override
        // public String getText(Object element) {
        //
        // if (element instanceof MetaTable) {
        // MetaTable currentElement = (MetaTable)element;
        // return QualifiedName.getName(currentElement.getLibrary(),
        // currentElement.getName());
        // } else {
        // return null;
        // }
        // }
        //
        // @Override
        // public Image getImage(Object element) {
        // if (element instanceof MetaTable) {
        // if (((MetaTable)element).isLoaded()) {
        // return null;
        // } else {
        // return
        // PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
        // }
        // } else {
        // return null;
        // }
        // }
        // });

        // /
        // / parserLibrary column
        // /
        TableColumn parserLibrary = new TableColumn(table, SWT.NONE);
        parserLibrary.setWidth(150);
        parserLibrary.setText(Messages.ConfigureParsersDialog_DefinitionLibrary);
        // parserLibrary.setLabelProvider(new ColumnLabelProvider() {
        // @Override
        // public String getText(Object element) {
        //
        // if (element instanceof MetaTable) {
        // MetaTable currentElement = (MetaTable)element;
        // return currentElement.getDefinitionLibrary().trim();
        // } else {
        // return null;
        // }
        // }
        // });
        // parserLibrary.setEditingSupport(new
        // ParserLibraryEditingSupport(tableViewer));

        // /
        // / parserObject column
        // /
        TableColumn parserObject = new TableColumn(table, SWT.NONE);
        parserObject.setWidth(150);
        parserObject.setText(Messages.ConfigureParsersDialog_DefinitionObject);
        // parserObject.setLabelProvider(new ColumnLabelProvider() {
        // @Override
        // public String getText(Object element) {
        //
        // if (element instanceof MetaTable) {
        // MetaTable currentElement = (MetaTable)element;
        // return currentElement.getDefinitionName().trim();
        // } else {
        // return null;
        // }
        // }
        // });
        // parserObject.setEditingSupport(new
        // ParserNameEditingSupport(tableViewer));

        // /
        // / parsingOffset column
        // /
        TableColumn parsingOffset = new TableColumn(table, SWT.NONE);
        parsingOffset.setWidth(170);
        parsingOffset.setText(Messages.ConfigureParsersDialog_ParsingOffset);
        // parsingOffset.setLabelProvider(new ColumnLabelProvider() {
        // @Override
        // public String getText(Object element) {
        //
        // if (element instanceof MetaTable) {
        // MetaTable currentElement = (MetaTable)element;
        // return Integer.toString(currentElement.getParsingOffset());
        // } else {
        // return null;
        // }
        // }
        // });
        // parsingOffset.setEditingSupport(new
        // ParsingOffsetEditingSupport(tableViewer));

        tableViewer.setColumnProperties(COLUMN_NAMES);
        tableViewer.setLabelProvider(new ParserColumnLabel());

        configureEditors(tableViewer, COLUMN_NAMES);
    }

    private void configureEditors(final TableViewer tableViewer, final String[] columnNames) {

        Table table = tableViewer.getTable();
        TextCellEditor textEditor;

        // Create the cell editors
        final CellEditor[] editors = new CellEditor[columnNames.length];

        // Column 1 : Journal object
        editors[0] = null;

        // Column 2 : Parser library
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).setTextLimit(10);
        editors[1] = textEditor;

        // Column 3 : Parser name
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).setTextLimit(10);
        editors[2] = textEditor;

        // Column 4 : Parsing offset
        textEditor = new TextCellEditor(table);
        ((Text)textEditor.getControl()).addVerifyListener(

        new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                e.doit = "0123456789".indexOf(e.text) >= 0;
            }
        });
        editors[3] = textEditor;

        // Assign the cell editors to the viewer
        tableViewer.setCellEditors(editors);

        // Set the cell modifier for the viewer
        tableViewer.setCellModifier(new ICellModifier() {

            public void modify(Object element, String property, Object value) {

                TableItem tableItem = (TableItem)element;
                IMetaTable metaTable = (IMetaTable)tableItem.getData();

                int index = getColumnIndex(property);
                switch (index) {
                case 1: // Parser library
                    metaTable.setDefinitionLibrary((String)value);
                    tableViewer.update(metaTable, null);
                case 2: // Parser name
                    metaTable.setDefinitionName((String)value);
                    tableViewer.update(metaTable, null);
                case 3: // Parsing offset
                    try {
                        metaTable.setParsingOffset(Integer.parseInt((String)value));
                        tableViewer.update(metaTable, null);
                    } catch (Exception e) {
                    }
                default:
                }
            }

            public Object getValue(Object element, String property) {

                IMetaTable metaTable = (IMetaTable)element;

                int index = getColumnIndex(property);
                switch (index) {
                case 0: // Journaled object
                    return metaTable.getQualifiedName();
                case 1: // Parser library
                    return metaTable.getDefinitionLibrary();
                case 2: // Parser name
                    return metaTable.getDefinitionName();
                case 3: // Parsing offset
                    return Integer.toString(metaTable.getParsingOffset());
                default:
                    return "";
                }
            }

            public boolean canModify(Object element, String property) {

                int index = getColumnIndex(property);
                if (index < 0 || index > editors.length - 1) {
                    return false;
                }

                return editors[index] != null;
            }

            private int getColumnIndex(String property) {

                for (int i = 0; i < columnNames.length; i++) {
                    if (property.equals(columnNames[i])) {
                        return i;
                    }

                }

                return -1;
            }
        });
    }

    private void loadValues() {
        tableViewer.setInput(MetaDataCache.INSTANCE.getCachedParsers());
    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ConfigureParsersDialog_SetDefinitions);
    }

    /**
     * Overridden make this dialog resizable {@link XDialog}.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Overridden to provide a default size to {@link XDialog}.
     */
    @Override
    protected Point getDefaultSize() {
        return new Point(680, 310);
    }

    /**
     * Overridden to let {@link XDialog} store the state of this dialog in a
     * separate section of the dialog settings file.
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        return super.getDialogBoundsSettings(ISphereJournalExplorerCorePlugin.getDefault().getDialogSettings());
    }
}
