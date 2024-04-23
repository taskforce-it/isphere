package biz.isphere.core.preferencepages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.Messages;
import biz.isphere.core.preferences.Preferences;
import biz.isphere.core.swt.widgets.WidgetFactory;

public class ISphereSynchronizeMembers extends PreferencePage implements IWorkbenchPreferencePage {

    private Button btnDetachedEditor;
    private Button btnCenterOnScreen;
    private Button btnSideBySide;

    public ISphereSynchronizeMembers() {
        super();

        setPreferenceStore(ISpherePlugin.getDefault().getPreferenceStore());
        getPreferenceStore();
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public Control createContents(Composite parent) {

        Composite container = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout(2, false);
        container.setLayout(gridLayout);

        createSectionGlobal(container);

        setScreenToValues();

        return container;
    }

    private void createSectionGlobal(Composite parent) {

        Group grpEditors = new Group(parent, SWT.NONE);
        grpEditors.setLayout(new GridLayout(2, false));
        grpEditors.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        grpEditors.setText(Messages.Editors);

        Label labelDetachedEditor = new Label(grpEditors, SWT.NONE);
        labelDetachedEditor.setLayoutData(createLabelLayoutData());
        labelDetachedEditor.setText(Messages.Detach_editors_colon);
        labelDetachedEditor.setToolTipText(Messages.Tooltip_Specifies_whether_to_use_detached_editors_for_editing_and_viewing_members);

        btnDetachedEditor = WidgetFactory.createCheckbox(grpEditors);
        btnDetachedEditor.setToolTipText(Messages.Tooltip_Specifies_whether_to_use_detached_editors_for_editing_and_viewing_members);
        btnDetachedEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label labelCenterOnScreen = new Label(grpEditors, SWT.NONE);
        labelCenterOnScreen.setLayoutData(createLabelLayoutData());
        labelCenterOnScreen.setText(Messages.Center_on_screen_colon);
        labelCenterOnScreen.setToolTipText(Messages.Tooltip_Specifies_whether_to_center_the_editor_on_the_screen_or_application);

        btnCenterOnScreen = WidgetFactory.createCheckbox(grpEditors);
        btnCenterOnScreen.setToolTipText(Messages.Tooltip_Specifies_whether_to_center_the_editor_on_the_screen_or_application);
        btnCenterOnScreen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label labelSideBySide = new Label(grpEditors, SWT.NONE);
        labelSideBySide.setLayoutData(createLabelLayoutData());
        labelSideBySide.setText(Messages.Side_by_side_colon);
        labelSideBySide.setToolTipText(Messages.Tooltip_Specifies_whether_to_place_the_left_and_the_right_editors_side_by_side);

        btnSideBySide = WidgetFactory.createCheckbox(grpEditors);
        btnSideBySide.setToolTipText(Messages.Tooltip_Specifies_whether_to_place_the_left_and_the_right_editors_side_by_side);
        btnSideBySide.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    @Override
    protected void performApply() {
        super.performApply();
    }

    @Override
    protected void performDefaults() {
        setScreenToDefaultValues();
        super.performDefaults();
    }

    @Override
    public boolean performOk() {

        setStoreToValues();
        return super.performOk();
    }

    protected void setStoreToValues() {

        Preferences preferences = getPreferences();

        preferences.setSyncMembersEditorDetached(btnDetachedEditor.getSelection());
        preferences.setSyncMembersCenterOnScreen(btnCenterOnScreen.getSelection());
        preferences.setSyncMembersSideBySide(btnSideBySide.getSelection());
    }

    protected void setScreenToValues() {

        ISpherePlugin.getDefault();

        Preferences preferences = getPreferences();

        btnDetachedEditor.setSelection(preferences.isSyncMembersEditorDetached());
        btnCenterOnScreen.setSelection(preferences.isSyncMembersCenterOnScreen());
        btnSideBySide.setSelection(preferences.isSyncMembersSideBySide());

        setControlsEnablement();
    }

    protected void setScreenToDefaultValues() {

        Preferences preferences = getPreferences();

        btnDetachedEditor.setSelection(preferences.getDefaultSyncMembersEditorDetached());
        btnCenterOnScreen.setSelection(preferences.getDefaultSyncMembersCenterOnScreen());
        btnSideBySide.setSelection(preferences.getDefaultSyncMembersEditorSideBySide());

        setControlsEnablement();
    }

    private void setControlsEnablement() {

    }

    private GridData createLabelLayoutData() {
        return new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    }

    private Preferences getPreferences() {
        return Preferences.getInstance();
    }
}