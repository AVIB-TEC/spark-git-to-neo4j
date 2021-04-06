package entities;

public class FileNode {
	private String name;
	private String qualifiedname;
	
	public FileNode(String name, String qualifiedname) {
		this.setName(name);
		this.setQualifiedname(qualifiedname);
	}

	public String getQualifiedname() {
		return qualifiedname;
	}

	public void setQualifiedname(String qualifiedname) {
		this.qualifiedname = qualifiedname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
