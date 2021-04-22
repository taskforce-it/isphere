/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;

import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;
import biz.isphere.core.internal.MessageDialogAsync;
import biz.isphere.core.search.SearchOptions;
import biz.isphere.core.sourcefilesearch.SearchElement;
import biz.isphere.core.sourcefilesearch.SearchExec;
import biz.isphere.core.sourcefilesearch.SearchPostRun;
import biz.isphere.core.sourcefilesearch.SourceFileSearchFilter;
import biz.isphere.rse.Messages;

public class RSESearchExec extends SearchExec {

    private IWorkbenchWindow _workbenchWindow;
    private IBMiConnection _connection;
    private Connection _jdbcConnection;

    public RSESearchExec(IWorkbenchWindow workbenchWindow, IBMiConnection connection) {

        this._workbenchWindow = workbenchWindow;
        this._connection = connection;

        this._jdbcConnection = IBMiHostContributionsHandler.getJdbcConnection(this._connection.getConnectionName());
    }

    public void resolveAndExecute(String library, String file, String member, SearchOptions searchOptions) {
        SearchPostRun postRun = createPostRun(_workbenchWindow, _connection, searchOptions);
        resolveAndExecute(library, file, member, searchOptions, postRun);
    }

    private void resolveAndExecute(String library, String file, String member, SearchOptions searchOptions, SearchPostRun postRun) {
        Job objectResolverJob = new ObjectResolverJob(_workbenchWindow, _connection, _jdbcConnection, searchOptions, library, file, member, postRun);
        objectResolverJob.setUser(true);
        objectResolverJob.schedule();
    }

    public void resolveAndExecute(ArrayList<Object> selectedElements, SearchOptions searchOptions) {
        SearchPostRun postRun = createPostRun(_workbenchWindow, _connection, searchOptions);
        resolveAndExecute(selectedElements, searchOptions, postRun);
    }

    private void resolveAndExecute(ArrayList<Object> selectedElements, SearchOptions searchOptions, SearchPostRun postRun) {
        Job filterResolverJob = new FilterResolverJob(_workbenchWindow, _connection, _jdbcConnection, searchOptions, selectedElements, postRun);
        filterResolverJob.setUser(true);
        filterResolverJob.schedule();
    }

    public void execute(ArrayList<SearchElement> filteredElements, SearchOptions searchOptions) {
        SearchPostRun postRun = createPostRun(_workbenchWindow, _connection, filteredElements, searchOptions);
        execute(filteredElements, searchOptions, postRun);
    }

    private void execute(ArrayList<SearchElement> filteredElements, SearchOptions searchOptions, SearchPostRun postRun) {
        execute(_connection.getConnectionName(), _jdbcConnection, searchOptions, filteredElements, postRun);
    }

    private SearchPostRun createPostRun(IWorkbenchWindow workbenchWindow, IBMiConnection connection, SearchOptions searchOptions) {
        return createPostRun(workbenchWindow, connection, new ArrayList<SearchElement>(), searchOptions);
    }

    private SearchPostRun createPostRun(IWorkbenchWindow workbenchWindow, IBMiConnection connection, ArrayList<SearchElement> searchElements,
        SearchOptions searchOptions) {

        SearchPostRun postRun = new SearchPostRun();
        postRun.setConnection(connection);
        postRun.setConnectionName(connection.getConnectionName());
        postRun.setSearchString(searchOptions.getCombinedSearchString());
        postRun.setSearchElements(new SourceFileSearchDelegate(workbenchWindow.getShell(), connection).createHashMap(searchElements));
        postRun.setWorkbenchWindow(workbenchWindow);

        return postRun;
    }

    private class FilterResolverJob extends Job {

        private IWorkbenchWindow workbenchWindow;
        private IBMiConnection connection;
        private Connection jdbcConnection;
        private SearchOptions searchOptions;
        private List<Object> selectedElements;
        private SearchPostRun postRun;

        public FilterResolverJob(IWorkbenchWindow workbenchWindow, IBMiConnection connection, Connection jdbcConnection, SearchOptions searchOptions,
            List<Object> selectedElements, SearchPostRun postRun) {
            super(biz.isphere.core.Messages.iSphere_Source_File_Search);

            this.workbenchWindow = workbenchWindow;
            this.connection = connection;
            this.jdbcConnection = jdbcConnection;
            this.searchOptions = searchOptions;
            this.selectedElements = selectedElements;
            this.postRun = postRun;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                monitor.beginTask(biz.isphere.core.Messages.Resolving_filters, 0);

                HashMap<String, SearchElement> searchElements = new SourceFileSearchFilterResolver(getShell(), connection, monitor)
                    .resolveFilterStrings(selectedElements);
                final ArrayList<SearchElement> filteredElements = new SourceFileSearchFilter().applyFilter(searchElements.values(), searchOptions);

                postRun.setSearchElements(searchElements);

                if (filteredElements.size() == 0) {
                    MessageDialogAsync.displayInformation(getShell(), biz.isphere.core.Messages.iSphere_Source_File_Search,
                        Messages.No_objects_found_that_match_the_selection_criteria);
                    return Status.OK_STATUS;
                }

                execute(connection.getConnectionName(), jdbcConnection, searchOptions, filteredElements, postRun);

                monitor.worked(1);

            } catch (Exception e) {
                MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
                return Status.OK_STATUS;
            } finally {
                monitor.done();
            }

            return Status.OK_STATUS;
        }

        private Shell getShell() {
            return workbenchWindow.getShell();
        }
    }

    private class ObjectResolverJob extends Job {

        private IWorkbenchWindow workbenchWindow;
        private IBMiConnection connection;
        private Connection jdbcConnection;
        private SearchOptions searchOptions;
        private String library;
        private String sourceFile;
        private String sourceMember;
        private SearchPostRun postRun;

        public ObjectResolverJob(IWorkbenchWindow workbenchWindow, IBMiConnection connection, Connection jdbcConnection, SearchOptions searchOptions,
            String library, String sourceFile, String sourceMember, SearchPostRun postRun) {
            super(biz.isphere.core.Messages.iSphere_Source_File_Search);

            this.workbenchWindow = workbenchWindow;
            this.connection = connection;
            this.jdbcConnection = jdbcConnection;
            this.searchOptions = searchOptions;
            this.library = library;
            this.sourceFile = sourceFile;
            this.sourceMember = sourceMember;
            this.postRun = postRun;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            try {

                monitor.beginTask(biz.isphere.core.Messages.Resolving_filters, 0);

                HashMap<String, SearchElement> searchElements = new HashMap<String, SearchElement>();

                SourceFileSearchDelegate delegate = new SourceFileSearchDelegate(getShell(), connection, monitor);
                delegate.addElements(searchElements, library, sourceFile, sourceMember);
                ArrayList<SearchElement> filteredElements = new SourceFileSearchFilter().applyFilter(searchElements.values(), searchOptions);

                postRun.setSearchElements(searchElements);

                if (filteredElements.size() == 0) {
                    MessageDialogAsync.displayInformation(getShell(), biz.isphere.core.Messages.iSphere_Source_File_Search,
                        Messages.No_objects_found_that_match_the_selection_criteria);
                    return Status.OK_STATUS;
                }

                execute(connection.getConnectionName(), jdbcConnection, searchOptions, filteredElements, postRun);

                monitor.worked(1);

            } catch (Exception e) {
                MessageDialogAsync.displayError(getShell(), ExceptionHelper.getLocalizedMessage(e));
                return Status.OK_STATUS;
            } finally {
                monitor.done();
            }

            return Status.OK_STATUS;
        }

        private Shell getShell() {
            return workbenchWindow.getShell();
        }
    }
}
