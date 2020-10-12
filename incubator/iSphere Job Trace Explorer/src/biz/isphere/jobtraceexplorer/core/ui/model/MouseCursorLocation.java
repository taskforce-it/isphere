package biz.isphere.jobtraceexplorer.core.ui.model;

import biz.isphere.jobtraceexplorer.core.model.JobTraceEntry;

public class MouseCursorLocation {

    private JobTraceEntry jobTraceEntry;
    private String columnName;
    private int columnIndexUI;

    public MouseCursorLocation(JobTraceEntry jobTraceEntry, String columnName, int columnIndexUI) {
        this.jobTraceEntry = jobTraceEntry;
        this.columnName = columnName;
        this.columnIndexUI = columnIndexUI;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(columnName);
        buffer.append("="); //$NON-NLS-1$
        buffer.append(jobTraceEntry.getValueForUi(columnName));

        return buffer.toString();
    }

    public JobTraceEntry getJobTraceEntry() {
        return jobTraceEntry;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnIndexUI() {
        return columnIndexUI;
    }
}
