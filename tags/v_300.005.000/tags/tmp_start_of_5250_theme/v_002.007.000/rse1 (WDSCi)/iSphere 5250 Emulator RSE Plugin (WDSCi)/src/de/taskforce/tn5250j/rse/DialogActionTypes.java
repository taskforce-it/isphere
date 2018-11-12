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

package de.taskforce.tn5250j.rse;

public class DialogActionTypes {
	
	public static final int CREATE = 1;
	public static final int CHANGE = 2;
	public static final int COPY = 3;
	public static final int DELETE = 4;
	public static final int DISPLAY = 5;
	
	public static String getText(int actionType) {
		switch (actionType) {
			case CREATE: {
				return Messages.getString("CREATEX");
			}
			case CHANGE: {
				return Messages.getString("CHANGEX");
			}
			case COPY: {
				return Messages.getString("COPYX");
			}
			case DELETE: {
				return Messages.getString("DELETEX");
			}
			case DISPLAY: {
				return Messages.getString("DISPLAYX");
			}
		}
		return "";
	}
}
