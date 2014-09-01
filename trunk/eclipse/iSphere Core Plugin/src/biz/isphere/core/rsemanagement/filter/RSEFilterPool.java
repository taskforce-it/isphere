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
