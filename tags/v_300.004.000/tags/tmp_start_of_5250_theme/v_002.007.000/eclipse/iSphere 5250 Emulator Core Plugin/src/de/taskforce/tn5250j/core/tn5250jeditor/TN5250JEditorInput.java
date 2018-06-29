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

package de.taskforce.tn5250j.core.tn5250jeditor;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TN5250JEditorInput implements IEditorInput {
	
	private String id;
	private String name;
	private String toolTip;
	private Image image;
	
	public TN5250JEditorInput(String id, String name, String toolTip, Image image) {
		this.id = id;
		this.name = name;
		this.toolTip = toolTip;
		this.image = image;
	}
	
	public TN5250JEditorInput(String id, String name) {
		this.id = id;
		this.name = name;
		toolTip = "";
		image = null;
	}

	public boolean exists() {
		return false;
	}

	public Image getImage() {
		return image;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return toolTip;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public int hashCode() {
		return id.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TN5250JEditorInput other = (TN5250JEditorInput)obj;
        if (!id.equals(other.id))
            return false;
        return true;
    }

	public String getId() {
		return id;
	}

}
