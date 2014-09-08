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
	private Object origin;

	public RSEFilterPool(String name, Object origin) {
		this.name = name;
		this.origin = origin;
	}

	public String getName() {
		return name;
	}

	public Object getOrigin() {
		return origin;
	}
	
}
