package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;

import utils.*;
public class Main {
	
	private String workDir = System.getProperty("user.dir");
	private static Config config = Config.getInstance();
	
	public static void main(String[] args) {
		
		Main main = new Main();
		
		File tempFile = new File(main.workDir + "\\public\\log.txt"); 
	    if (tempFile.delete()) { 
	      System.out.println("Deleted the file: " + tempFile.getName() );
	    } else {
	      System.out.println("Failed to delete the file. Couldn't find the file.");
	    }
	    
	    File file = new File(main.workDir + "\\public\\repo");
	    boolean bool = file.mkdir();
	    if(bool){
	    	System.out.println("Directory created successfully");
	    }else{
	    	System.out.println("Sorry couldn’t create specified directory");
	    }
	    //Clone repo
	    String cmd = "git clone "+ config.getProperty("repository_url") +" "+ main.workDir + "\\public\\\\repo";
	    main.executeCommand(cmd);
	    
	    //Get data
	    cmd = "git --git-dir=public\\\\repo\\\\.git log --stat --name-only >> "+ main.workDir+"\\"+config.getProperty("file_log_url");
	    main.executeCommand(cmd);
	    
	    //Delete repo
	    tempFile = new File("./public\\repo"); 
	    if (tempFile.delete()) { 
	      System.out.println("Deleted the file: " + tempFile.getName());
	    } else {
	      System.out.println("Failed to delete the file. Couldn't find the file.");
	    }
	    
	    main.getCommits();
	}
	
	public void getCommits() {
		try {
			  File tempFile = new File(this.workDir + "\\resources\\public\\log.txt"); 
		      Scanner myReader = new Scanner(tempFile);
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(data);
		      }
		      myReader.close();
		    } catch (FileNotFoundException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	
	public void executeCommand(String cmd) {
		try {
			System.out.println("Running cmd command: "+cmd);
		    Runtime run = Runtime.getRuntime();
		    Process pr = run.exec(cmd);
		    pr.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		    String line = "";
		    while ((line=buf.readLine())!=null) {
		    	System.out.println(line);
		    }
	    } catch (IOException e) {
			e.printStackTrace();
	    }
	    catch (InterruptedException e) {
	    	e.printStackTrace();
	    }
	}
}
