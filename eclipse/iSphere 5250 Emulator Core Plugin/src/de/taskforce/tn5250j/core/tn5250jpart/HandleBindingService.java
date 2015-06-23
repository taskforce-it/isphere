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

package de.taskforce.tn5250j.core.tn5250jpart;

import java.util.ArrayList;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

public class HandleBindingService {

	private static ArrayList<ITN5250JPart> tn5250jParts = new ArrayList<ITN5250JPart>(); 

	public static void addTN5250JPart(ITN5250JPart tn5250jPart) {
		tn5250jParts.add(tn5250jPart);
	}
	
	public static void removeTN5250JPart(ITN5250JPart tn5250jPart) {
		tn5250jParts.remove(tn5250jPart);
		if (tn5250jParts.size() == 0) {
			setBindingService(true);
		}
	}
	
	public static void setBindingService(boolean state) {

		BindingService bindingService = (BindingService)PlatformUI.getWorkbench().getService(IBindingService.class);
		bindingService.setKeyFilterEnabled(state);

		for (int idx = 0; idx < tn5250jParts.size(); idx++) {
			tn5250jParts.get(idx).setBindingService(!state);
		}
		
	}
	
}
