package biz.isphere.bindingdirectoryeditor;

public class BindingDirectoryEntry {

	private String connection;
	private String library;
	private String object;
	private String objectType;
	private String activation;
	
	public BindingDirectoryEntry() {
		connection = "";
		library = "";
		object = "";
		objectType = "";
		activation = "";
	}

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getActivation() {
		return activation;
	}

	public void setActivation(String activation) {
		this.activation = activation;
	}
	
}
