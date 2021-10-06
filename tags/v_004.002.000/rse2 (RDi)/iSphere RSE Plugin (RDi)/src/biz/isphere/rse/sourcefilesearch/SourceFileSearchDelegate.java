/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.model.SystemMessageObject;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.internal.exception.InvalidFilterException;
import biz.isphere.core.sourcefilesearch.AbstractSourceFileSearchDelegate;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.services.qsys.api.IQSYSSourceMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSObjectSubSystem;

public class SourceFileSearchDelegate extends AbstractSourceFileSearchDelegate {

    private IBMiConnection connection;

    public SourceFileSearchDelegate(Shell shell, IBMiConnection connection) {
        this(shell, connection, null);
    }

    public SourceFileSearchDelegate(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        super(shell, monitor);

        this.connection = connection;
    }

    protected String produceMemberFilterString(String library, String sourceFile, String sourceMember, String memberType) {

        ISeriesMemberFilterString memberFilterString = new ISeriesMemberFilterString();
        memberFilterString.setLibrary(library);
        memberFilterString.setFile(sourceFile);
        memberFilterString.setMember(sourceMember);
        memberFilterString.setMemberType(memberType);

        return memberFilterString.toString();
    }

    protected String produceLibraryFilterString(String library) {

        com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString objectFilterString = new ISeriesObjectFilterString();
        objectFilterString.setLibrary(library);
        objectFilterString.setObject("*"); //$NON-NLS-1$
        objectFilterString.setObjectType(ISeries.FILE);
        String attributes = "*FILE:PF-SRC *FILE:PF38-SRC"; //$NON-NLS-1$
        objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));

        return objectFilterString.toString();
    }

    protected Object[] resolveFilterString(String filterString) throws Exception {

        QSYSObjectSubSystem objectSubSystem = connection.getQSYSObjectSubSystem();
        return objectSubSystem.resolveFilterString(filterString, null);
    }

    protected void throwSystemErrorMessage(final Object message) throws InvalidFilterException {
        throw new InvalidFilterException(((SystemMessageObject)message).getMessage());
    }

    protected boolean isSystemMessageObject(Object object) {
        return (object instanceof SystemMessageObject);
    }

    protected boolean isLibrary(Object object) {
        return ResourceTypeUtil.isLibrary(object);
    }

    protected boolean isSourceFile(Object object) {
        return ResourceTypeUtil.isSourceFile(object);
    }

    protected boolean isSourceMember(Object object) {
        return ResourceTypeUtil.isSrcMember(object);
    }

    protected String getResourceLibrary(Object resource) {
        return ((IQSYSResource)resource).getLibrary();
    }

    protected String getResourceName(Object resource) {
        return ((IQSYSResource)resource).getName();
    }

    protected String getMemberResourceLibrary(Object resource) {
        return ((IQSYSMember)resource).getLibrary();
    }

    protected String getMemberResourceFile(Object resource) {
        return ((IQSYSMember)resource).getFile();
    }

    protected String getMemberResourceName(Object resource) {
        return ((IQSYSMember)resource).getName();
    }

    protected String getMemberResourceType(Object resource) {
        return ((IQSYSMember)resource).getType();
    }

    protected String getMemberResourceDescription(Object resource) {
        return ((IQSYSMember)resource).getDescription();
    }

    @Override
    protected Date getMemberLastChangedDate(Object resource) {
        return ((IQSYSSourceMember)resource).getDateModified();
    }
}
