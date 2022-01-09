/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.rsemanagement.filter;

public class RSEFilterPool {
	
	private String name;
	private boolean _default;
	private Object origin;

	public RSEFilterPool(String name, boolean _default, Object origin) {
		this.name = name;
		this._default = _default;
		this.origin = origin;
	}

	public String getName() {
		return name;
	}

	public boolean isDefault() {
        return _default;
    }

    public Object getOrigin() {
		return origin;
	}
	
}
