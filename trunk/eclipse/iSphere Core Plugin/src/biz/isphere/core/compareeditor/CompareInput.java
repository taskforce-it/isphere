/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IFileEditorInput;

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.internal.Member;
import biz.isphere.core.internal.MessageDialogAsync;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectLock;

/**
 * This class is used when RDi is the host application.
 * 
 * @see: {@link CompareInputWithSaveNeededHandling}
 */
public class CompareInput extends CompareEditorInput implements IFileEditorInput {

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
    private Member ancestorMember;
    private Member leftMember;
    private Member rightMember;
    private CompareNode fAncestor;
    private CompareNode fLeft;
    private CompareNode fRight;
    private Object fRoot;

    public CompareInput(CompareEditorConfiguration config, Member ancestorMember, Member leftMember, Member rightMember) {
        super(config);
        this.editable = config.isLeftEditable();
        this.threeWay = config.isThreeWay();
        this.considerDate = config.isConsiderDate();
        this.ignoreCase = config.isIgnoreCase();
        this.hasCompareFilters = config.hasCompareFilters();
        this.ancestorMember = ancestorMember;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {

            if (editable) {
                ISeriesHostObjectLock lock = leftMember.queryLocks();
                if (lock != null) {
                    MessageDialogAsync.displayError(leftMember.getMemberLockedMessages(lock));
                    editable = false;
                }
            }

            monitor.beginTask(Messages.Downloading_source_members, IProgressMonitor.UNKNOWN);

            if (threeWay) {
                ancestorMember.download(monitor);
                IResource fAncestorResource = ancestorMember.getLocalResource();
                fAncestorResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                fAncestor = new CompareNode(fAncestorResource, considerDate, ignoreCase, hasCompareFilters);
            }

            leftMember.download(monitor);
            IResource fLeftResource = leftMember.getLocalResource();
            fLeftResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            fLeft = new CompareNode(fLeftResource, considerDate, ignoreCase, hasCompareFilters);

            rightMember.download(monitor);
            IResource fRightResource = rightMember.getLocalResource();
            fRightResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            fRight = new CompareNode(fRightResource, considerDate, ignoreCase, hasCompareFilters);

            monitor.beginTask(Messages.Comparing_source_members, IProgressMonitor.UNKNOWN);
            CompareDifferencer d;
            if (editable) {
                d = new CompareDifferencer(getConfiguration()) {
                    @Override
                    protected Object visit(Object data, int result, Object ancestor, Object left, Object right) {
                        return new MyDiffNode((IDiffContainer)data, result, (ITypedElement)ancestor, (ITypedElement)left, (ITypedElement)right);
                    }
                };
            } else {
                d = new CompareDifferencer(getConfiguration());
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
        super.saveChanges(pm);

        try {
            fLeft.commit(pm);
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not commit changes of left file: (" + ExceptionHelper.getLocalizedMessage(e) + ") ***", e); //$NON-NLS-1$ //$NON-NLS-2$
            setDirty(true);
            throw new CoreException(new Status(Status.ERROR, ISpherePlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), e));
        }

        try {
            String error = leftMember.upload(pm);
            if (error != null) {
                throw new Exception(error);
            }
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not upload left file: (" + ExceptionHelper.getLocalizedMessage(e) + ") ***", e); //$NON-NLS-1$ //$NON-NLS-2$
            setDirty(true);
            throw new CoreException(new Status(Status.ERROR, ISpherePlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), e));
        }

        fLeft.refreshTempFile();
        ((MyDiffNode)fRoot).fireChange();
    }

    public void addIgnoreFile() {
        leftMember.addIgnoreFile();
        try {
            leftMember.openStream();
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not open stream of left file ***", e); //$NON-NLS-1$
        }
    }

    public void removeIgnoreFile() {
        leftMember.removeIgnoreFile();
        try {
            leftMember.closeStream();
        } catch (Exception e) {
            ISpherePlugin.logError("*** Could not close stream of left file ***", e); //$NON-NLS-1$
        }
    }

    public Member getAncestor() {
        return ancestorMember;
    }

    public Member getLeft() {
        return leftMember;
    }

    public Member getRight() {
        return rightMember;
    }

    public IFile getFile() {
        if (editable) {
            return leftMember.getLocalResource();
        } else {
            /*
             * Produce an invalid file name, so that Eclipse does not find the
             * member in any open editor.
             */
            return getBrowseModeFile();
        }
    }

    public IStorage getStorage() throws CoreException {
        if (editable) {
            return leftMember.getLocalResource();
        } else {
            /*
             * Produce an invalid file name, so that Eclipse does not find the
             * member in any open editor.
             */
            return getBrowseModeFile();
        }
    }

    private IFile getBrowseModeFile() {
        String osPath = leftMember.getLocalResource().getFullPath().toOSString();
        Path path = new Path(osPath + "(browse mode)"); //$NON-NLS-1$
        return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    }

    public CompareEditorConfiguration getConfiguration() {
        return (CompareEditorConfiguration)super.getCompareConfiguration();
    }

}
