/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.streamfilesearch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.subsystems.ifs.files.IFSFileFilterString;
import com.ibm.etools.iseries.subsystems.ifs.files.IFSRemoteFile;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.streamfilesearch.SearchElement;
import biz.isphere.core.streamfilesearch.StreamFileSearchFilter;

/**
 * This class produces a list of {@link SearchElement} elements from a mixed
 * list of all kind of objects around an filter of the <i>Remote Systems</i>
 * view.
 */
public class StreamFileSearchFilterResolver {

    public static final int MAX_DEPTH = 1;

    private Shell _shell;
    private IBMiConnection _connection;
    private StreamFileSearchFilter streamFileSearchFilter;

    private Map<String, SearchElement> _searchElements;
    private IFSFileFilterString _ifsFileFilterString;
    private StreamFileSearchDelegate _delegate;
    private IProgressMonitor monitor;

    public StreamFileSearchFilterResolver(Shell shell, IBMiConnection connection, StreamFileSearchFilter streamFileSearchFilter) {
        this(shell, connection, streamFileSearchFilter, null);
    }

    private StreamFileSearchFilterResolver(Shell shell, IBMiConnection connection, StreamFileSearchFilter streamFileSearchFilter,
        IProgressMonitor monitor) {
        this._shell = shell;
        this._connection = connection;
        this.streamFileSearchFilter = streamFileSearchFilter;
        this.monitor = monitor;
    }

    public Map<String, SearchElement> resolveRSEFilter(List<Object> _selectedElements) throws InterruptedException, Exception {

        _searchElements = new LinkedHashMap<String, SearchElement>();

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            if (isCanceled()) {
                break;
            }

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof IFSRemoteFile)) {

                IFSRemoteFile element = (IFSRemoteFile)_object;

                if (element.isDirectory()) {
                    addElementsFromDirectory(element);
                } else if (element.isFile()) {
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
            } else if ((_object instanceof IFSFileFilterString)) {

                IFSFileFilterString fileFilter = (IFSFileFilterString)_object;
                addElementsFromFilterString(fileFilter.toString());
            }

        }

        return _searchElements;
    }

    private void addElement(IFSRemoteFile element) {

        String key = SearchElement.produceKey(element.getParentName(), element.getName());

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setDirectory(element.getParentPath());
            _searchElement.setStreamFile(element.getName());
            _searchElements.put(key, _searchElement);

        }

    }

    private void addElementsFromDirectory(IFSRemoteFile element) throws InterruptedException, Exception {

        getStreamFilterString().setPath(element.getName());

        addElementsFromFilterString(_ifsFileFilterString.toString());
    }

    private void addElementsFromFilterString(String... filterStrings) throws InterruptedException, Exception {

        getStreamFileSearchDelegate().addElementsFromFilterString(_searchElements, streamFileSearchFilter, MAX_DEPTH, filterStrings);
    }

    private IFSFileFilterString getStreamFilterString() {

        if (_ifsFileFilterString == null) {
            _ifsFileFilterString = new IFSFileFilterString();
        }

        return _ifsFileFilterString;
    }

    private StreamFileSearchDelegate getStreamFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new StreamFileSearchDelegate(_shell, _connection, monitor);
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
