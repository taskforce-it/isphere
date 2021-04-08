/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.base.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.base.ISphereBasePlugin;
import biz.isphere.base.Messages;

public class Contributors extends PreferencePage implements IWorkbenchPreferencePage {

    public Contributors() {
        super();
        noDefaultAndApplyButton();
    }

    @Override
    public Control createContents(Composite parent) {

        Composite _container = new Composite(parent, SWT.NONE);
        _container.setLayout(new FillLayout(SWT.VERTICAL));

        ScrolledComposite sc = new ScrolledComposite(_container, SWT.H_SCROLL | SWT.V_SCROLL);

        Composite container = new Composite(sc, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        container.setLayout(gridLayout);

        createSectionTaskForce(container);
        createSeparator(container);
        createSectionTools400(container);
        createSeparator(container);
        createSectionTranslators(container);
        createSeparator(container);
        createSectionDocumentation(container);

        // Compute size
        Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);

        // Set the child as the scrolled content of the ScrolledComposite
        sc.setContent(container);

        // Set the minimum size
        sc.setMinSize(point.x, point.y);

        // Expand both horizontally and vertically
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        return _container;
    }

    private void createSectionTaskForce(Composite container) {

        createImage(container, ISphereBasePlugin.IMAGE_TASKFORCE);

        final Composite compositeAdress = createOneColumnComposite(container);
        createSimpleEntry(compositeAdress, "Task Force IT-Consulting GmbH");
        createSimpleEntry(compositeAdress, "Fallgatter 3");
        createSimpleEntry(compositeAdress, "44369 Dortmund");
        createSimpleEntry(compositeAdress, "Deutschland / Germany");

        final Composite compositeNumbers = createOneColumnComposite(container);
        createLabeledEntry(compositeNumbers, Messages.Telefon, "+49 (0) 231/28219967");
        createLabeledEntry(compositeNumbers, Messages.Telefax, "+49 (0) 231/28861681");

        final Composite compositeInternet = createOneColumnComposite(container);
        createLabeledEntry(compositeInternet, Messages.E_Mail, "info@taskforce-it.de");
        createInternetLink(compositeInternet, "www.taskforce-it.de");
    }

    private void createSectionTools400(Composite container) {

        createImage(container, ISphereBasePlugin.IMAGE_TOOLS400);

        createSimpleEntry(container, "Thomas Raddatz");

        final Composite compositeInternetTools400 = createOneColumnComposite(container);
        createLabeledEntry(compositeInternetTools400, Messages.E_Mail, "thomas.raddatz@tools400.de");
        createInternetLink(compositeInternetTools400, "www.tools400.de");
    }

    private void createSectionTranslators(Composite parent) {

        Composite translators = createTwoColumnComposite(parent);

        createContributorEntry(translators, "Dutch:", "Peter Colpaert");
        createContributorEntry(translators, null, "Wim Jongman", "remainsoftware.com");

        createContributorEntry(translators, "Italian:", "Nicola Brion");
        createContributorEntry(translators, null, "Marco Riva", "markonetools.it");
    }

    private void createSectionDocumentation(Composite parent) {

        Composite documentation = createTwoColumnComposite(parent);

        createContributorEntry(documentation, "Documentation:", "Buck Calabro");
        createContributorEntry(documentation, "Sam Lennon");
    }

    private Composite createOneColumnComposite(Composite parent) {

        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        composite.setLayout(new GridLayout());

        return composite;
    }

    private Composite createTwoColumnComposite(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        GridLayout layout = new GridLayout(2, true);
        layout.horizontalSpacing = 30;
        composite.setLayout(layout);

        return composite;
    }

    private void createImage(Composite parent, String image) {

        final Label iamgeLabel = new Label(parent, SWT.NONE);
        iamgeLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        iamgeLabel.setImage(ISphereBasePlugin.getDefault().getImageRegistry().get(image));
    }

    private void createInternetLink(Composite parent, String homepage) {

        final Link link = new Link(parent, SWT.NONE);
        link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        link.setText(Messages.Internet + ": <a href=\"" + homepage + "\">" + homepage + "</a>");
        link.addSelectionListener(new LinkSelectionListener());
    }

    private void createLabeledEntry(Composite parent, String headline, String text) {
        createSimpleEntry(parent, headline + ": " + text);
    }

    private void createSimpleEntry(Composite parent, String text) {

        final Label labelTools400Email = new Label(parent, SWT.NONE);
        labelTools400Email.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        labelTools400Email.setText(text);
    }

    private void createContributorEntry(Composite parent, String name) {
        createContributorEntry(parent, null, name, null);
    }

    private void createContributorEntry(Composite parent, String headline, String name) {
        createContributorEntry(parent, headline, name, null);
    }

    private void createContributorEntry(Composite parent, String headline, String name, String homepage) {

        if (headline == null) {
            Label filler = new Label(parent, SWT.NONE);
        } else {
            Label leftLabel = new Label(parent, SWT.NONE);
            leftLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
            leftLabel.setText(headline);
        }

        if (homepage == null) {
            Label rightLabel = new Label(parent, SWT.NONE);
            rightLabel.setText(name);
        } else {
            Link linkMarkonetools = new Link(parent, SWT.NONE);
            linkMarkonetools.setText("<a href=\"" + homepage + "\">" + name + "</a>");
            linkMarkonetools.addSelectionListener(new LinkSelectionListener());
        }
    }

    private void createSeparator(Composite container) {

        final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        labelSeparator1.setLayoutData(gd_labelSeparator1);
    }

    public void init(IWorkbench workbench) {
    }

}
