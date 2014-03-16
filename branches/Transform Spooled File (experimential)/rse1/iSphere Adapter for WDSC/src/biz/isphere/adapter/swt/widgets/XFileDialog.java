package biz.isphere.adapter.swt.widgets;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.adapter.Messages;

public class XFileDialog  {
	
	private FileDialog fileDialog;

	private boolean overwrite;
	
	public XFileDialog(Shell aShell, int aStyle) {
		fileDialog = new FileDialog(aShell, aStyle);
	}

	public XFileDialog(Shell aShell) {
		fileDialog = new FileDialog(aShell);
	}
	
	public String open() {
		String tFileName = null;
		boolean tCanOverwrite = false;
		while (!tCanOverwrite) {
			tFileName = fileDialog.open();
			if (tFileName == null) {
				return null;
			}

			File tFile = new File(tFileName);
			if (tFile.exists()) {
				String tQuestion = Messages.bind(Messages.XFileDialog_OverwriteDialog_question, tFileName);
				MessageDialog tOverwriteDialog = new MessageDialog(fileDialog.getParent(),
						Messages.XFileDialog_OverwriteDialog_headline, null, tQuestion, MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL,
								IDialogConstants.CANCEL_LABEL }, 0);
				int tOverwrite = tOverwriteDialog.open();
				switch (tOverwrite) {
				case 0: // Yes
					tCanOverwrite = true;
					break;
				case 1: // No
					break;
				case 2: // Cancel
				default:
					return null;
				}
			} else {
				tCanOverwrite = true;
			}
		}
		return tFileName;
	}

	public void setOverwrite(boolean anOverwrite) {
		overwrite = anOverwrite;
	}

	public boolean getOverwrite() {
		return overwrite;
	}

	public void setText(String aText) {
		fileDialog.setText(aText);
	}

	public void setFileName(String aFileName) {
		fileDialog.setFileName(aFileName);
	}

	public void setFilterPath(String aFilterPath) {
		fileDialog.setFilterPath(aFilterPath);
	}

	public void setFilterExtensions(String[] aFilterExtensions) {
		fileDialog.setFilterExtensions(aFilterExtensions);
	}
}
