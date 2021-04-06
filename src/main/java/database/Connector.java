package database;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import entities.FileNode;

public class Connector implements AutoCloseable{
    private final Driver driver;
    private String uri ;
	private String user;
	private String password;
	private static Connector instance = null;
	
    public static Connector getInstance() {
		if(instance  == null) {
			synchronized (Connector.class) {
				if (instance==null) {
					instance = new Connector();
				}
			}
		}
		return instance;
	}
    
    private Connector() {
    	loadConfig();
    	//Must change to work with variables
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic("neo4j", "admin"));
    }
    
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
            System.out.println( String.format( "Got class with name: %s and qualifiedname: %s", record.get("c.name").asString(), record.get("c.qualifiedname").toString()));
        }
        return nodes;
    }
    
    
    
    
    

}