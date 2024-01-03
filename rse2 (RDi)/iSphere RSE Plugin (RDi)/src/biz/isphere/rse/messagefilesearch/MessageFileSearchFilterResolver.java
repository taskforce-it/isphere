/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.messagefilesearch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMessageFile;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.messagefilesearch.SearchElement;

/**
 * This class produces a list of {@link SearchElement} elements from a mixed
 * list of all kind of objects around an filter of the <i>Remote Systems</i>
 * view.
 */
public class MessageFileSearchFilterResolver {

    private Shell _shell;
    private IBMiConnection _connection;

    private Map<String, SearchElement> _searchElements;
    private ISeriesObjectFilterString _objectFilterString;
    private MessageFileSearchDelegate _delegate;
    private IProgressMonitor monitor;

    public MessageFileSearchFilterResolver(Shell shell, IBMiConnection connection) {
        this(shell, connection, null);
    }

    public MessageFileSearchFilterResolver(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        this._shell = shell;
        this._connection = connection;
        this.monitor = monitor;
    }

    public Map<String, SearchElement> resolveRSEFilter(List<Object> _selectedElements) throws InterruptedException, Exception {

        _searchElements = new LinkedHashMap<String, SearchElement>();

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            if (isCanceled()) {
                break;
            }

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof IQSYSResource)) {

                IQSYSResource element = (IQSYSResource)_object;

                if (ResourceTypeUtil.isLibrary(element)) {
                    addElementsFromLibrary(element);
                } else if (ResourceTypeUtil.isMessageFile(element)) {
                    addElement(element);
                }

            } else if ((_object instanceof SystemFilterReference)) {

                SystemFilterReference filterReference = (SystemFilterReference)_object;
                String[] _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                addElementsFromFilterString(_filterStrings);

            } else if ((_object instanceof ISystemFilterStringReference)) {

                ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                addElementsFromFilterString(_filterStrings);

            } else if ((_object instanceof ISystemFilter)) {

                ISystemFilter systemFilter = (ISystemFilter)_object;
                for (String filterString : systemFilter.getFilterStrings()) {
                    addElementsFromFilterString(filterString);
                }
            }

        }

        return _searchElements;
    }

    private void addElement(IQSYSResource element) {

        String library = element.getLibrary();
        String name = ((IQSYSMessageFile)element).getName();

        String key = library + "-" + name; //$NON-NLS-1$

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(element.getLibrary());
            _searchElement.setMessageFile(((IQSYSMessageFile)element).getName());
            _searchElement.setDescription(((IQSYSMessageFile)element).getDescription());
            _searchElements.put(key, _searchElement);

        }

    }

    private void addElementsFromLibrary(IQSYSResource element) throws InterruptedException, Exception {

        getObjectFilterString().setLibrary(element.getName());

        addElementsFromFilterString(_objectFilterString.toString());
    }

    private void addElementsFromFilterString(String... filterStrings) throws InterruptedException, Exception {

        getMessageFileSearchDelegate().addElementsFromFilterString(_searchElements, filterStrings);
    }

    private ISeriesObjectFilterString getObjectFilterString() {

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*"); //$NON-NLS-1$
            _objectFilterString.setObjectType(ISeries.FILE);
            String attributes = "*MSGF"; //$NON-NLS-1$
            _objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
        }

        return _objectFilterString;
    }

    private MessageFileSearchDelegate getMessageFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new MessageFileSearchDelegate(_shell, _connection, monitor);
        }

        return _delegate;
    }

    private boolean isCanceled() {

        if (monitor == null) {
            return false;
        }

        return monitor.isCanceled();
    }
}
