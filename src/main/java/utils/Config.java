package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {

	private static final long serialVersionUID = 3975634262124767870L;
	private String workDir = System.getProperty("user.dir");
	private Properties prop = new Properties();
	private static Config instance = null; 
	
	private Config() {
		try {
			FileInputStream ip= new FileInputStream(workDir +  "\\src\\main\\resources\\config.properties");
			prop.load(ip); 
		} catch (FileNotFoundException e) {
			System.out.println("Error loading config file.");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("Error loading config file.");
			e.printStackTrace();
		}
	}
	
	public static Config getInstance() {
		if (instance == null) {
			synchronized (Config.class) {
				if(instance == null){
					instance = new Config();
		        }
		    }
		}
        return instance;
	}
	
	public String getProperty(String property){
		return prop.getProperty(property);
	}
	
}
