package entities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;


@NodeEntity
public class Clase{
	@Id @GeneratedValue
	private Long id;
	private String name;
	private String qualifiedname;
	
	//Relationship Element as LinkedList
	private LinkedList<Metodo> methodsLN = null;
	
    private int totalMethodChildren=0;
	
	@Relationship(type = "OWNS_METHOD")
    Set<Metodo> methods = new HashSet<>();
	
	@Relationship(type="HAS_CLASS", direction = Relationship.INCOMING)
	Project project;
	
	@Relationship(type="CONTAINS_CLASS", direction = Relationship.INCOMING)
	Namespace namspace;
	
	
	
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getQualifiedname() {
		return qualifiedname;
	}
	
	public Set<Metodo> getMethods() {
		return methods;
	}
	public Project getProject() {
		return project;
	}
	public Namespace getNamspace() {
		return namspace;
	}
	
    public void setName(String name) {
        this.name = name;
    }

    /**
	 * Instantiates all the LinkedList 
	 * of the relationships as a new 
	 * LinkedList<T> with its corresponding
	 * set.
	 */
    public void changeAllToLN() {
    	this.methodsLN=new LinkedList<Metodo>(methods);
	}
	public LinkedList<Metodo> getMethodsLN() {
		return methodsLN;
	}
	public void setMethodsLN(LinkedList<Metodo> methodsLN) {
		this.methodsLN = methodsLN;
	}

	/**
	 * Gets the sum of the amount of dependencies
	 * that each method in the class has.
	 * @return the totalMethodChildren
	 */
	public int getTotalMethodChildren() {
		return totalMethodChildren;
	}
	/**
	 * @param totalMethodChildren the totalMethodChildren to set
	 */
	public void setTotalMethodChildren(int totalMethodChildren) {
		this.totalMethodChildren = totalMethodChildren;
	}

}
