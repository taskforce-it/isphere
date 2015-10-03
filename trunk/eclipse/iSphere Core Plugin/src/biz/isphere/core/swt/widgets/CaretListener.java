package biz.isphere.core.swt.widgets;

import java.util.EventListener;

public interface CaretListener extends EventListener {

    public abstract void caretMoved(CaretEvent caretevent);
}
