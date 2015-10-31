// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {

	private static final String BUNDLE_NAME = "de.taskforce.tn5250j.core.messages";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private Messages() {
	}
	
	public static String getString(String key) {
		try {
				return RESOURCE_BUNDLE.getString(key);
		} 
		catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}
	
}
