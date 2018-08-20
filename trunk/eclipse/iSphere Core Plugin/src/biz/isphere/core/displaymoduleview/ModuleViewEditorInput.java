package biz.isphere.core.displaymoduleview;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.ReadOnlyEditor;
import biz.isphere.core.internal.api.debugger.moduleviews.DebuggerView;

public class ModuleViewEditorInput implements IEditorInput {

    private String systemName;
    private String program;
    private String library;
    private String objectType;
    private String module;
    private int viewId;

    private ModuleViewStorage storage;

    public ModuleViewEditorInput(String systemName, DebuggerView debuggerView, String[] lines) {

        this.systemName = systemName;
        this.program = debuggerView.getObject();
        this.library = debuggerView.getLibrary();
        this.objectType = debuggerView.getObjectType();
        this.module = debuggerView.getModule();
        this.viewId = debuggerView.getId();

        storage = new ModuleViewStorage(systemName, program, library, objectType, module, viewId, lines);
    }

    public IStorage getStorage() throws CoreException {
        return storage;
    }

    public boolean exists() {
        return false;
    }

    public String getName() {
        return storage.getFullQualifiedName();
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return storage.getFullQualifiedName();
    }

    public ImageDescriptor getImageDescriptor() {
        return ISpherePlugin.getImageDescriptor(ISpherePlugin.IMAGE_DISPLAY_MODULE_VIEW);
    }

    public String getContentDescription() {
        return storage.getFullQualifiedName();
    }

    public boolean isSameModuleView(IEditorInput editorInput) throws CoreException {

        if (editorInput == this) {
            return true;
        }

        if (!(editorInput instanceof ModuleViewEditorInput)) {
            return false;
        }

        ModuleViewEditorInput otherEditorInput = (ModuleViewEditorInput)editorInput;

        return getName().equals(otherEditorInput.getName());
    }

    public ReadOnlyEditor findEditor(IWorkbenchPage aPage) {
        if (aPage == null) {
            return null;
        }

        IEditorReference[] tEditors = aPage.getEditorReferences();
        for (IEditorReference tEditorReference : tEditors) {
            try {
                if (isSameModuleView(tEditorReference.getEditorInput())) {
                    IEditorPart tEditor = tEditorReference.getEditor(true);
                    if (tEditor instanceof ReadOnlyEditor) {
                        return (ReadOnlyEditor)tEditor;
                    }
                }
            } catch (Exception e) {
                ISpherePlugin.logError("Could not find 'ModuleViewEditor'.", e); //$NON-NLS-1$
            }
        }
        return null;
    }

    public Object getAdapter(Class arg0) {
        return null;
    }
}