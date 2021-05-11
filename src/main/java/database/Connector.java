package database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import static org.neo4j.driver.Values.parameters;

//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
import java.util.ArrayList;
//import java.util.Properties;

import entities.FileNode;
import main.Commit;

public class Connector implements AutoCloseable{
    private final Driver driver;
    //private String uri ;
	//private String user;
	//private String password;
	
    public Connector() {
    	//loadConfig();
    	//Use this line to run on the cluster
    	driver = GraphDatabase.driver( "bolt://13.72.82.9:7687", AuthTokens.basic("neo4j", "admin"));
    	//Uncomment this line to run using a contianer
    	//driver = GraphDatabase.driver( "bolt://host.docker.internal:7687", AuthTokens.basic("neo4j", "admin"));
    	//Uncomment this line to run locally.
        //driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic("neo4j", "admin"));
    }
    /*
    private void loadConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			// load a properties file
			input = new FileInputStream("resources/config.properties");
			prop.load(input);
			uri =prop.getProperty("uri");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			
			System.out.println(uri);
			System.out.println(user);
			System.out.println(password);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	*/
    
    @Override
    public void close() throws Exception{
        driver.close();
    }
    
    public ArrayList<FileNode> searchFiles(String id, String filename) {
    	ArrayList<FileNode>  result  =  new ArrayList<FileNode>();
    	try ( Session session = driver.session()){
    		result = session.readTransaction( tx -> searchFiles( tx, id, filename));
    		session.close();
   	 	}
    	return result;
    }
    
    private ArrayList<FileNode> searchFiles(final Transaction tx, final String id, final String filename) {
        Result result = tx.run( "MATCH (p:Project)-[:HAS_CLASS]->(c:Class) "
        		+ "WHERE p.id = $neo4j_project_id "
        		+ "AND c.name= $filename "
        		+ "RETURN c.name, c.qualifiedname", parameters("neo4j_project_id", id, "filename", filename ) );
        ArrayList<FileNode> nodes = new ArrayList<FileNode>();
        while (result.hasNext()){
            org.neo4j.driver.Record record = result.next();
            nodes.add(new FileNode(record.get("c.name").asString(), record.get("c.qualifiedname").asString()));
        }
        return nodes;
    }
    
    public void saveCommit(String id, Commit commit, FileNode file, String comment) {
    	
    	try ( Session session = driver.session()){
    		session.writeTransaction( tx -> saveCommit( tx, id, commit, file, comment));
    		session.close();
   	 	}
    }
    
    private Result saveCommit(final Transaction tx, String id, Commit commit, FileNode file, final String comment) {
    	final String commitId = commit.getId();
    	final String author = commit.getAuthor();
    	final String date = commit.getDate();
    	final String qualifiedname = file.getQualifiedname();
    	Result result = tx.run( " MATCH (p:Project{id:$neo4j_project_id})-[:HAS_CLASS]->(class:Class) " +
        		" WHERE class.qualifiedname = $qualifiedname " +
        		" MERGE (commit:Commit{id: $commitid, author: $author, " +
        		" date:$date, comment: $comment}) " +
        		" MERGE (p)-[:HAS_COMMIT]->(commit) " +
        		" MERGE (class)-[:CHANGED_IN]->(commit) " +
        		" ON CREATE SET class.changes_count = 1 " +
        		" ON MATCH SET class.changes_count = class.changes_count + 1 ",
        		parameters("neo4j_project_id", id, "qualifiedname", qualifiedname,
        			   "commitid", commitId, "author", author, "date", date,
        			   "comment", comment)
        	);
        while (result.hasNext()){
           result.next();
        }
        return null;
    }
    
    public void commitFileRelationship(String id, FileNode current, FileNode compareFile) {
    	
    	try ( Session session = driver.session()){
    		session.writeTransaction( tx -> commitFileRelationship( tx, id, current, compareFile));
    		session.close();
   	 	}
    }
    
    private Result commitFileRelationship(final Transaction tx, final String id, FileNode current, FileNode compareFile) {
    	final String fileQualifiedname = current.getQualifiedname();
    	final String tempQualifiedname = compareFile.getQualifiedname();
    	Result result = tx.run( "MATCH (p:Project{id: $id})-[:HAS_CLASS]->(c1:Class { "
    			+ "qualifiedname: $fileQualifiedname}), (p:Project{id: $id})-[:HAS_CLASS]->(c2:Class{ "
    			+ "qualifiedname: $tempQualifiedname}) "
    			+ "MERGE (c1)-[r:CO_EVOLVE]-(c2) "
    			+ "ON CREATE SET r.changes_count = 1 "
    			+ "ON MATCH SET r.changes_count = r.changes_count + 1;",
        		parameters("id", id, "fileQualifiedname", fileQualifiedname,
        			   "tempQualifiedname", tempQualifiedname)
        	);
        while (result.hasNext()){
           result.next();
        }
        return null;
    }
    
    
    
    
    

}