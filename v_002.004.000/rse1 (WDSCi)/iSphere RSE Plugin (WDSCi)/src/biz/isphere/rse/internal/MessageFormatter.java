package biz.isphere.rse.internal;

import biz.isphere.core.internal.AbstractMessageFormatter;
import biz.isphere.core.messagefileeditor.MessageDescription;

import com.ibm.etools.iseries.comm.interfaces.IISeriesMessageDescription;

public class MessageFormatter extends AbstractMessageFormatter {

    public String format(MessageDescription aMessageDescription) {
        return format(aMessageDescription.getMessage(), aMessageDescription.getHelpText());
    }

    public String format(IISeriesMessageDescription aMessageDescription) {
        return format(aMessageDescription.getText(), aMessageDescription.getHelp());
    }
}
