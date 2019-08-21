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

package de.taskforce.tn5250j.rse.model;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.rse.ui.view.AbstractSystemRemoteAdapterFactory;
import org.eclipse.rse.ui.view.ISystemViewElementAdapter;
import org.eclipse.ui.views.properties.IPropertySource;

public class RSESessionAdapterFactory extends AbstractSystemRemoteAdapterFactory implements IAdapterFactory {
	
	private RSESessionAdapter rseSessionAdapter = new RSESessionAdapter();

	public RSESessionAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		ISystemViewElementAdapter adapter = null;
		if (adaptableObject instanceof RSESession)
			adapter = rseSessionAdapter;
		if ((adapter != null) && (adapterType == IPropertySource.class))
			adapter.setPropertySourceInput(adaptableObject);
		return adapter;
	}
	
}
