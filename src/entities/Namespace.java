package entities;

import java.util.HashSet;
import java.util.LinkedList;

import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

//import database.DataSorter;

@NodeEntity
public class Namespace {
	@Id @GeneratedValue
	private Long id;
	private String name;
	private String qualifiedname;
	
	//Relationship Element as LinkedList
	private LinkedList<Clase> classesLN = null;
	private LinkedList<Namespace> namespacesLN = null;
	private LinkedList<Metodo> methodsLN = null;
	
	//Attributes used for the tree
	 private int nClasses = 0;
	 
	//private LinkedList<_Class> classes  = new LinkedList<_Class>();
	 private Namespace parent = null;
	 private int namespacesClasses = 0;
	 private int childNamespaces = 0; //Cuantos hijos se deben dibujar
	 private int totalMethodChildren=0;
	 private int mostDepInClass=0;
	 
	@Relationship(type = "CONTAINS_NAMESPACE", direction  = Relationship.OUTGOING)
    Set<Namespace> namespaces = new HashSet<>();
	
	@Relationship(type = "CONTAINS_CLASS", direction  = Relationship.OUTGOING)
    Set<Clase> classes = new HashSet<>();;
	
	
	@Relationship(type = "CONTAINS_METHOD", direction  = Relationship.OUTGOING)
    Set<Metodo> methods = new HashSet<>();;
	
	@Relationship(type="HAS_NAMESPACE", direction = Relationship.INCOMING)
	Project project;
	
	@Relationship(type="CONTAINS_NAMESPACE", direction = Relationship.INCOMING)
	Namespace namespace;
	
	public int getChildNamespaces() {
		return childNamespaces;
	}

	public void setChildNamespaces(int childNamespaces) {
		this.childNamespaces = childNamespaces;
	}
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}

	/**
	 * Set of namespaces that belong to it.
	 * @return
	 */
	public Set<Namespace> getNamespaces() {
		return namespaces;
	}
	public Set<Clase> getClasses() {
		return classes;
	}
	public Set<Metodo> getMethods() {
		return methods;
	}
	public Project getProject() {
		return project;
	}
	/**
	 * Namespace to which it belongs.
	 * @return
	 */
	public Namespace getNamespace() {
		return namespace;
	}
	

    public void setName(String name) {this.name = name;}

    public int getNClasses() { return nClasses;}

    public void setNClasses(int nClasses) { this.nClasses = nClasses;}

    public String getQualifiedname() {
		return qualifiedname;
	}
	public void setQualifiedname(String qualifiedname) {
		this.qualifiedname = qualifiedname;
	}

    public Namespace getParent() { return parent;}

    public void setParent(Namespace parent) { this.parent = parent; }

    public int getNamespacesClasses() { return namespacesClasses; }

    public void setNamespacesClasses(int namespacesClasses) { this.namespacesClasses = namespacesClasses; }
	
	//Gets and Sets for Relationships as LN
	public LinkedList<Clase> getClassesLN() {
		return classesLN;
	}
	
	/**
	 * Instantiates all the LinkedList 
	 * of the relationships as a new 
	 * LinkedList<T> with its corresponding
	 * and sorts them alphabetically
	 * set.
	 
	public void changeAllToLN() {
		DataSorter dataSorter = new DataSorter();
		
		this.classesLN = new LinkedList<Clase>(classes);
		this.namespacesLN =new LinkedList<Namespace>(namespaces);
		this.methodsLN=new LinkedList<Metodo>(methods);
		
		namespacesLN=dataSorter.sortNamespace(namespacesLN);
		//classesLN = dataSorter.sortClase(classesLN);
		
		for (int i = 0; i < namespacesLN.size(); i++) {
			namespacesLN.get(i).setParent(this);
		}
		
	}*/
	
	public LinkedList<Namespace> getNamespacesLN() {
		return namespacesLN;
	}
	public void setNamespacesLN(LinkedList<Namespace> namespacesLN) {
		this.namespacesLN = namespacesLN;
	}
	public LinkedList<Metodo> getMethodsLN() {
		return methodsLN;
	}
	public void setMethodsLN(LinkedList<Metodo> methodsLN) {
		this.methodsLN = methodsLN;
	}

	public void setTotalMethodChildren(int methodsInNamespaceCont) {
		this.totalMethodChildren = methodsInNamespaceCont;
	}
	public int getTotalMethodChildren() {
		return this.totalMethodChildren ;
	}

	public void setMostDepInClass(int mostDepInClass) {
		this.mostDepInClass = mostDepInClass;		
	}
	public int getMostDepInClass() {
		return this.mostDepInClass ;		
	}
}