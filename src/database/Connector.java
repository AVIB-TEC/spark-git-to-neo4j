package database;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import entities.*;

public final class Connector {
	
	private SessionFactory sessionFactory=null;
	private Session session=null;
	private Configuration configuration=null;
	private String uri ;
	private String user;
	private String password;
	private String database;
	private String entitiesPackage;
	private static Connector instance = null;
	
	//Singleton
	private Connector() {}
	
	/**
	 * Double check Singleton
	 * @return
	 */
	public static Connector getInstance() {
		if(instance  == null) {
			synchronized (Connector .class) {
				if (instance==null) {
					instance = new Connector();
				}
			}
		}
		return instance;
	}
	
	private void connect() {
		loadConfig();
		System.out.println("Connecting to Neo4j ("+uri+")");
		if(configuration==null) {
			configuration = new Configuration.Builder()
	    		.uri(uri)
	    		.credentials(user, password)
	            .build();
		}
		sessionFactory = new SessionFactory(configuration, entitiesPackage);
		
	}
	
	private void loadConfig() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("resources/config.properties");

			// load a properties file
			prop.load(input);
			uri =prop.getProperty("uri");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			entitiesPackage = prop.getProperty("entitiesPackage");
			database = prop.getProperty("database");

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
	private void disconnect() {
		System.out.println("Closing the connection.");
		sessionFactory.close();
	}
	
	
	private void runQuery() {
		session = sessionFactory.openSession();
		sessionFactory.close();
	}
	
	/*
	public Collection<Project> getProjects() {
		connect();
        Collection<Project> projects = session.loadAll(Project.class);        
        disconnect();
        return projects;
	}
	
	public Collection<Namespace> getNamespaces() {
		connect();
        Collection<Namespace> namespaces = session.loadAll(Namespace.class);
        disconnect();
        return  namespaces;
	}
	
	public Collection<Clase> getClasses() {
		connect();
        Collection<Clase> classes = session.loadAll(Clase.class);
        disconnect();
        return classes;
	}
	
	public Collection<Metodo> getMethods() {
		connect();
        Collection<Metodo> methods = session.loadAll(Metodo.class);
        disconnect();
        return methods;
	}*/
	
	/*public void getEntityData(String entityName) {
		connect();
		
		Session session = sessionFactory.openSession();
        Collection<Namespace> allUsers = session.loadAll(Namespace.class);
        for (Iterator<Namespace> iterator = allUsers.iterator(); iterator.hasNext();) {
        	Namespace type = (Namespace) iterator.next();
        	if (type.getNamespace() != null) {
        		System.out.println(type.getNamespace().getQualifiednname());	
        	}	
        }
        disconnect();
	}*/
	
	/*public static void main(String[] args) {
		Connector conn = new Connector();
		//conn.getClass();
		//conn.getNamespaces();
		//conn.getProjects();
		conn.getMethods();
	}*/
}

