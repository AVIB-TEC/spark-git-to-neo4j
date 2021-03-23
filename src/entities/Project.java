package entities;


import java.util.LinkedList;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

//import database.DataSorter;

@NodeEntity
public class Project {

	@Id @GeneratedValue
	private Long id;
	private String name;	
	
	//Used for method tree
	private LinkedList<Clase> classesLN = null;
	
	@Relationship(type = "HAS_NAMESPACE")
    Set<Namespace> namespaces;
	
	@Relationship(type = "HAS_CLASS")
    Set<Clase> classes;
	
	@Relationship(type = "HAS_METHOD")
    Set<Metodo> methods;
	

	public void changeAllToLN() {
		//DataSorter dataSorter = new DataSorter();
		
		this.classesLN = new LinkedList<Clase>(classes);
		
		//namespacesLN=dataSorter.sortNamespace(namespacesLN);
		//classesLN = dataSorter.sortClase(classesLN);
		
	}
	
	
	//Sets and gets of DB
	public Long getId() {return id;}
	
	public String getName() {return name;}
	
	//Sets and Gets of method tree

	public LinkedList<Clase> getClassesLN() {return classesLN;}

	public void setClassesLN(LinkedList<Clase> classesLN) {this.classesLN = classesLN;}

	
}

