/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.util.HashMap;
import java.util.List;

import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.rse.ui.messages.SystemMessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.sourcefilesearch.SearchElement;

public class SourceFileSearchFilterResolver {

    private Shell _shell;
    private IBMiConnection _connection;

    private HashMap<String, SearchElement> _searchElements;
    private ISeriesObjectFilterString _objectFilterString;
    private SourceFileSearchDelegate _delegate;

    public SourceFileSearchFilterResolver(Shell shell, IBMiConnection connection) {
        this._shell = shell;
        this._connection = connection;
    }

    public HashMap<String, SearchElement> resolveFilterStrings(List<Object> _selectedElements) {

        _searchElements = new HashMap<String, SearchElement>();

        boolean _continue = true;

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof IQSYSResource)) {

                IQSYSResource element = (IQSYSResource)_object;

                if (ResourceTypeUtil.isLibrary(element)) {
                    _continue = addElementsFromLibrary(element);
                } else if ((ResourceTypeUtil.isSourceFile(element))) {
                    addElementsFromSourceFile(element.getLibrary(), element.getName());
                } else if (ResourceTypeUtil.isMember(element)) {
                    addElement(element);
                }
                if (!_continue) {
                    break;
                }

            } else if ((_object instanceof SystemFilterReference)) {

                SystemFilterReference filterReference = (SystemFilterReference)_object;
                String[] _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                if (!addElementsFromFilterString(_filterStrings)) {
                    break;
                }

            } else if ((_object instanceof ISystemFilterStringReference)) {

                ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                if (!addElementsFromFilterString(_filterStrings)) {
                    break;
                }

            }

        }

        return _searchElements;
    }

    private void addElement(IQSYSResource element) {

        String library = element.getLibrary();
        String file = ((IQSYSMember)element).getFile();
        String member = element.getName();

        String key = library + "-" + file + "-" + member; //$NON-NLS-1$ //$NON-NLS-2$

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(element.getLibrary());
            _searchElement.setFile(((IQSYSMember)element).getFile());
            _searchElement.setMember(element.getName());
            _searchElement.setType(element.getType());
            _searchElement.setDescription(((IQSYSMember)element).getDescription());
            _searchElements.put(key, _searchElement);

        }

    }

    private void addElementsFromSourceFile(String library, String sourceFile) {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*"); //$NON-NLS-1$
        _memberFilterString.setMemberType("*"); //$NON-NLS-1$

        addElementsFromFilterString(_memberFilterString.toString());
    }

    private boolean addElementsFromLibrary(IQSYSResource element) {

        getObjectFilterString().setLibrary(element.getName());

        return addElementsFromFilterString(_objectFilterString.toString());
    }

    private boolean addElementsFromFilterString(String... filterStrings) {

        try {
            return getSourceFileSearchDelegate().addElementsFromFilterString(_searchElements, filterStrings);
        } catch (InterruptedException localInterruptedException) {
            return false;
        } catch (Exception e) {
            SystemMessageDialog.displayExceptionMessage(_shell, e);
            return false;
        }
    }

    private ISeriesObjectFilterString getObjectFilterString() {

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*"); //$NON-NLS-1$
            _objectFilterString.setObjectType(ISeries.FILE);
            String attributes = "*FILE:PF-SRC *FILE:PF38-SRC"; //$NON-NLS-1$
            _objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
        }

        return _objectFilterString;
    }

    private SourceFileSearchDelegate getSourceFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new SourceFileSearchDelegate(_shell, _connection);
        }

        return _delegate;
    }
}
