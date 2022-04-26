/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.StreamFile;

public class CompareStreamFileInput extends CompareEditorInput implements IFileEditorInput {

    public static class MyDiffNode extends DiffNode {
        public MyDiffNode(IDiffContainer parent, int kind, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
            super(parent, kind, ancestor, left, right);
        }

        @Override
        public void fireChange() {
            super.fireChange();
        }
    }

    private boolean editable;
    private boolean threeWay;
    private boolean considerDate;
    private boolean ignoreCase;
    private boolean hasCompareFilters;
    private StreamFile ancestorStreamFile;
    private StreamFile leftStreamFile;
    private StreamFile rightStreamFile;
    private CompareStreamFileNode fAncestor;
    private CompareStreamFileNode fLeft;
    private CompareStreamFileNode fRight;
    private Object fRoot;

    // private boolean isSaveNeeded = false;

    public CompareStreamFileInput(CompareEditorConfiguration config, StreamFile ancestorStreamFile, StreamFile leftStreamFile, StreamFile rightStreamFile) {
        super(config);
        this.editable = config.isLeftEditable();
        this.threeWay = config.isThreeWay();
        this.considerDate = config.isConsiderDate();
        this.ignoreCase = config.isIgnoreCase();
        this.hasCompareFilters = config.hasCompareFilters();
        this.ancestorStreamFile = ancestorStreamFile;
        this.leftStreamFile = leftStreamFile;
        this.rightStreamFile = rightStreamFile;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            monitor.beginTask(Messages.Downloading_stream_files, IProgressMonitor.UNKNOWN);
            if (threeWay) {
                ancestorStreamFile.download(monitor);
                IResource fAncestorResource = ancestorStreamFile.getLocalResource();
                fAncestorResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                fAncestor = new CompareStreamFileNode(fAncestorResource, considerDate, ignoreCase, hasCompareFilters);
            }

            leftStreamFile.download(monitor);
            IResource fLeftResource = leftStreamFile.getLocalResource();
            fLeftResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            fLeft = new CompareStreamFileNode(fLeftResource, considerDate, ignoreCase, hasCompareFilters);

            rightStreamFile.download(monitor);
            IResource fRightResource = rightStreamFile.getLocalResource();
            fRightResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            fRight = new CompareStreamFileNode(fRightResource, considerDate, ignoreCase, hasCompareFilters);

            monitor.beginTask(Messages.Comparing_stream_files, IProgressMonitor.UNKNOWN);
            CompareStreamFileDifferencer d;
            if (editable) {
                d = new CompareStreamFileDifferencer(getConfiguration()) {
                    @Override
                    protected Object visit(Object data, int result, Object ancestor, Object left, Object right) {
                        return new MyDiffNode((IDiffContainer)data, result, (ITypedElement)ancestor, (ITypedElement)left, (ITypedElement)right);
                    }
                };
            } else {
                d = new CompareStreamFileDifferencer(getConfiguration());
            }

            if (threeWay) {
                fRoot = d.findDifferences(true, monitor, null, fAncestor, fLeft, fRight);
            } else {
                fRoot = d.findDifferences(false, monitor, null, null, fLeft, fRight);
            }

            if (fRoot == null) {
                cleanup();
            } else {
                if (editable) {
                    addIgnoreFile();
                }
            }

            return fRoot;

        } catch (Exception e) {
            ISpherePlugin.logError(Messages.Unexpected_Error, e);
            String message = ExceptionHelper.getLocalizedMessage(e);
            throw new RuntimeException(message, e);
        } finally {
            monitor.done();
        }
    }

    public void cleanup() {
        if (threeWay && fAncestor != null) {
            File ancestorTemp = fAncestor.getTempFile(ignoreCase);
            if (ancestorTemp != null) {
                try {
                    ancestorTemp.delete();
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not delete temporary ancestor file ***", e); //$NON-NLS-1$
                }
            }
        }
        if (fLeft != null) {
            File leftTemp = fLeft.getTempFile(ignoreCase);
            if (leftTemp != null) {
                try {
                    leftTemp.delete();
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not delete temporary left file ***", e); //$NON-NLS-1$
                }
            }
        }
        if (fRight != null) {
            File rightTemp = fRight.getTempFile(ignoreCase);
            if (rightTemp != null) {
                try {
                    rightTemp.delete();
                } catch (Exception e) {
                    ISpherePlugin.logError("*** Could not delete temporary right file ***", e); //$NON-NLS-1$
                }
            }
        }
    }

    @Override
    public void saveChanges(IProgressMonitor pm) throws CoreException {
        // isSaveNeeded = false;
        super.saveChanges(pm);
        try {
            fLeft.commit(pm);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not commit changes of left file ***", e); //$NON-NLS-1$
        }
        try {
            leftStreamFile.upload(pm);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not upload left file ***", e); //$NON-NLS-1$
        }
        fLeft.refreshTempFile();
        ((MyDiffNode)fRoot).fireChange();
    }

    public void addIgnoreFile() {
        leftStreamFile.addIgnoreFile();
        try {
            leftStreamFile.openStream();
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not open stream of left file ***", e); //$NON-NLS-1$
        }
    }

    public void removeIgnoreFile() {
        leftStreamFile.removeIgnoreFile();
        try {
            leftStreamFile.closeStream();
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not close stream of left file ***", e); //$NON-NLS-1$
        }
    }

    public StreamFile getAncestor() {
        return ancestorStreamFile;
    }

    public StreamFile getLeft() {
        return leftStreamFile;
    }

    public StreamFile getRight() {
        return rightStreamFile;
    }

    public IFile getFile() {
        return leftStreamFile.getLocalResource();
    }

    public IStorage getStorage() throws CoreException {
        return leftStreamFile.getLocalResource();
    }

    public CompareEditorConfiguration getConfiguration() {
        return (CompareEditorConfiguration)super.getCompareConfiguration();
    }

}
