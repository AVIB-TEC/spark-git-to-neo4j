package entities;

import java.util.LinkedList;
import java.util.Set;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Metodo {
	@Id @GeneratedValue
	private Long id;
	private String name;
	private int lines;
	private int cyclomatic; 
	private int constant;

	private int icrlmin;
	private int icrlmax;
	private int icrlavg;
	private int icrlsum;
	private int icrcmin;
	private int icrcmax;
	private int icrcavg;
	private int icrcsum;
	private int icflmin;
	private int icflmax;
	private int icflavg;
	private int icflsum;
	private int icfcmin;
	private int icfcmax;
	private int icfcavg;
	private int icfcsum;
	private int icrkmin;
	private int icrkmax;
	private int icrkavg;
	private int icrksum;
	private int icfkmin;
	private int icfkmax;
	private int icfkavg;
	private int icfksum;
	private int ismethod;
	private int iscollapsed;
	private int isrecursive;
	
	//Relationship Element as LinkedList
	private LinkedList<Metodo> methodsLN = null;
	private LinkedList<Metodo> parentMethods = new LinkedList<Metodo>();
	
	//Atributes used for the tree
	private float x1 = 0;
	private float x2 = 0;
	
	@Relationship(type = "CALLS")
    Set<Metodo> methods;
	
	@Relationship(type = "COLLAPSES")
    Set<Metodo> collapsedMethods;
	
	@Relationship(type="CALLS", direction = Relationship.INCOMING)
    Metodo method;

	@Relationship(type="COLLAPSES", direction = Relationship.INCOMING)
    Metodo methodColapsed;
	
	@Relationship(type="OWNS_METHOD", direction = Relationship.INCOMING)
    Clase clase;
	
	
	@Relationship(type="HAS_METHOD", direction = Relationship.INCOMING)
	Project project;
	
	@Relationship(type="CONTAINS_METHOD", direction = Relationship.INCOMING)
	Namespace namespace;
	
	//Gets for the attributes of the graph
	public Long getId() {return id;}
	public String getName() {return name;}
	public int getLines() {return lines;}
	public int getCyclomatic() {return cyclomatic;}
	public int getConstant() {return constant;}

	//Gets used for the tree
	public float getX1() {return x1;}
    public void setX1(float x1) {this.x1 = x1;}
    public float getX2() {return x2;}
    public void setX2(float x2) {this.x2 = x2;}
    public void setName(String name) {this.name = name;}
    public Clase getOwnerClass() {return clase;}
    public LinkedList<Metodo> getMethodsLN() {return methodsLN;}
	public void setMethodsLN(LinkedList<Metodo> methodsLN) {this.methodsLN = methodsLN;}
	
    /**
	 * Instantiates all the LinkedList 
	 * of the relationships as a new 
	 * LinkedList<T> with its corresponding
	 * set.
	 */
    public void changeAllToLN() {
    	if (methods == null) {
    		this.methodsLN=new LinkedList<Metodo>();
    	}else {
    		this.methodsLN=new LinkedList<Metodo>(methods);	
    	}
	}
    /**
     * Adds a parent to the current node.
     * @param parent
     */
    public void addparent(Metodo parent) {
    	this.parentMethods.add(parent);
    }
    
    public LinkedList<Metodo> getParentMethods() {return parentMethods;}
	public int getIcrlmin() {return icrlmin;}
	public int getIcrlmax() {return icrlmax;}
	public int getIcrlavg() {return icrlavg;}
	public int getIcrlsum() {return icrlsum;}
	public int getIcrcmin() {return icrcmin;}
	public int getIcrcmax() {return icrcmax;}
	public int getIcrcavg() {return icrcavg;}
	public int getIcrcsum() {return icrcsum;}
	public int getIcflmin() {return icflmin;}
	public int getIcflmax() {return icflmax;}
	public int getIcflavg() {return icflavg;}
	public int getIcflsum() {return icflsum;}
	public int getIcfcmin() {return icfcmin;}
	public int getIcfcmax() {return icfcmax;}
	public int getIcfcavg() {return icfcavg;}
	public int getIcfcsum() {return icfcsum;}
	public int getIcrkmin() {return icrkmin;}
	public int getIcrkmax() {return icrkmax;}
	public int getIcrkavg() {return icrkavg;}
	public int getIcrksum() {return icrksum;}
	public int getIcfkmin() {return icfkmin;}
	public int getIcfkmax() {return icfkmax;}
	public int getIcfkavg() {return icfkavg;}
	public int getIcfksum() {return icfksum;}
	public int getIsmethod() {return ismethod;}
	public int getIscollapsed() {return iscollapsed;}
	public int getIsrecursive() {return isrecursive;}	
}
