package biz.isphere.core.swt.widgets;

import org.eclipse.swt.events.TypedEvent;

import biz.isphere.core.swt.widgets.internal.HexEditorInternal;

public class CaretEvent extends TypedEvent {

    private static final long serialVersionUID = -4351282007326910073L;

    public int caretOffset;

    public CaretEvent(HexEditorInternal e) {
        super(e);
        caretOffset = e.getCaretOffset();
    }

}
