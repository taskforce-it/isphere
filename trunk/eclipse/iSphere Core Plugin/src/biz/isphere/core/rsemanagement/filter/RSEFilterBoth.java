package biz.isphere.core.rsemanagement.filter;

import biz.isphere.core.rsemanagement.AbstractResourceBoth;

public class RSEFilterBoth extends AbstractResourceBoth {

	private String name;

	public RSEFilterBoth(String name, RSEFilter workspaceFilter, RSEFilter repositoryFilter) {
		super(workspaceFilter, repositoryFilter);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getKey() {
		return null;
	}

	@Override
	public String getValue() {
		return null;
	}
	
}
