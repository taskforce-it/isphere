/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.jobtraceexplorer.core.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import biz.isphere.core.swt.widgets.ContentAssistProposal;
import biz.isphere.core.swt.widgets.ContentAssistText;
import biz.isphere.core.swt.widgets.WidgetFactory;
import biz.isphere.core.swt.widgets.sqleditor.DisplaySQLHelpListener;
import biz.isphere.jobtraceexplorer.core.ISphereJobTraceExplorerCorePlugin;
import biz.isphere.jobtraceexplorer.core.Messages;
import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public class JobTraceExplorerSearchPanel extends Composite {

    private ContentAssistText textSearch;

    private Button buttonUp;
    private Button buttonDown;

    private List<SelectionListener> filterChangedListeners;

    public JobTraceExplorerSearchPanel(Composite parent, int style) {
        super(parent, style);

        this.filterChangedListeners = new ArrayList<SelectionListener>();

        createContentArea();
    }

    private GridLayout createGridLayout(int numColumns, boolean makeColumnsEqual) {

        GridLayout gridLayout = new GridLayout(numColumns, makeColumnsEqual);
        gridLayout.marginTop = 0;
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 1;
        gridLayout.marginWidth = 5;

        return gridLayout;
    }

    private void createContentArea() {

        GridLayout gridLayout = createGridLayout(2, false);
        gridLayout.marginTop = 5;
        setLayout(gridLayout);

        // Create controls
        createTextSearchControls(this);

        createButtons(this);
    }

    private void createTextSearchControls(Composite parent) {

        Composite textSearchArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(6, false);
        textSearchArea.setLayout(gridLayout);
        textSearchArea.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));

        Label labelSearch = new Label(textSearchArea, SWT.NONE);
        labelSearch.setText(Messages.Label_Text);
        labelSearch.setToolTipText(Messages.ButtonTooltip_Text_tooltip);

        textSearch = WidgetFactory.createContentAssistText(textSearchArea, SWT.BORDER);
        textSearch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        textSearch.setTraverseEnabled(true);
        textSearch.enableEnterKey(false);
        textSearch.enableAutoActivation(false);
        textSearch.enableAutoInsert(true);
        textSearch.setContentAssistProposals(getContentAssistProposals());
        textSearch.setToolTipText(Messages.ButtonTooltip_Text_tooltip);

        buttonUp = WidgetFactory.createPushButton(textSearchArea);
        buttonUp.setImage(ISphereJobTraceExplorerCorePlugin.getDefault().getImage(ISphereJobTraceExplorerCorePlugin.IMAGE_SEARCH_UP));
        buttonUp.setToolTipText(Messages.ButtonTooltip_Search_up);
        buttonUp.addSelectionListener(new SearchUpSelectionListener());

        buttonDown = WidgetFactory.createPushButton(textSearchArea);
        buttonDown.setImage(ISphereJobTraceExplorerCorePlugin.getDefault().getImage(ISphereJobTraceExplorerCorePlugin.IMAGE_SEARCH_DOWN));
        buttonDown.setToolTipText(Messages.ButtonTooltip_Search_down);
        buttonDown.addSelectionListener(new SearchDownSelectionListener());

        DisplaySQLHelpListener.createLabel(textSearchArea);

        final Label dummy = new Label(textSearchArea, SWT.NONE);
        dummy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dummy.setVisible(false);
    }

    private void createButtons(Composite parent) {

        Composite buttonsArea = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = createGridLayout(0, true);
        buttonsArea.setLayout(gridLayout);
        buttonsArea.setLayoutData(new GridData(GridData.CENTER));

        createButtonSpacer(buttonsArea);
    }

    private void createButtonSpacer(Composite parent) {

        Composite spacer = new Composite(parent, SWT.NONE);
        GridData layoutData = new GridData();
        layoutData.heightHint = 0;
        spacer.setLayoutData(layoutData);

        GridLayout layout = (GridLayout)parent.getLayout();
        layout.numColumns++;
    }

    private ContentAssistProposal[] getContentAssistProposals() {

        List<ContentAssistProposal> proposals = JobTraceEntry.getContentAssistProposals();

        return proposals.toArray(new ContentAssistProposal[proposals.size()]);
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        setChildrenEnabled(getChildren(), enabled);
    }

    public void addFilterChangedListener(SelectionListener listener) {

        filterChangedListeners.add(listener);
    }

    public void removeFilterChangedListener(ISelectionChangedListener listener) {

        filterChangedListeners.remove(listener);
    }

    private void notifyFilterChangedListeners(SelectionEvent event) {

        for (SelectionListener listener : filterChangedListeners) {
            listener.widgetSelected(event);
        }
    }

    private void setChildrenEnabled(Control[] children, boolean enabled) {

        for (Control child : children) {
            if (child instanceof Composite) {
                Composite composite = (Composite)child;
                composite.setEnabled(enabled);
                setChildrenEnabled(composite.getChildren(), enabled);
            }
        }
    }

    private class SearchUpSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobTraceExplorerFilterPanelEvents.SEARCH_UP;
            event.data = null;
            event.text = textSearch.getText();
            notifyFilterChangedListeners(event);
        }
    }

    private class SearchDownSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent event) {

            event.detail = JobTraceExplorerFilterPanelEvents.SEARCH_DOWN;
            event.data = null;
            event.text = textSearch.getText();
            notifyFilterChangedListeners(event);
        }
    }
}
