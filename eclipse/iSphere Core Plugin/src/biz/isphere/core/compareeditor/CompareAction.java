/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
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
import org.eclipse.swt.widgets.Shell;
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

import biz.isphere.base.internal.ExceptionHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.Member;

import com.ibm.etools.iseries.comm.interfaces.ISeriesHostObjectLock;

public class CompareAction {

    private ArrayList<CleanupListener> cleanupListener = new ArrayList<CleanupListener>();
    private CompareEditorConfiguration cc;
    private Member ancestorMember;
    private Member leftMember;
    private Member rightMember;
    private String editorTitle;
    private CompareInput fInput;

    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons. With CMOne NG 5.1.8 and higher this constructor will NO LONGER be used. In July 2019 this constructor can be removed.")
    public CompareAction(boolean editable, boolean considerDate, boolean threeWay, Member ancestorMember, Member leftMember, Member rightMember,
        String editorTitle) {
        
        boolean sequenceNumbersAndDateFields = true;
        if (!leftMember.hasSequenceNumbersAndDateFields()) {
            sequenceNumbersAndDateFields = false;
        }
        else {
            if (!rightMember.hasSequenceNumbersAndDateFields()) {
                sequenceNumbersAndDateFields = false;
            }
            else {
                if (threeWay && !ancestorMember.hasSequenceNumbersAndDateFields()) {
                    sequenceNumbersAndDateFields = false;
                }
            }
        }
        
        this.cc = new CompareEditorConfiguration();
        cc.setLeftEditable(editable);
        cc.setRightEditable(false);
        cc.setConsiderDate(considerDate);
        cc.setIgnoreCase(false);
        cc.setIgnoreChangesLeft(false);
        cc.setIgnoreChangesRight(false);
        cc.setThreeWay(threeWay);
        cc.setDropSequenceNumbersAndDateFields(!sequenceNumbersAndDateFields);
        
        this.ancestorMember = ancestorMember;
        this.leftMember = leftMember;
        this.rightMember = rightMember;
        this.editorTitle = editorTitle;

    }

    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons. With CMOne NG 5.1.8 and higher this constructor will be used.")
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

                if (cc.isLeftEditable()) {
                    ISeriesHostObjectLock lock;
                    try {
                        lock = leftMember.queryLocks();
                        if (lock != null) {
                            throw new Exception(leftMember.getMemberLockedMessages(lock));
                        }
                    } catch (Exception e) {
                        MessageDialog.openError(getShell(), Messages.Compare_source_members, ExceptionHelper.getLocalizedMessage(e));
                        return;
                    }
                }

                if (cc.isThreeWay() && (ancestorMember == null || !ancestorMember.exists())) {
                    MessageDialog.openError(getShell(), Messages.Compare_source_members, Messages.Member_not_found_colon_ANCESTOR);
                    return;
                }

                if (leftMember == null) {
                    MessageDialog.openError(getShell(), Messages.Compare_source_members, Messages.Member_not_found_colon_LEFT);
                    return;
                } else {
                    // Retrieve name parts before exist(), because the file name
                    // is lost if the member does not exist.
                    String library = leftMember.getLibrary();
                    String file = leftMember.getSourceFile();
                    String member = leftMember.getMember();
                    if (!leftMember.exists()) {
                        displayMemberNotFoundMessage(library, file, member);
                        return;
                    }
                }

                if (rightMember == null) {
                    MessageDialog.openError(getShell(), Messages.Compare_source_members, Messages.Member_not_found_colon_RIGHT);
                    return;
                } else {
                    // Retrieve name parts before exist(), because the file name
                    // is lost if the member does not exist.
                    String library = rightMember.getLibrary();
                    String file = rightMember.getSourceFile();
                    String member = rightMember.getMember();
                    if (!rightMember.exists()) {
                        displayMemberNotFoundMessage(library, file, member);
                        return;
                    }
                }

                if (cc.isLeftEditable()) {
                    IEditorPart editor = findMemberInEditor(leftMember);
                    if (editor != null) {
                        MessageDialog.openError(getShell(), Messages.Compare_source_members,
                            Messages.bind(Messages.Member_is_already_open_in_an_editor, leftMember.getMember()));
                        return;
                    }
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

                if (ISpherePlugin.isSaveNeededHandling()) {
                    // executed when WDSCi is the host application
                    fInput = new CompareInputWithSaveNeededHandling(cc, ancestorMember, leftMember, rightMember);
                } else {
                    // executed when RDi is the host application
                    fInput = new CompareInput(cc, ancestorMember, leftMember, rightMember);
                }

                if (editorTitle != null) {
                    fInput.setTitle(editorTitle);
                } else {
                    String title;
                    if (cc.isLeftEditable()) {
                        title = Messages.bind(Messages.CompareEditor_Compare_Edit, new String[] { getQualifiedMemberName(leftMember),
                            getQualifiedMemberName(rightMember) });
                    } else {
                        title = Messages.bind(Messages.CompareEditor_Compare, new String[] { getQualifiedMemberName(leftMember),
                            getQualifiedMemberName(rightMember) });
                    }
                    fInput.setTitle(title);
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

            private Shell getShell() {
                return Display.getCurrent().getActiveShell();
            }

            private String getQualifiedMemberName(Member member) {

                StringBuilder buffer = new StringBuilder();

                buffer.append(member.getLibrary());
                buffer.append("/");
                buffer.append(member.getSourceFile());
                buffer.append("(");
                buffer.append(member.getMember());
                buffer.append(")");

                return buffer.toString();
            }

            private void displayMemberNotFoundMessage(String library, String file, String member) {
                String message = biz.isphere.core.Messages.bind(biz.isphere.core.Messages.Member_2_of_file_1_in_library_0_not_found, new Object[] {
                    library, file, member });
                MessageDialog.openError(getShell(), Messages.Compare_source_members, message);
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
