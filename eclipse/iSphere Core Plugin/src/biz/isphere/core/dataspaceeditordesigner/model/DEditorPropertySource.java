package biz.isphere.core.dataspaceeditordesigner.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class DEditorPropertySource implements IPropertySource {

    private DEditor dEditor;
    private IPropertyDescriptor[] propertyDescriptors;

    private static final String PROPERTY_NAME = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.name";
    private static final String PROPERTY_DESCRIPTION = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.description";
    private static final String PROPERTY_COLUMNS = "biz.isphere.core.dataspaceeditordesigner.model.DEditor.columns";

    public DEditorPropertySource(DEditor dEditor) {
        this.dEditor = dEditor;
    }

    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (propertyDescriptors == null) {

            PropertyDescriptor sizeDescriptor = new PropertyDescriptor(PROPERTY_NAME, "Name");
            PropertyDescriptor textDescriptor = new PropertyDescriptor(PROPERTY_DESCRIPTION, "Description");
            PropertyDescriptor columnsDescriptor = new PropertyDescriptor(PROPERTY_COLUMNS, "Columns");

            propertyDescriptors = new IPropertyDescriptor[] { sizeDescriptor, textDescriptor, columnsDescriptor };
        }
        return propertyDescriptors;
    }

    public Object getPropertyValue(Object name) {
        if (name.equals(PROPERTY_NAME)) {
            return dEditor.getName();
        } else if (name.equals(PROPERTY_DESCRIPTION)) {
            return dEditor.getDescription();
        } else if (name.equals(PROPERTY_COLUMNS)) {
            return dEditor.getColumns();
        }
        return null;
    }

    public boolean isPropertySet(Object name) {
        return getPropertyValue(name) != null;
    }

    public void resetPropertyValue(Object arg0) {

    }

    public void setPropertyValue(Object arg0, Object arg1) {

    }
}
