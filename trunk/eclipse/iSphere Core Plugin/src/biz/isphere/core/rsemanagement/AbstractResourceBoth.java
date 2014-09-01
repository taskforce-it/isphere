package biz.isphere.core.rsemanagement;

public abstract class AbstractResourceBoth extends AbstractResource {

	private AbstractResource resourceWorkspace;
	private AbstractResource resourceRepository;

	public AbstractResourceBoth(AbstractResource resourceWorkspace, AbstractResource resourceRepository) {
		super(resourceWorkspace.isEditable() && resourceRepository.isEditable());
		this.resourceWorkspace = resourceWorkspace;
		this.resourceRepository = resourceRepository;
	}
	
	public AbstractResource getResourceWorkspace() {
		return resourceWorkspace;
	}

	public AbstractResource getResourceRepository() {
		return resourceRepository;
	}

}
