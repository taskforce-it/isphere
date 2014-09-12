/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.rsemanagement.filter;

import java.io.File;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.Messages;
import biz.isphere.core.rsemanagement.AbstractEntryDialog;

public abstract class AbstractFilterEntryDialog extends AbstractEntryDialog {

	private RSEFilterPool[] filterPools;
	private ComboViewer comboViewerFilterPool;
	private Combo comboFilterPool;
	
	private class FilterPoolLabelProvider extends LabelProvider {
		public String getText(Object element) {
			return ((RSEFilterPool)element).getName();
		}
		public Image getImage(Object element) {
			return null;
		}
	}

	private class FilterPoolContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			return filterPools;
		}
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
	
	public AbstractFilterEntryDialog(Shell parentShell) {
		super(parentShell);
	}
	
	protected String getTitle() {
		return Messages.RSE_Filter_Management;
	}

	protected String getRSESubject() {
		return Messages.RSE_Filters;
	}

	protected String getSubject() {
		return Messages.filters;
	}

	protected String getFileExtension() {
		return "rseflt";
	}

	protected boolean needWorkspaceArea() {
		return true;
	}

	protected void configureWorkspaceArea(Composite compositeWorkspace) {

		filterPools = getFilterPools();

		if (filterPools.length > 0) {
			
			compositeWorkspace.setLayout(new GridLayout(2, false));
			
			Label labelFilterPool = new Label(compositeWorkspace, SWT.NONE);
			labelFilterPool.setText(Messages.Filter_pool + ":");
			labelFilterPool.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
			comboViewerFilterPool = new ComboViewer(compositeWorkspace, SWT.READ_ONLY);
			comboViewerFilterPool.setLabelProvider(new FilterPoolLabelProvider());
			comboViewerFilterPool.setContentProvider(new FilterPoolContentProvider());
			comboViewerFilterPool.setInput(new Object());
			comboFilterPool = comboViewerFilterPool.getCombo();
			comboFilterPool.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			for (int idx = 0; idx < filterPools.length; idx++) {
			    if (!filterPools[idx].isDefault()) {
	                comboViewerFilterPool.setSelection(new StructuredSelection(filterPools[idx]), true);
	                break;
			    }
			}
			
		}
		
	}
	
	protected String checkWorkspaceArea() {
		if (filterPools.length == 0) {
			return Messages.No_filter_pools_available + ".";
		}
		else {
			return null;
		}
	};

	private RSEFilterPool getFilterPool() {
		if (filterPools.length > 0 && comboViewerFilterPool.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection)comboViewerFilterPool.getSelection();
			return (RSEFilterPool)structuredSelection.getFirstElement();
		}
		else {
			return null;
		}
	}
	
	protected void run() {
		
		String workspace = null;
		RSEFilterPool filterPool = null;
		RSEFilter[] filtersWorkspace = null;
		if (isEditBoth() || isEditWorkspace()) {
			filterPool = getFilterPool();
			if (filterPool != null) {
				workspace = filterPool.getName();
				filtersWorkspace = getFilters(filterPool);
			}
		}
		
		String repository = null;
		RSEFilter[] filtersRepository = null;
		if (isEditBoth() || isEditRepository()) {
			repository = getRepository();
			if (repository != null) {
                filtersRepository = restoreFiltersFromXML(new File(repository), filterPool);
			}
		}
		
		RSEFilter[] resourcesWorkspace = null;
		RSEFilter[] resourcesRepository = null; 
		RSEFilterBoth[] resourcesBothDifferent = null;
		RSEFilter[] resourcesBothEqual = null;

		if (filtersWorkspace != null && filtersWorkspace.length > 0 &&
				filtersRepository != null && filtersRepository.length > 0) {
			
			FilterComparator comparator = new FilterComparator(filtersWorkspace, filtersRepository);

			if (comparator.getResourcesWorkspace().size() > 0) {
				resourcesWorkspace = new RSEFilter[comparator.getResourcesWorkspace().size()];
				resourcesWorkspace = comparator.getResourcesWorkspace().toArray(resourcesWorkspace);
			}
			
			if (comparator.getResourcesRepository().size() > 0) {
				resourcesRepository = new RSEFilter[comparator.getResourcesRepository().size()];
				resourcesRepository = comparator.getResourcesRepository().toArray(resourcesRepository);
			}
			
			if (comparator.getResourcesBothDifferent().size() > 0) {
				resourcesBothDifferent = new RSEFilterBoth[comparator.getResourcesBothDifferent().size()];
				resourcesBothDifferent = comparator.getResourcesBothDifferent().toArray(resourcesBothDifferent);
			}
			
			if (comparator.getResourcesBothEqual().size() > 0) {
				resourcesBothEqual = new RSEFilter[comparator.getResourcesBothEqual().size()];
				resourcesBothEqual = comparator.getResourcesBothEqual().toArray(resourcesBothEqual);
			}
			
		}
		else if (filtersWorkspace != null && filtersWorkspace.length > 0) {
			resourcesWorkspace = filtersWorkspace;
		}
		else if (filtersRepository != null && filtersRepository.length > 0) {
			resourcesRepository = filtersRepository;
		}
				
		openEditingDialog(
					getShell(),
					isEditWorkspace(),
					isEditRepository(),
					isEditBoth(),
					workspace,
					repository,
					resourcesWorkspace, 
					resourcesRepository, 
					resourcesBothDifferent, 
					resourcesBothEqual);
				
	}

	protected boolean createEmptyRepository(File repository) {
        return saveFiltersToXML(repository, new RSEFilter[0]);
	}
	
	protected abstract RSEFilterPool[] getFilterPools();

	protected abstract RSEFilter[] getFilters(RSEFilterPool filterPool);

	protected abstract void openEditingDialog(
        Shell parentShell, 
        boolean editWorkspace, boolean editRepository, boolean editBoth, String workspace, String repository, 
        RSEFilter[] resourceWorkspace, RSEFilter[] resourceRepository, 
        RSEFilterBoth[] resourceBothDifferent, RSEFilter[] resourceBothEqual);

    protected abstract boolean saveFiltersToXML(File toFile, RSEFilter[] filters);

    protected abstract RSEFilter[] restoreFiltersFromXML(File fromFile, RSEFilterPool filterPool);
    
}
