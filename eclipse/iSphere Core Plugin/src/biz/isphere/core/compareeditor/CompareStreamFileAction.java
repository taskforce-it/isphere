/*******************************************************************************
 * Copyright (c) 2012-2022 iSphere Project Owners
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

import biz.isphere.base.internal.UIHelper;
import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.annotations.CMOne;
import biz.isphere.core.internal.StreamFile;

public class CompareStreamFileAction {

    private ArrayList<CleanupListener> cleanupListener = new ArrayList<CleanupListener>();
    private CompareEditorConfiguration cc;
    private StreamFile ancestorStreamFile;
    private StreamFile leftStreamFile;
    private StreamFile rightStreamFile;
    private String editorTitle;
    private CompareStreamFileInput fInput;

    @CMOne(info = "Don`t change this constructor due to CMOne compatibility reasons")
    public CompareStreamFileAction(boolean editable, boolean threeWay, StreamFile ancestorStreamFile, StreamFile leftStreamFile,
        StreamFile rightStreamFile, String editorTitle) {

        this.cc = new SourceMemberCompareEditorConfiguration();
        cc.setLeftEditable(editable);
        cc.setRightEditable(false);
        cc.setThreeWay(threeWay);

        this.ancestorStreamFile = ancestorStreamFile;
        this.leftStreamFile = leftStreamFile;
        this.rightStreamFile = rightStreamFile;
        this.editorTitle = editorTitle;

    }

    public CompareStreamFileAction(CompareEditorConfiguration compareConfiguration, StreamFile ancestorStreamFile, StreamFile leftStreamFile,
        StreamFile rightStreamFile, String editorTitle) {
        this.cc = compareConfiguration;
        this.ancestorStreamFile = ancestorStreamFile;
        this.leftStreamFile = leftStreamFile;
        this.rightStreamFile = rightStreamFile;
        this.editorTitle = editorTitle;
    }

    public void run() {
        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {

                if (cc.isThreeWay() && (ancestorStreamFile == null || !ancestorStreamFile.exists())) {
                    MessageDialog.openError(getShell(), Messages.Compare_stream_files, Messages.Stream_file_not_found_colon_ANCESTOR);
                    return;
                }

                if (leftStreamFile == null) {
                    MessageDialog.openError(getShell(), Messages.Compare_stream_files, Messages.Stream_file_not_found_colon_LEFT);
                    return;
                } else {
                    // Retrieve name parts before exist(), because the file name
                    // is lost if the streamFile does not exist.
                    String directory = leftStreamFile.getDirectory();
                    String streamFile = leftStreamFile.getStreamFile();
                    if (!leftStreamFile.exists()) {
                        displayStreamFileNotFoundMessage(directory, streamFile);
                        return;
                    }
                }

                if (rightStreamFile == null) {
                    MessageDialog.openError(getShell(), Messages.Compare_stream_files, Messages.Stream_file_not_found_colon_RIGHT);
                    return;
                } else {
                    // Retrieve name parts before exist(), because the file name
                    // is lost if the streamFile does not exist.
                    String directory = rightStreamFile.getDirectory();
                    String streamFile = rightStreamFile.getStreamFile();
                    if (!rightStreamFile.exists()) {
                        displayStreamFileNotFoundMessage(directory, streamFile);
                        return;
                    }
                }

                if (cc.isLeftEditable()) {
                    IEditorPart editor = findStreamFileInEditor(leftStreamFile);
                    if (editor != null) {
                        MessageDialog.openError(getShell(), Messages.Compare_stream_files,
                            Messages.bind(Messages.Stream_file_is_already_open_in_an_editor, leftStreamFile.getStreamFile()));
                        return;
                    }
                }

                UIHelper.getActivePage().addPartListener(new IPartListener() {
                    public void partClosed(IWorkbenchPart part) {
                        if (part instanceof EditorPart) {
                            EditorPart editorPart = (EditorPart)part;
                            if (editorPart.getEditorInput() == fInput) {
                                if (cc.isLeftEditable()) {
                                    fInput.removeIgnoreFile();
                                }
                                fInput.cleanup();
                                IWorkbenchPage workbenchPage = UIHelper.getActivePage();
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
                    cc.setAncestorLabel(createLabel(ancestorStreamFile));
                }

                cc.setLeftLabel(createLabel(leftStreamFile));
                cc.setRightLabel(createLabel(rightStreamFile));

                fInput = new CompareStreamFileInput(cc, ancestorStreamFile, leftStreamFile, rightStreamFile);
                if (editorTitle != null) {
                    fInput.setTitle(editorTitle);
                } else {
                    String title;
                    if (cc.isLeftEditable()) {
                        title = Messages.bind(Messages.CompareEditor_Compare_Edit,
                            new String[] { getQualifiedStreamFileName(leftStreamFile), getQualifiedStreamFileName(rightStreamFile) });
                    } else {
                        title = Messages.bind(Messages.CompareEditor_Compare,
                            new String[] { getQualifiedStreamFileName(leftStreamFile), getQualifiedStreamFileName(rightStreamFile) });
                    }
                    fInput.setTitle(title);
                }

                IEditorReference editorReference = findCompareEditor(leftStreamFile, rightStreamFile);
                if (editorReference != null) {

                    // TODO: make decision, what is better: close editor or
                    // restore part. Now the part is brought to front
                    // IEditorPart editorPart =
                    // editorReference.getEditor(false);
                    // editorPart.getEditorSite().getPage().closeEditor(editorPart,
                    // false);

                    UIHelper.getActivePage().activate(editorReference.getPart(false));
                    return;
                }

                CompareUI.openCompareEditorOnPage(fInput, UIHelper.getActivePage());
                for (int index = 0; index < cleanupListener.size(); index++) {
                    (cleanupListener.get(index)).cleanup();
                }

            }

            private String createLabel(StreamFile streamFile) {

                if (streamFile.getLabel() != null) {
                    return streamFile.getLabel();
                } else {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append(streamFile.getDirectory());
                    buffer.append("/");
                    buffer.append(streamFile.getStreamFile());
                    return buffer.toString();
                }

            }

            private Shell getShell() {
                return Display.getCurrent().getActiveShell();
            }

            private String getQualifiedStreamFileName(StreamFile streamFile) {
                return streamFile.getDirectory() + "/" + streamFile.getStreamFile();
            }

            private void displayStreamFileNotFoundMessage(String directory, String streamFile) {
                String message = biz.isphere.core.Messages.bind(biz.isphere.core.Messages.Stream_file_1_in_directory_0_not_found,
                    new Object[] { directory, streamFile });
                MessageDialog.openError(getShell(), Messages.Compare_stream_files, message);
            }
        });
    }

    protected static IEditorReference findCompareEditor(StreamFile left, StreamFile right) {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                for (IEditorReference editorReference : page.getEditorReferences()) {
                    try {
                        IEditorInput editorInput = editorReference.getEditorInput();

                        // FIXME: add FileEditorInput

                        if (editorInput instanceof CompareStreamFileInput) {
                            CompareStreamFileInput compareStreamFileInput = (CompareStreamFileInput)editorInput;
                            IFile leftStreamFile = compareStreamFileInput.getLeft().getLocalResource();
                            IFile rightStreamFile = compareStreamFileInput.getRight().getLocalResource();
                            if (leftStreamFile.equals(left.getLocalResource()) && rightStreamFile.equals(right.getLocalResource())
                                || leftStreamFile.equals(right.getLocalResource()) && rightStreamFile.equals(left.getLocalResource())) {
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

    protected static IEditorPart findStreamFileInEditor(StreamFile left) {
        IFile streamFile = left.getLocalResource();
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
                            if (((FileEditorInput)editorInput).getFile().equals(streamFile)) {
                                return editor;
                            }
                        }
                        if (editorInput instanceof CompareStreamFileInput) {
                            StreamFile editorStreamFile = ((CompareStreamFileInput)editorInput).getLeft();
                            if (editorStreamFile.getLocalResource().equals(left.getLocalResource())) {
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
