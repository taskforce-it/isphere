package org.bac.gati.tools.journalexplorer.internals;

public class QualifiedName {

	public static String getName(String library, String objectName) {
		return library.trim() + '/' + objectName.trim();
	}
}
