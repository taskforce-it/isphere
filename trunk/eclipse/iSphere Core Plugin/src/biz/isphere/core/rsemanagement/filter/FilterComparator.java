package biz.isphere.core.rsemanagement.filter;

import biz.isphere.core.rsemanagement.AbstractComparator;
import biz.isphere.core.rsemanagement.AbstractResource;

public class FilterComparator extends AbstractComparator {

	public FilterComparator(AbstractResource[] resourcesWorkspaceToCompare, AbstractResource[] resourcesRepositoryToCompare) {
		super(resourcesWorkspaceToCompare, resourcesRepositoryToCompare);
	}

	@Override
	protected AbstractResource getInstanceForBothDifferent(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
		return new RSEFilterBoth(((RSEFilter)resourceWorkspace).getName(), (RSEFilter)resourceWorkspace, (RSEFilter)resourceRepository);
	}

}
