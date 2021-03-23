package entities;


//Temp class for use in CloneLayout. 
public class CloneCode {
	
	private long id;
	private int lines;
	private int cloneType;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}
	
	public void setCloneType(int cloneType) {
		this.cloneType = cloneType;
	}
	
	public int getCloneType() {
		return cloneType;
	}
	
}

