/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.compareeditor;

import java.util.ArrayList;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.Member;

public class CompareAction {

    private ArrayList<CleanupListener> cleanupListener = new ArrayList<CleanupListener>();
    private CompareEditorConfiguration cc;
    private Member ancestorMember;
    private Member leftMember;
    private Member rightMember;
    private String editorTitle;
    private CompareInput fInput;

    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public CompareAction(boolean editable, boolean considerDate, boolean threeWay, Member ancestorMember, Member leftMember, Member rightMember,
        String editorTitle) {

        this.cc = new CompareEditorConfiguration();
        cc.setLeftEditable(editable);
        cc.setRightEditable(false);
        cc.setConsiderDate(considerDate);
        cc.setThreeWay(threeWay);

        this.ancestorMember = ancestorMember;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
        this.editorTitle = editorTitle;

    }

    public CompareAction(CompareEditorConfiguration compareConfiguration, Member ancestorMember, Member leftMember, Member rightMember,
        String editorTitle) {
        this.cc = compareConfiguration;
        this.ancestorMember = ancestorMember;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
        this.editorTitle = editorTitle;
    }

    public void run() {
        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {

                if (cc.isThreeWay() && (ancestorMember == null || !ancestorMember.exists())) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Compare_source_members,
                        Messages.Member_not_found_colon_ANCESTOR);
                    return;
                }

                if (leftMember == null || !leftMember.exists()) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Compare_source_members,
                        Messages.Member_not_found_colon_LEFT);
                    return;
                }

                if (rightMember == null || !rightMember.exists()) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Compare_source_members,
                        Messages.Member_not_found_colon_RIGHT);
                    return;
                }

                IEditorPart editor = findMemberInEditor(leftMember);
                if (editor != null) {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.Compare_source_members,
                        Messages.bind(Messages.Member_is_already_open_in_an_editor, leftMember.getMember()));
                    return;
                }

                ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {
                    public void partClosed(IWorkbenchPart part) {
                        if (part instanceof EditorPart) {
                            EditorPart editorPart = (EditorPart)part;
                            if (editorPart.getEditorInput() == fInput) {
                                if (cc.isLeftEditable()) {
                                    fInput.removeIgnoreFile();
                                }
                                fInput.cleanup();
                                IWorkbenchPage workbenchPage = ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                if (workbenchPage != null) {
                                    workbenchPage.removePartListener(this);
                                }
                            }
                        }
                    }

                    public void partActivated(IWorkbenchPart part) {
                    }

                    public void partBroughtToTop(IWorkbenchPart part) {
                    }

                    public void partDeactivated(IWorkbenchPart part) {
                    }

                    public void partOpened(IWorkbenchPart part) {
                    }
                });

                if (cc.isThreeWay()) {
                    if (ancestorMember.getLabel() != null) {
                        cc.setAncestorLabel(ancestorMember.getLabel());
                    } else {
                        cc.setAncestorLabel(ancestorMember.getLibrary() + "/" + ancestorMember.getSourceFile() + "(" + ancestorMember.getMember()
                            + ")");
                    }
                }

                if (leftMember.getLabel() != null) {
                    cc.setLeftLabel(leftMember.getLabel());
                } else {
                    cc.setLeftLabel(leftMember.getLibrary() + "/" + leftMember.getSourceFile() + "(" + leftMember.getMember() + ")");
                }

                if (rightMember.getLabel() != null) {
                    cc.setRightLabel(rightMember.getLabel());
                } else {
                    cc.setRightLabel(rightMember.getLibrary() + "/" + rightMember.getSourceFile() + "(" + rightMember.getMember() + ")");
                }

                fInput = new CompareInput(cc, ancestorMember, leftMember, rightMember);
                if (editorTitle != null) {
                    fInput.setTitle(editorTitle);
                } else {
                    fInput.setTitle(leftMember.getLibrary() + "/" + leftMember.getSourceFile() + "(" + leftMember.getMember() + ")");
                }

                IEditorReference editorReference = findCompareEditor(leftMember, rightMember);
                if (editorReference != null) {

                    // TODO: make decision, what is better: close editor or
                    // restore part. Now the part is brought to front
                    // IEditorPart editorPart =
                    // editorReference.getEditor(false);
                    // editorPart.getEditorSite().getPage().closeEditor(editorPart,
                    // false);

                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(editorReference.getPart(false));
                    return;
                }

                CompareUI.openCompareEditorOnPage(fInput, ISpherePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage());
                for (int index = 0; index < cleanupListener.size(); index++) {
                    (cleanupListener.get(index)).cleanup();
                }

            }
        });
    }

    protected static IEditorReference findCompareEditor(Member left, Member right) {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference editorReference : page.getEditorReferences()) {
                    try {
                        IEditorInput editorInput = editorReference.getEditorInput();

                        // FIXME: add FileEditorInput

                        if (editorInput instanceof CompareInput) {
                            CompareInput compareInput = (CompareInput)editorInput;
                            IFile leftMember = compareInput.getLeft().getLocalResource();
                            IFile rightMember = compareInput.getRight().getLocalResource();
                            if (leftMember.equals(left.getLocalResource()) && rightMember.equals(right.getLocalResource())
                                || leftMember.equals(right.getLocalResource()) && rightMember.equals(left.getLocalResource())) {
                                return editorReference;
                            }
                        }
                    } catch (Exception e) {
                        ISpherePlugin.logError("*** Could not the compare editor ***", e);
                    }
                }
            }
        }
        return null;
    }

    protected static IEditorPart findMemberInEditor(Member left) {
        IFile member = left.getLocalResource();
        IWorkbench workbench = ISpherePlugin.getDefault().getWorkbench();
        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
        IWorkbenchPage[] pages;
        IEditorReference[] editorRefs;
        IEditorPart editor;
        IEditorInput editorInput;
        for (int i = 0; i < windows.length; i++) {
            pages = windows[i].getPages();
            for (int x = 0; x < pages.length; x++) {
                editorRefs = pages[x].getEditorReferences();
                for (int refsIdx = 0; refsIdx < editorRefs.length; refsIdx++) {
                    editor = editorRefs[refsIdx].getEditor(false);
                    if (editor != null) {
                        editorInput = editor.getEditorInput();
                        if (editorInput instanceof FileEditorInput) {
                            if (((FileEditorInput)editorInput).getFile().equals(member)) {
                                return editor;
                            }
                        }
                        if (editorInput instanceof CompareInput) {
                            Member editorMember = ((CompareInput)editorInput).getLeft();
                            if (editorMember.getLocalResource().equals(left.getLocalResource())) {
                                return editor;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void addCleanupListener(CleanupListener cleanupListener) {
        this.cleanupListener.add(cleanupListener);
    }

    public void removeCleanupListener(CleanupListener cleanupListener) {
        this.cleanupListener.remove(cleanupListener);
    }

}
